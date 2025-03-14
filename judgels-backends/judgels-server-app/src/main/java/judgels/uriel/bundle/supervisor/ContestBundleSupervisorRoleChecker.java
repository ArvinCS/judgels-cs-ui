package judgels.uriel.bundle.supervisor;

import javax.inject.Inject;

import judgels.uriel.api.bundle.ContestBundle;
import judgels.uriel.bundle.ContestBundleRoleChecker;

public class ContestBundleSupervisorRoleChecker {
    private final ContestBundleRoleChecker contestBundleRoleChecker;

    @Inject
    public ContestBundleSupervisorRoleChecker(ContestBundleRoleChecker contestBundleRoleChecker) {
        this.contestBundleRoleChecker = contestBundleRoleChecker;
    }

    public boolean canView(String userJid, ContestBundle bundle) {
        return contestBundleRoleChecker.canManage(userJid, bundle);
    }

    public boolean canManage(String userJid, ContestBundle bundle) {
        return contestBundleRoleChecker.canManage(userJid, bundle);
    }
}
