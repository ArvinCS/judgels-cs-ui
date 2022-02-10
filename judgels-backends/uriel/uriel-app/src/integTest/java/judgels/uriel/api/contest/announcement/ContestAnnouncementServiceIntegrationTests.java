package judgels.uriel.api.contest.announcement;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static judgels.uriel.api.contest.announcement.ContestAnnouncementStatus.DRAFT;
import static judgels.uriel.api.contest.announcement.ContestAnnouncementStatus.PUBLISHED;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_JID;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_HEADER;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import org.junit.jupiter.api.Test;

class ContestAnnouncementServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private final ContestAnnouncementService announcementService = createService(ContestAnnouncementService.class);

    @Test
    void create_update_announcement() {
        Contest contest = beginContest(createContestWithRoles());

        ContestAnnouncement announcement = announcementService.createAnnouncement(
                MANAGER_HEADER,
                contest.getJid(),
                new ContestAnnouncementData.Builder()
                        .title("this is title 1")
                        .content("this is content 1")
                        .status(PUBLISHED)
                        .build());

        assertThat(announcement.getUserJid()).isEqualTo(MANAGER_JID);
        assertThat(announcement.getTitle()).isEqualTo("this is title 1");
        assertThat(announcement.getContent()).isEqualTo("this is content 1");
        assertThat(announcement.getStatus()).isEqualTo(PUBLISHED);

        announcement = announcementService.updateAnnouncement(
                MANAGER_HEADER,
                contest.getJid(),
                announcement.getJid(),
                new ContestAnnouncementData.Builder()
                        .title("this is new title")
                        .content("this is new content")
                        .status(DRAFT)
                        .build());

        assertThat(announcement.getUserJid()).isEqualTo(MANAGER_JID);
        assertThat(announcement.getTitle()).isEqualTo("this is new title");
        assertThat(announcement.getContent()).isEqualTo("this is new content");
        assertThat(announcement.getStatus()).isEqualTo(DRAFT);
    }

    @Test
    void get_announcements() {
        Contest contest = createContestWithRoles();
        beginContest(contest);
        enableModule(contest, REGISTRATION);

        ContestAnnouncement announcement1 = createAnnouncement(contest, PUBLISHED);
        ContestAnnouncement announcement2 = createAnnouncement(contest, DRAFT);
        ContestAnnouncement announcement3 = createAnnouncement(contest, PUBLISHED);

        Map<Optional<AuthHeader>, List<ContestAnnouncement>> announcementsMap = new LinkedHashMap<>();
        announcementsMap.put(of(ADMIN_HEADER), ImmutableList.of(announcement3, announcement2, announcement1));
        announcementsMap.put(of(MANAGER_HEADER), ImmutableList.of(announcement3, announcement2, announcement1));
        announcementsMap.put(of(SUPERVISOR_HEADER), ImmutableList.of(announcement3, announcement2, announcement1));
        announcementsMap.put(of(CONTESTANT_HEADER), ImmutableList.of(announcement3, announcement1));
        announcementsMap.put(of(USER_HEADER), ImmutableList.of(announcement3, announcement1));
        announcementsMap.put(empty(), ImmutableList.of(announcement3, announcement1));

        Map<Optional<AuthHeader>, Boolean> canSuperviseMap = new LinkedHashMap<>();
        canSuperviseMap.put(of(ADMIN_HEADER), true);
        canSuperviseMap.put(of(MANAGER_HEADER), true);
        canSuperviseMap.put(of(SUPERVISOR_HEADER), true);
        canSuperviseMap.put(of(CONTESTANT_HEADER), false);
        canSuperviseMap.put(of(USER_HEADER), false);
        canSuperviseMap.put(empty(), false);

        Map<Optional<AuthHeader>, Boolean> canManageMap = new LinkedHashMap<>();
        canManageMap.put(of(ADMIN_HEADER), true);
        canManageMap.put(of(MANAGER_HEADER), true);
        canManageMap.put(of(SUPERVISOR_HEADER), false);
        canManageMap.put(of(CONTESTANT_HEADER), false);
        canManageMap.put(of(USER_HEADER), false);
        canManageMap.put(empty(), false);

        for (Optional<AuthHeader> authHeader : announcementsMap.keySet()) {
            ContestAnnouncementsResponse response =
                    announcementService.getAnnouncements(authHeader, contest.getJid(), empty());
            assertThat(response.getData().getPage()).containsExactlyElementsOf(announcementsMap.get(authHeader));
            assertThat(response.getProfilesMap()).containsOnlyKeys(MANAGER_JID);
            assertThat(response.getConfig().getCanSupervise()).isEqualTo(canSuperviseMap.get(authHeader));
            assertThat(response.getConfig().getCanManage()).isEqualTo(canManageMap.get(authHeader));
        }
    }

    private ContestAnnouncement createAnnouncement(Contest contest, ContestAnnouncementStatus status) {
        return announcementService.createAnnouncement(
                MANAGER_HEADER,
                contest.getJid(),
                new ContestAnnouncementData.Builder()
                        .title(randomString())
                        .content(randomString())
                        .status(status)
                        .build());
    }
}
