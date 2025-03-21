package judgels.uriel.hibernate;

import dagger.Module;
import dagger.Provides;
import judgels.uriel.persistence.ContestAnnouncementDao;
import judgels.uriel.persistence.ContestBundleContestantDao;
import judgels.uriel.persistence.ContestBundleDao;
import judgels.uriel.persistence.ContestBundleItemSubmissionDao;
import judgels.uriel.persistence.ContestBundleManagerDao;
import judgels.uriel.persistence.ContestBundleRoleDao;
import judgels.uriel.persistence.ContestBundleSupervisorDao;
import judgels.uriel.persistence.ContestClarificationDao;
import judgels.uriel.persistence.ContestContestantDao;
import judgels.uriel.persistence.ContestDao;
import judgels.uriel.persistence.ContestLogDao;
import judgels.uriel.persistence.ContestManagerDao;
import judgels.uriel.persistence.ContestModuleDao;
import judgels.uriel.persistence.ContestProblemDao;
import judgels.uriel.persistence.ContestProgrammingGradingDao;
import judgels.uriel.persistence.ContestProgrammingSubmissionDao;
import judgels.uriel.persistence.ContestRoleDao;
import judgels.uriel.persistence.ContestScoreboardDao;
import judgels.uriel.persistence.ContestStyleDao;
import judgels.uriel.persistence.ContestSupervisorDao;

@Module
public class UrielHibernateDaoModule {
    private UrielHibernateDaoModule() {}

    @Provides
    static ContestBundleContestantDao contestBundleContestantDao(ContestBundleContestantHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestBundleDao contestBundleDao(ContestBundleHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestBundleManagerDao contestBundleManagerDao(ContestBundleManagerHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestBundleRoleDao contestBundleRoleDao(ContestBundleRoleHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestBundleSupervisorDao contestBundleSupervisorDao(ContestBundleSupervisorHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestDao contestDao(ContestHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestStyleDao contestStyleDao(ContestStyleHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestAnnouncementDao contestAnnouncementDao(ContestAnnouncementHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestClarificationDao contestClarificationDao(ContestClarificationHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestContestantDao contestContestantDao(ContestContestantHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestManagerDao contestManagerDao(ContestManagerHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestModuleDao contestModuleDao(ContestModuleHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestProblemDao contestProblemDao(ContestProblemHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestRoleDao contestRoleDao(ContestRoleHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestScoreboardDao contestScoreboardDao(ContestScoreboardHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestSupervisorDao contestSupervisorDao(ContestSupervisorHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestBundleItemSubmissionDao contestBundleItemSubmissionDao(ContestBundleItemSubmissionHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestProgrammingGradingDao contestProgrammingGradingDao(ContestProgrammingGradingHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestProgrammingSubmissionDao contestProgrammingSubmissionDao(
            ContestProgrammingSubmissionHibernateDao dao) {

        return dao;
    }

    @Provides
    static ContestLogDao contestLogDao(ContestLogHibernateDao dao) {
        return dao;
    }
}
