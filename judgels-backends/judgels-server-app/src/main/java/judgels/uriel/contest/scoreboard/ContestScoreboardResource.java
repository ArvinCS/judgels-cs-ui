package judgels.uriel.contest.scoreboard;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
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
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.problem.ContestProblem;
import judgels.uriel.api.contest.scoreboard.ContestScoreboard;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardConfig;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardResponse;
import judgels.uriel.api.contest.scoreboard.ScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.TrocScoreboard;
import judgels.uriel.api.contest.scoreboard.TrocScoreboard.TrocScoreboardEntry;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.log.ContestLogger;
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

        if (showClosedProblems) {
            checkAllowed(canSupervise);
        }

        contestLogger.log(contestJid, "OPEN_SCOREBOARD");

        return scoreboardFetcher
                .fetchScoreboard(contest, actorJid, canSupervise, frozen, showClosedProblems, pageNumber, PAGE_SIZE)
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
            @PathParam("contestJid") String contestJid) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(scoreboardRoleChecker.canManage(actorJid, contest));

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
                    .fetchScoreboard(contest, actorJid, true, false, true, 1, 1000); 

            if (maybeScoreboard.isEmpty()) {
                return Response.noContent().build();
            }

            ContestScoreboard scoreboard = maybeScoreboard.get();

            var contestantJids = Lists.transform(scoreboard.getScoreboard().getContent().getEntries(), ScoreboardEntry::getContestantJid);
            Map<String, Profile> profilesMap = jophielClient.getProfiles(contestantJids, contest.getBeginTime());

            if (scoreboard.getStyle() == ContestStyle.TROC) {
                TrocScoreboard trocScoreboard = (TrocScoreboard) scoreboard.getScoreboard();
                for (int i=0; i<trocScoreboard.getContent().getEntries().size(); i++) {
                    TrocScoreboardEntry entry = trocScoreboard.getContent().getEntries().get(i);
                    String contestantJid = entry.getContestantJid();
                    Profile profile = profilesMap.get(contestantJid);
                    User user = userStore.getUserByJid(contestantJid).orElseThrow();
                    UserInfo userInfo = userInfoStore.getInfo(contestantJid);

                    List<String> row = new ArrayList<>();
                    row.add(contestantJid);
                    row.add(userInfo.getStudentId().orElse("-1"));
                    row.add(profile.getUsername());
                    row.add(user.getEmail());
                    row.add(String.valueOf(entry.getTotalPenalties()));
                    row.add(String.valueOf(entry.getTotalPoints()));

                    for (int j=0; j<entry.getProblemStateList().size(); j++) {
                        if (entry.getProblemStateList().get(i) == TrocScoreboard.TrocScoreboardProblemState.NOT_ACCEPTED) {
                            row.add("0");
                        } else {
                            row.add(String.valueOf(problems.get(j).getPoints().orElse(0)));
                        }
                    }
                    writer.writeNext(row.toArray(new String[0]), false);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ResponseBuilder response = Response.ok(csv.toString());
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
}
