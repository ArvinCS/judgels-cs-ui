package judgels.uriel.hibernate;

import static judgels.persistence.CriteriaPredicate.or;
import static judgels.uriel.hibernate.ContestRoleHibernateDao.isPublic;
import static judgels.uriel.hibernate.ContestRoleHibernateDao.userCanViewAsContestant;
import static judgels.uriel.hibernate.ContestRoleHibernateDao.userCanViewAsSupervisorOrAbove;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import judgels.persistence.CriteriaPredicate;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.uriel.persistence.ContestBundleManagerModel;
import judgels.uriel.persistence.ContestBundleManagerModel_;
import judgels.uriel.persistence.ContestBundleModel;
import judgels.uriel.persistence.ContestBundleModel_;
import judgels.uriel.persistence.ContestBundleRoleDao;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestModel_;

@Singleton
public class ContestBundleRoleHibernateDao extends JudgelsHibernateDao<ContestBundleModel> implements ContestBundleRoleDao {
    @Inject
    public ContestBundleRoleHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public boolean isManager(String userJid, String contestBundleJid) {
        return select()
                .where(contestBundleIs(contestBundleJid))
                .where(userIsManager(userJid))
                .unique()
                .isPresent();
    }

    static CriteriaPredicate<ContestBundleModel> contestBundleIs(String contestBundleJid) {
        return (cb, cq, root) -> cb.equal(root.get(ContestBundleModel_.jid), contestBundleJid);
    }

    static CriteriaPredicate<ContestBundleModel> userCanView(String userJid) {
        return (cb, cq, root) -> {
            Subquery<ContestModel> subquery = cq.subquery(ContestModel.class);
            Root<ContestModel> subroot = subquery.from(ContestModel.class);

            return cb.or(userIsManager(userJid).toPredicate(cb, cq, root), cb.exists(subquery
                    .select(subroot)
                    .where(cb.equal(subroot.get(ContestModel_.bundleJid), root.get(ContestBundleModel_.jid)),
                            or(
                                    isPublic(),
                                    userCanViewAsContestant(userJid),
                                    userCanViewAsSupervisorOrAbove(userJid)
                            ).toPredicate(cb, cq, subroot))));
        };
    }

    static CriteriaPredicate<ContestBundleModel> userIsManager(String userJid) {
        return (cb, cq, root) -> {
            Subquery<ContestBundleManagerModel> subquery = cq.subquery(ContestBundleManagerModel.class);
            Root<ContestBundleManagerModel> subroot = subquery.from(ContestBundleManagerModel.class);

            return cb.exists(subquery
                    .select(subroot)
                    .where(
                            cb.equal(subroot.get(ContestBundleManagerModel_.bundleJid), root.get(ContestBundleModel_.jid)),
                            cb.equal(subroot.get(ContestBundleManagerModel_.userJid), userJid)));
        };
    }
}
