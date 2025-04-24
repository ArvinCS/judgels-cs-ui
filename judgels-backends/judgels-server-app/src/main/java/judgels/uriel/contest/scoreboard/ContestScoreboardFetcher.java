package judgels.uriel.contest.scoreboard;

import static judgels.uriel.api.contest.scoreboard.ContestScoreboardType.FROZEN;
import static judgels.uriel.api.contest.scoreboard.ContestScoreboardType.OFFICIAL;

import java.util.Optional;
import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.scoreboard.ContestScoreboard;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.api.contest.scoreboard.Scoreboard;
import judgels.uriel.contest.module.ContestModuleStore;

public class ContestScoreboardFetcher {
    private final ContestModuleStore scoreboardModuleStore;
    private final ContestScoreboardTypeFetcher typeFetcher;
    private final ContestScoreboardStore scoreboardStore;
    private final ContestScoreboardBuilder scoreboardBuilder;

    @Inject
    public ContestScoreboardFetcher(
            ContestModuleStore scoreboardModuleStore,
            ContestScoreboardTypeFetcher typeFetcher,
            ContestScoreboardStore scoreboardStore,
            ContestScoreboardBuilder scoreboardBuilder) {

        this.scoreboardModuleStore = scoreboardModuleStore;
        this.typeFetcher = typeFetcher;
        this.scoreboardStore = scoreboardStore;
        this.scoreboardBuilder = scoreboardBuilder;
    }

    public Optional<ContestScoreboard> fetchScoreboard(
            Contest contest,
            String userJid,
            boolean canSupervise,
            boolean frozen,
            boolean topParticipantsOnly,
            boolean showAllProblems,
            int page,
            int pageSize) {

        ContestScoreboardType type = frozen ? FROZEN : typeFetcher.fetchDefaultType(contest, canSupervise);
        Optional<RawContestScoreboard> rawScoreboard = scoreboardStore.getScoreboard(contest.getJid(), type);

        // TODO(fushar): keep frozen scoreboard in database even after being unfrozen
        if (type == FROZEN && !rawScoreboard.isPresent()) {
            rawScoreboard = scoreboardStore.getScoreboard(contest.getJid(), OFFICIAL);
        }

        final boolean showTopParticipants = topParticipantsOnly
                || (scoreboardModuleStore.getScoreboardModuleConfig(contest.getJid()).getTopParticipantsCount() >= 0
                        && !canSupervise);

        return rawScoreboard.map(raw -> {
            Scoreboard scoreboard =
                    scoreboardBuilder.buildScoreboard(raw, contest, userJid, canSupervise, showAllProblems, showTopParticipants);
            Scoreboard scoreboardPage = scoreboardBuilder.paginateScoreboard(scoreboard, contest, page, pageSize);
            int totalEntries = scoreboard.getContent().getEntries().size();
            return new ContestScoreboard.Builder()
                    .scoreboard(scoreboardPage)
                    .totalEntries(totalEntries)
                    .type(raw.getType())
                    .style(contest.getStyle())
                    .updatedTime(raw.getUpdatedTime())
                    .build();
        });
    }
}
