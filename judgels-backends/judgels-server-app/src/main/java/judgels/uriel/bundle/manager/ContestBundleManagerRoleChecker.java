package judgels.uriel.bundle.manager;

import javax.inject.Inject;

import judgels.uriel.api.bundle.ContestBundle;
import judgels.uriel.bundle.ContestBundleRoleChecker;

public class ContestBundleManagerRoleChecker {
    private final ContestBundleRoleChecker contestBundleRoleChecker;

    @Inject
    public ContestBundleManagerRoleChecker(ContestBundleRoleChecker contestBundleRoleChecker) {
        this.contestBundleRoleChecker = contestBundleRoleChecker;
    }

    public boolean canView(String userJid, ContestBundle bundle) {
        return contestBundleRoleChecker.canManage(userJid, bundle);
    }

    public boolean canManage(String userJid) {
        return contestBundleRoleChecker.canAdminister(userJid);
    }
}
