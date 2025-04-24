package judgels.uriel.bundle.scoreboard;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
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
import judgels.uriel.api.bundle.ContestBundle;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.scoreboard.BundleScoreboard;
import judgels.uriel.api.contest.scoreboard.BundleScoreboard.BundleScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.ContestScoreboard;
import judgels.uriel.api.contest.scoreboard.GcjScoreboard;
import judgels.uriel.api.contest.scoreboard.GcjScoreboard.GcjScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard.IcpcScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard.IoiScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.ScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.TrocScoreboard;
import judgels.uriel.api.contest.scoreboard.TrocScoreboard.TrocScoreboardEntry;
import judgels.uriel.bundle.ContestBundleRoleChecker;
import judgels.uriel.bundle.ContestBundleStore;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.scoreboard.ContestScoreboardFetcher;
import liquibase.util.csv.CSVWriter;

@Path("/api/v2/contest-bundles/{bundleJid}/scoreboard")
public class ContestBundleScoreboardResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected ContestBundleStore contestBundleStore;
    @Inject protected ContestBundleRoleChecker contestBundleRoleChecker;
    @Inject protected ContestStore contestStore;
    @Inject protected ContestScoreboardFetcher scoreboardFetcher;
    @Inject protected UserStore userStore;
    @Inject protected UserInfoStore userInfoStore;
    @Inject protected JophielClient jophielClient;

    @Inject public ContestBundleScoreboardResource() {}

    @GET
    @Path("/export")
    @Produces("application/x-download")
    @UnitOfWork(readOnly = true)
    public Response exportScoreboard(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("bundleJid") String contestBundleJid,
            @QueryParam("frozen") boolean frozen,
            @QueryParam("topParticipantsOnly") @DefaultValue("false") boolean topParticipantsOnly,
            @QueryParam("showClosedProblems") @DefaultValue("false") boolean showClosedProblems,
            @QueryParam("page") @DefaultValue("1") int pageNumber,
            @QueryParam("pageSize") @DefaultValue("1000") int pageSize) {

        String actorJid = actorChecker.check(authHeader);
        ContestBundle bundle = checkFound(contestBundleStore.getContestBundleByJid(contestBundleJid));
        checkAllowed(contestBundleRoleChecker.canManage(actorJid, bundle));

        List<Contest> contests = contestStore.getAllContestsByBundle(bundle.getJid(), Optional.empty(), 1, 1000000).getPage();

        StringWriter csv = new StringWriter();
        try (CSVWriter writer = new CSVWriter(csv)) {
            Set<String> userJids = new HashSet<>();
            List<ContestScoreboard> scoreboards = new ArrayList<>();

            List<String> header = new ArrayList<>();
            header.add("User JID");
            header.add("NPM");
            header.add("Username");
            header.add("Email");
            for (Contest contest : contests) {
                Optional<ContestScoreboard> maybeScoreboard = scoreboardFetcher
                        .fetchScoreboard(contest, actorJid, true, frozen, topParticipantsOnly, showClosedProblems, pageNumber, pageSize);

                if (!maybeScoreboard.isPresent()) {
                    continue;
                }

                header.add(contest.getName());

                ContestScoreboard scoreboard = maybeScoreboard.get();
                scoreboards.add(scoreboard);

                var contestantJids = Lists.transform(scoreboard.getScoreboard().getContent().getEntries(), ScoreboardEntry::getContestantJid);
                contestantJids.forEach(userJids::add);
            }
            header.add("Total Points");

            writer.writeNext(header.toArray(new String[0]), false);

            double sumPoints = 0;

            for (String contestantJid : userJids) {
                User user = userStore.getUserByJid(contestantJid).orElseThrow();
                UserInfo userInfo = userInfoStore.getInfo(contestantJid);
                Profile userProfile = jophielClient.getProfile(user.getJid());

                List<String> row = new ArrayList<>();
                row.add(contestantJid);
                row.add(userInfo.getStudentId().orElse("-1"));
                row.add(userProfile.getUsername());
                row.add(user.getEmail());
                for (ContestScoreboard scoreboard : scoreboards) {
                    if (scoreboard.getStyle() == ContestStyle.TROC) {
                        TrocScoreboard trocScoreboard = (TrocScoreboard) scoreboard.getScoreboard();
                        Optional<TrocScoreboardEntry> maybeEntry = trocScoreboard.getContent().getEntries().stream()
                                .filter(e -> e.getContestantJid().equals(contestantJid)).findFirst();

                        if (maybeEntry.isPresent()) {
                            TrocScoreboardEntry entry = maybeEntry.get();

                            long totalPoints = trocScoreboard.getState().getProblemPoints().get().stream().mapToLong(i -> i).sum();

                            double contestantPoints = entry.getTotalPoints() * 100.0 / totalPoints;

                            sumPoints += contestantPoints;
                            row.add(String.valueOf(contestantPoints));
                        } else {
                            row.add("0");
                        }
                    } else if (scoreboard.getStyle() == ContestStyle.IOI) {
                        IoiScoreboard ioiScoreboard = (IoiScoreboard) scoreboard.getScoreboard();
                        Optional<IoiScoreboardEntry> maybeEntry = ioiScoreboard.getContent().getEntries().stream()
                                .filter(e -> e.getContestantJid().equals(contestantJid)).findFirst();

                        if (maybeEntry.isPresent()) {
                            IoiScoreboardEntry entry = maybeEntry.get();

                            long totalScores = ioiScoreboard.getState().getProblemPoints().get().stream().mapToLong(i -> i).sum();

                            double contestantPoints = entry.getTotalScores() * 100.0 / totalScores;

                            sumPoints += contestantPoints;
                            row.add(String.valueOf(contestantPoints));
                        } else {
                            row.add("0");
                        }
                    } else if (scoreboard.getStyle() == ContestStyle.ICPC) {
                        IcpcScoreboard icpcScoreboard = (IcpcScoreboard) scoreboard.getScoreboard();
                        Optional<IcpcScoreboardEntry> maybeEntry = icpcScoreboard.getContent().getEntries().stream()
                                .filter(e -> e.getContestantJid().equals(contestantJid)).findFirst();

                        if (maybeEntry.isPresent()) {
                            IcpcScoreboardEntry entry = maybeEntry.get();

                            long totalProblems = icpcScoreboard.getState().getProblemAliases().size();

                            double contestantPoints = entry.getTotalAccepted() * 100.0 / totalProblems;

                            sumPoints += contestantPoints;
                            row.add(String.valueOf(contestantPoints));
                        } else {
                            row.add("0");
                        }
                    } else if (scoreboard.getStyle() == ContestStyle.BUNDLE) {
                        BundleScoreboard bundleScoreboard = (BundleScoreboard) scoreboard.getScoreboard();
                        Optional<BundleScoreboardEntry> maybeEntry = bundleScoreboard.getContent().getEntries().stream()
                                .filter(e -> e.getContestantJid().equals(contestantJid)).findFirst();

                        if (maybeEntry.isPresent()) {
                            BundleScoreboardEntry entry = maybeEntry.get();

                            long totalPoints = bundleScoreboard.getState().getProblemPoints().get().stream().mapToLong(i -> i).sum();

                            double contestantPoints = entry.getTotalAnsweredItems() * 100.0 / totalPoints;

                            sumPoints += contestantPoints;
                            row.add(String.valueOf(contestantPoints));
                        } else {
                            row.add("0");
                        }
                    } else if (scoreboard.getStyle() == ContestStyle.GCJ) {
                        GcjScoreboard gcjScoreboard = (GcjScoreboard) scoreboard.getScoreboard();
                        Optional<GcjScoreboardEntry> maybeEntry = gcjScoreboard.getContent().getEntries().stream()
                                .filter(e -> e.getContestantJid().equals(contestantJid)).findFirst();

                        if (maybeEntry.isPresent()) {
                            GcjScoreboardEntry entry = maybeEntry.get();

                            long totalPoints = gcjScoreboard.getState().getProblemPoints().get().stream().mapToLong(i -> i).sum();

                            double contestantPoints = entry.getTotalPoints() * 100.0 / totalPoints;

                            sumPoints += contestantPoints;
                            row.add(String.valueOf(contestantPoints));
                        } else {
                            row.add("0");
                        }
                    } else {
                        row.add("0");
                    }
                }
                row.add(String.valueOf(sumPoints));

                writer.writeNext(row.toArray(new String[0]), false);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ResponseBuilder response = Response.ok(csv.toString());
        response.header("Access-Control-Expose-Headers", "Content-Disposition");
        response.header("Content-Disposition", "attachment; filename=\"scoreboard.csv\"");
        response.header("Content-Encoding", "csv");
        return response.build();
    }
}
