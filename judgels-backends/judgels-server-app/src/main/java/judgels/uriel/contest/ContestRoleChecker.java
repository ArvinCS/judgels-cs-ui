package judgels.uriel.contest;

import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.role.ContestRole;
import judgels.uriel.persistence.ContestBundleRoleDao;
import judgels.uriel.persistence.ContestRoleDao;
import judgels.uriel.role.RoleChecker;

public class ContestRoleChecker {
    private final RoleChecker roleChecker;
    private final ContestBundleRoleDao contestBundleRoleDao;
    private final ContestRoleDao contestRoleDao;
    private final ContestTimer contestTimer;

    @Inject
    public ContestRoleChecker(RoleChecker roleChecker, ContestBundleRoleDao contestBundleRoleDao, ContestRoleDao contestRoleDao, ContestTimer contestTimer) {
        this.roleChecker = roleChecker;
        this.contestBundleRoleDao = contestBundleRoleDao;
        this.contestTimer = contestTimer;
        this.contestRoleDao = contestRoleDao;
    }

    public boolean canAdminister(String userJid) {
        return roleChecker.isAdmin(userJid);
    }

    public boolean canView(String userJid, Contest contest) {
        return roleChecker.isAdmin(userJid) || (contest.getBundleJid().isPresent() && contestBundleRoleDao.isManager(userJid, contest.getBundleJid().get())) || contestRoleDao.isViewerOrAbove(userJid, contest.getJid());
    }

    public boolean canSupervise(String userJid, Contest contest) {
        return roleChecker.isAdmin(userJid) || (contest.getBundleJid().isPresent() && contestBundleRoleDao.isManager(userJid, contest.getBundleJid().get())) || contestRoleDao.isSupervisorOrAbove(userJid, contest.getJid());
    }

    public boolean canManage(String userJid, Contest contest) {
        return roleChecker.isAdmin(userJid) || (contest.getBundleJid().isPresent() && contestBundleRoleDao.isManager(userJid, contest.getBundleJid().get())) || contestRoleDao.isManager(userJid, contest.getJid());
    }

    public boolean canStartVirtual(String userJid, Contest contest) {
        return contestRoleDao.isContestant(userJid, contest.getJid())
                && contestTimer.hasBegun(contest)
                && !contestTimer.hasEnded(contest)
                && !contestTimer.hasStarted(contest, userJid);
    }

    public boolean canResetVirtual(String userJid, Contest contest) {
        return roleChecker.isAdmin(userJid) || contestRoleDao.isManager(userJid, contest.getJid());
    }

    public ContestRole getRole(String userJid, Contest contest) {
        if (roleChecker.isAdmin(userJid)) {
            return ContestRole.ADMIN;
        } else if (contestRoleDao.isManager(userJid, contest.getJid()) || (contest.getBundleJid().isPresent() && contestBundleRoleDao.isManager(userJid, contest.getBundleJid().get()))) {
            return ContestRole.MANAGER;
        } else if (contestRoleDao.isSupervisorOrAbove(userJid, contest.getJid())) {
            return ContestRole.SUPERVISOR;
        } else if (contestRoleDao.isContestant(userJid, contest.getJid())) {
            return ContestRole.CONTESTANT;
        }

        return ContestRole.NONE;
    }
}
