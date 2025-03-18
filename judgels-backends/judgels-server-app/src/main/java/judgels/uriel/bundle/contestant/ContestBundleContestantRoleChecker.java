package judgels.uriel.bundle.contestant;

import javax.inject.Inject;
import judgels.uriel.api.bundle.ContestBundle;
import judgels.uriel.bundle.ContestBundleRoleChecker;

public class ContestBundleContestantRoleChecker {
    private final ContestBundleRoleChecker contestBundleRoleChecker;

    @Inject
    public ContestBundleContestantRoleChecker(ContestBundleRoleChecker contestBundleRoleChecker) {
        this.contestBundleRoleChecker = contestBundleRoleChecker;
    }

    public boolean canView(String userJid, ContestBundle bundle) {
        return contestBundleRoleChecker.canManage(userJid, bundle);
    }

    public boolean canManage(String userJid, ContestBundle bundle) {
        return contestBundleRoleChecker.canManage(userJid, bundle);
    }
}
