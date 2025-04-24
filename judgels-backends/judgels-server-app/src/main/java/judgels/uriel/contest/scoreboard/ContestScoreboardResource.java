package judgels.uriel.contest.scoreboard;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import judgels.jophiel.JophielClient;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.info.UserInfo;
import judgels.jophiel.user.UserStore;
import judgels.jophiel.user.info.UserInfoStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.api.contest.problem.ContestProblem;
import judgels.uriel.api.contest.scoreboard.ContestScoreboard;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardConfig;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardResponse;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard.IcpcScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard.IoiScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.ScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.TrocScoreboard;
import judgels.uriel.api.contest.scoreboard.TrocScoreboard.TrocScoreboardEntry;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.log.ContestLogger;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.problem.ContestProblemStore;
import judgels.uriel.contest.submission.ContestSubmissionRoleChecker;
import liquibase.util.csv.CSVWriter;

@Path("/api/v2/contests/{contestJid}/scoreboard")
public class ContestScoreboardResource {
    private static final int PAGE_SIZE = 250;

    @Inject protected ActorChecker actorChecker;
    @Inject protected ContestStore contestStore;
    @Inject protected ContestProblemStore contestProblemStore;
    @Inject protected ContestLogger contestLogger;
    @Inject protected ContestModuleStore contestModuleStore;
    @Inject protected ContestSubmissionRoleChecker submissionRoleChecker;
    @Inject protected ContestScoreboardRoleChecker scoreboardRoleChecker;
    @Inject protected ContestScoreboardFetcher scoreboardFetcher;
    @Inject protected ContestScoreboardPoller scoreboardUpdaterDispatcher;
    @Inject protected ContestScoreboardStore contestScoreboardStore;
    @Inject protected ScoreboardIncrementalMarker scoreboardIncrementalMarker;
    @Inject protected UserStore userStore;
    @Inject protected UserInfoStore userInfoStore;
    @Inject protected JophielClient jophielClient;

    @Inject public ContestScoreboardResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public Optional<ContestScoreboardResponse> getScoreboard(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestJid") String contestJid,
            @QueryParam("frozen") boolean frozen,
            @QueryParam("topParticipantsOnly") @DefaultValue("false") boolean topParticipantsOnly,
            @QueryParam("showClosedProblems") boolean showClosedProblems,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(scoreboardRoleChecker.canViewDefault(actorJid, contest));

        boolean canManage = scoreboardRoleChecker.canManage(actorJid, contest);
        boolean canSupervise = scoreboardRoleChecker.canSupervise(actorJid, contest);
        boolean canViewOfficialAndFrozen = scoreboardRoleChecker.canViewOfficialAndFrozen(actorJid, contest);
        boolean canViewClosedProblems = scoreboardRoleChecker.canViewClosedProblems(actorJid, contest);
        boolean canViewSubmissions = submissionRoleChecker.canViewAll(contest);
        ContestScoreboardConfig config = new ContestScoreboardConfig.Builder()
                .canViewOfficialAndFrozen(canViewOfficialAndFrozen)
                .canViewClosedProblems(canViewClosedProblems)
                .canViewSubmissions(canViewSubmissions)
                .canRefresh(canManage)
                .build();
        ContestModulesConfig module = contestModuleStore.getConfig(contestJid, contest.getStyle());

        if (showClosedProblems) {
            checkAllowed(canSupervise);
        }

        contestLogger.log(contestJid, "OPEN_SCOREBOARD");

        return scoreboardFetcher
                .fetchScoreboard(contest, actorJid, canSupervise, frozen, topParticipantsOnly, showClosedProblems, pageNumber, PAGE_SIZE)
                .map(scoreboard -> {
                    var contestantJids = Lists.transform(scoreboard.getScoreboard().getContent().getEntries(), ScoreboardEntry::getContestantJid);
                    Map<String, Profile> profilesMap = jophielClient.getProfiles(contestantJids, contest.getBeginTime());

                    return new ContestScoreboardResponse.Builder()
                            .data(scoreboard)
                            .profilesMap(profilesMap)
                            .config(config)
                            .build();
                });
    }

    @GET
    @Path("/export")
    @Produces("application/x-download")
    @UnitOfWork(readOnly = true)
    public Response exportScoreboard(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            @QueryParam("frozen") boolean frozen,
            @QueryParam("topParticipantsOnly") @DefaultValue("false") boolean topParticipantsOnly,
            @QueryParam("showClosedProblems") @DefaultValue("false") boolean showClosedProblems,
            @QueryParam("page") @DefaultValue("1") int pageNumber,
            @QueryParam("pageSize") @DefaultValue("1000") int pageSize) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(scoreboardRoleChecker.canManage(actorJid, contest));

        ContestScoreboardExporter exporter = new ContestScoreboardExporter();
        String scoreboardResult;

        switch (contest.getStyle()) {
            case TROC:
                scoreboardResult = exporter.getScoreboardTroc(
                    actorJid,
                    contest,
                    true,
                    frozen,
                    topParticipantsOnly,
                    showClosedProblems,
                    pageNumber,
                    pageSize);
                break;
            case IOI:
                scoreboardResult = exporter.getScoreboardIOI(
                    actorJid,
                    contest,
                    true,
                    frozen,
                    topParticipantsOnly,
                    showClosedProblems,
                    pageNumber,
                    pageSize);
                break;
            case ICPC:
                scoreboardResult = exporter.getScoreboardIcpc(
                    actorJid,
                    contest,
                    true,
                    frozen,
                    topParticipantsOnly,
                    showClosedProblems,
                    pageNumber,
                    pageSize);
                break;
            case BUNDLE:
                scoreboardResult = exporter.getScoreboardBundle(
                    actorJid,
                    contest,
                    true,
                    frozen,
                    topParticipantsOnly,
                    showClosedProblems,
                    pageNumber,
                    pageSize);
                break;
            default:
                throw new RuntimeException("Unsupported contest style");
        }

        ResponseBuilder response = Response.ok(scoreboardResult);
        response.header("Access-Control-Expose-Headers", "Content-Disposition");
        response.header("Content-Disposition", "attachment; filename=\"scoreboard.csv\"");
        response.header("Content-Encoding", "csv");
        return response.build();
    }

    @POST
    @Path("/refresh")
    @UnitOfWork
    public void refreshScoreboard(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(scoreboardRoleChecker.canManage(actorJid, contest));

        scoreboardIncrementalMarker.invalidateMark(contestJid);
        scoreboardUpdaterDispatcher.updateContestAsync(contest);

        contestLogger.log(contestJid, "REFRESH_SCOREBOARD");
    }

    class ContestScoreboardExporter {
        public String getScoreboardTroc(
                String actorJid,
                Contest contest,
                boolean canSupervise,
                boolean frozen,
                boolean topParticipantsOnly,
                boolean showClosedProblems,
                int page,
                int pageSize) {
            List<ContestProblem> problems = contestProblemStore.getProblems(contest.getJid());

            StringWriter csv = new StringWriter();
            try (CSVWriter writer = new CSVWriter(csv)) {
                List<String> header = new ArrayList<>();
                header.add("User JID");
                header.add("NPM");
                header.add("Username");
                header.add("Email");
                header.add("Penalties");
                header.add("Total Points");
                for (ContestProblem problem : problems) {
                    header.add(problem.getAlias());
                }

                writer.writeNext(header.toArray(new String[0]), false);

                Optional<ContestScoreboard> maybeScoreboard = scoreboardFetcher
                        .fetchScoreboard(contest, actorJid, canSupervise, frozen, topParticipantsOnly, showClosedProblems, page, pageSize);

                if (maybeScoreboard.isEmpty()) {
                    return csv.toString();
                }

                ContestScoreboard scoreboard = maybeScoreboard.get();

                if (!(scoreboard.getScoreboard() instanceof TrocScoreboard)) {
                    throw new RuntimeException("Scoreboard is not TROC-style");
                }

                TrocScoreboard trocScoreboard = (TrocScoreboard) scoreboard.getScoreboard();
                for (int i = 0; i < trocScoreboard.getContent().getEntries().size(); i++) {
                    TrocScoreboardEntry entry = trocScoreboard.getContent().getEntries().get(i);
                    String contestantJid = entry.getContestantJid();
                    User user = userStore.getUserByJid(contestantJid).orElseThrow();
                    UserInfo userInfo = userInfoStore.getInfo(contestantJid);

                    List<String> row = new ArrayList<>();
                    row.add(contestantJid);
                    row.add(userInfo.getStudentId().orElse("-1"));
                    row.add(entry.getContestantUsername());
                    row.add(user.getEmail());
                    row.add(String.valueOf(entry.getTotalPenalties()));
                    row.add(String.valueOf(entry.getTotalPoints()));

                    for (int j = 0; j < entry.getProblemStateList().size(); j++) {
                        if (entry.getProblemStateList().get(j) == TrocScoreboard.TrocScoreboardProblemState.NOT_ACCEPTED) {
                            row.add("0");
                        } else {
                            row.add(String.valueOf(problems.get(j).getPoints().orElse(0)));
                        }
                    }
                    writer.writeNext(row.toArray(new String[0]), false);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return csv.toString();
        }

        public String getScoreboardIOI(
                String actorJid,
                Contest contest,
                boolean canSupervise,
                boolean frozen,
                boolean topParticipantsOnly,
                boolean showClosedProblems,
                int page,
                int pageSize) {
            List<ContestProblem> problems = contestProblemStore.getProblems(contest.getJid());

            StringWriter csv = new StringWriter();
            try (CSVWriter writer = new CSVWriter(csv)) {
                List<String> header = new ArrayList<>();
                header.add("User JID");
                header.add("NPM");
                header.add("Username");
                header.add("Email");
                header.add("Total Scores");
                header.add("Last Affecting Penalty");
                for (ContestProblem problem : problems) {
                    header.add(problem.getAlias());
                }

                writer.writeNext(header.toArray(new String[0]), false);

                Optional<ContestScoreboard> maybeScoreboard = scoreboardFetcher
                        .fetchScoreboard(contest, actorJid, canSupervise, frozen, topParticipantsOnly, showClosedProblems, page, pageSize);

                if (maybeScoreboard.isEmpty()) {
                    return csv.toString();
                }

                ContestScoreboard scoreboard = maybeScoreboard.get();

                if (!(scoreboard.getScoreboard() instanceof IoiScoreboard)) {
                    throw new RuntimeException("Scoreboard is not IOI-style");
                }

                IoiScoreboard ioiScoreboard = (IoiScoreboard) scoreboard.getScoreboard();
                for (int i = 0; i < ioiScoreboard.getContent().getEntries().size(); i++) {
                    IoiScoreboardEntry entry = ioiScoreboard.getContent().getEntries().get(i);
                    String contestantJid = entry.getContestantJid();
                    User user = userStore.getUserByJid(contestantJid).orElseThrow();
                    UserInfo userInfo = userInfoStore.getInfo(contestantJid);

                    List<String> row = new ArrayList<>();
                    row.add(contestantJid);
                    row.add(userInfo.getStudentId().orElse("-1"));
                    row.add(entry.getContestantUsername());
                    row.add(user.getEmail());
                    row.add(String.valueOf(entry.getTotalScores()));
                    row.add(String.valueOf(entry.getLastAffectingPenalty()));

                    for (int j = 0; j < entry.getScores().size(); j++) {
                        row.add(entry.getScores().get(j).map(String::valueOf).orElse("0"));
                    }
                    writer.writeNext(row.toArray(new String[0]), false);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return csv.toString();
        }

        public String getScoreboardIcpc(
                String actorJid,
                Contest contest,
                boolean canSupervise,
                boolean frozen,
                boolean topParticipantsOnly,
                boolean showClosedProblems,
                int page,
                int pageSize) {
            List<ContestProblem> problems = contestProblemStore.getProblems(contest.getJid());

            StringWriter csv = new StringWriter();
            try (CSVWriter writer = new CSVWriter(csv)) {
                List<String> header = new ArrayList<>();
                header.add("User JID");
                header.add("NPM");
                header.add("Username");
                header.add("Email");
                header.add("Total Accepted");
                header.add("Total Penalties");
                header.add("Last Affecting Penalty");
                for (ContestProblem problem : problems) {
                    header.add(problem.getAlias());
                }

                writer.writeNext(header.toArray(new String[0]), false);

                Optional<ContestScoreboard> maybeScoreboard = scoreboardFetcher
                        .fetchScoreboard(contest, actorJid, canSupervise, frozen, topParticipantsOnly, showClosedProblems, page, pageSize);

                if (maybeScoreboard.isEmpty()) {
                    return csv.toString();
                }

                ContestScoreboard scoreboard = maybeScoreboard.get();

                if (!(scoreboard.getScoreboard() instanceof IcpcScoreboard)) {
                    throw new RuntimeException("Scoreboard is not ICPC-style");
                }

                IcpcScoreboard icpcScoreboard = (IcpcScoreboard) scoreboard.getScoreboard();
                for (int i = 0; i < icpcScoreboard.getContent().getEntries().size(); i++) {
                    IcpcScoreboardEntry entry = icpcScoreboard.getContent().getEntries().get(i);
                    String contestantJid = entry.getContestantJid();
                    User user = userStore.getUserByJid(contestantJid).orElseThrow();
                    UserInfo userInfo = userInfoStore.getInfo(contestantJid);

                    List<String> row = new ArrayList<>();
                    row.add(contestantJid);
                    row.add(userInfo.getStudentId().orElse("-1"));
                    row.add(entry.getContestantUsername());
                    row.add(user.getEmail());
                    row.add(String.valueOf(entry.getTotalAccepted()));
                    row.add(String.valueOf(entry.getTotalPenalties()));
                    row.add(String.valueOf(entry.getLastAcceptedPenalty()));

                    for (int j = 0; j < entry.getProblemStateList().size(); j++) {
                        row.add(entry.getProblemStateList().get(j) == IcpcScoreboard.IcpcScoreboardProblemState.ACCEPTED ? "1" : "0");
                    }
                    writer.writeNext(row.toArray(new String[0]), false);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return csv.toString();
        }

        public String getScoreboardBundle(
                String actorJid,
                Contest contest,
                boolean canSupervise,
                boolean frozen,
                boolean topParticipantsOnly,
                boolean showClosedProblems,
                int page,
                int pageSize) {
            List<ContestProblem> problems = contestProblemStore.getProblems(contest.getJid());

            StringWriter csv = new StringWriter();
            try (CSVWriter writer = new CSVWriter(csv)) {
                List<String> header = new ArrayList<>();
                header.add("User JID");
                header.add("NPM");
                header.add("Username");
                header.add("Email");
                header.add("Total Accepted");
                header.add("Total Penalties");
                header.add("Last Affecting Penalty");
                for (ContestProblem problem : problems) {
                    header.add(problem.getAlias());
                }

                writer.writeNext(header.toArray(new String[0]), false);

                Optional<ContestScoreboard> maybeScoreboard = scoreboardFetcher
                        .fetchScoreboard(contest, actorJid, canSupervise, frozen, topParticipantsOnly, showClosedProblems, page, pageSize);

                if (maybeScoreboard.isEmpty()) {
                    return csv.toString();
                }

                ContestScoreboard scoreboard = maybeScoreboard.get();

                if (!(scoreboard.getScoreboard() instanceof IcpcScoreboard)) {
                    throw new RuntimeException("Scoreboard is not ICPC-style");
                }

                IcpcScoreboard icpcScoreboard = (IcpcScoreboard) scoreboard.getScoreboard();
                for (int i = 0; i < icpcScoreboard.getContent().getEntries().size(); i++) {
                    IcpcScoreboardEntry entry = icpcScoreboard.getContent().getEntries().get(i);
                    String contestantJid = entry.getContestantJid();
                    User user = userStore.getUserByJid(contestantJid).orElseThrow();
                    UserInfo userInfo = userInfoStore.getInfo(contestantJid);

                    List<String> row = new ArrayList<>();
                    row.add(contestantJid);
                    row.add(userInfo.getStudentId().orElse("-1"));
                    row.add(entry.getContestantUsername());
                    row.add(user.getEmail());
                    row.add(String.valueOf(entry.getTotalAccepted()));
                    row.add(String.valueOf(entry.getTotalPenalties()));
                    row.add(String.valueOf(entry.getLastAcceptedPenalty()));

                    for (int j = 0; j < entry.getProblemStateList().size(); j++) {
                        row.add(entry.getProblemStateList().get(j) == IcpcScoreboard.IcpcScoreboardProblemState.ACCEPTED ? "1" : "0");
                    }
                    writer.writeNext(row.toArray(new String[0]), false);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return csv.toString();
        }
    }
}
