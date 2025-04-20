package judgels.uriel.bundle;

import javax.inject.Inject;
import judgels.uriel.api.bundle.ContestBundle;
import judgels.uriel.api.bundle.role.ContestBundleRole;
import judgels.uriel.persistence.ContestBundleRoleDao;
import judgels.uriel.persistence.ContestDao;
import judgels.uriel.persistence.ContestRoleDao;
import judgels.uriel.role.RoleChecker;

public class ContestBundleRoleChecker {
    private final RoleChecker roleChecker;
    private final ContestBundleRoleDao contestBundleRoleDao;
    private final ContestDao contestDao;
    private final ContestRoleDao contestRoleDao;

    @Inject
    public ContestBundleRoleChecker(RoleChecker roleChecker, ContestBundleRoleDao contestBundleRoleDao, ContestDao contestDao, ContestRoleDao contestRoleDao) {
        this.roleChecker = roleChecker;
        this.contestBundleRoleDao = contestBundleRoleDao;
        this.contestDao = contestDao;
        this.contestRoleDao = contestRoleDao;
    }

    public boolean canAdminister(String userJid) {
        return roleChecker.isAdmin(userJid);
    }

    public boolean canManage(String userJid, ContestBundle bundle) {
        return roleChecker.isAdmin(userJid) || contestBundleRoleDao.isManager(userJid, bundle.getJid());
    }

    public boolean canManage(String userJid, String bundleJid) {
        return roleChecker.isAdmin(userJid) || contestBundleRoleDao.isManager(userJid, bundleJid);
    }

    public boolean canView(String userJid, ContestBundle bundle) {
        return roleChecker.isAdmin(userJid) || contestBundleRoleDao.isManager(userJid, bundle.getJid()) || contestDao.selectAllByBundle(bundle.getJid()).stream().anyMatch(contest -> contestRoleDao.isViewerOrAbove(userJid, contest.jid));
    }

    public ContestBundleRole getRole(String userJid, ContestBundle bundle) {
        if (roleChecker.isAdmin(userJid)) {
            return ContestBundleRole.ADMIN;
        } else if (contestBundleRoleDao.isManager(userJid, bundle.getJid())) {
            return ContestBundleRole.MANAGER;
        }

        return ContestBundleRole.NONE;
    }
}
