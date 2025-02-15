package judgels.uriel.hibernate;

import static judgels.uriel.hibernate.ContestBundleRoleHibernateDao.userCanView;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import judgels.persistence.CriteriaPredicate;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.HibernateQueryBuilder;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.uriel.persistence.ContestBundleDao;
import judgels.uriel.persistence.ContestBundleModel;
import judgels.uriel.persistence.ContestBundleModel_;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestModel_;
import org.hibernate.Session;

public class ContestBundleHibernateDao extends JudgelsHibernateDao<ContestBundleModel> implements ContestBundleDao {
    @Inject
    public ContestBundleHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public ContestBundleHibernateQueryBuilder select() {
        return new ContestBundleHibernateQueryBuilder(currentSession());
    }

    @Override
    public Optional<ContestBundleModel> selectBySlug(String contestBundleSlug) {
        return select()
                .where((cb, cq, root) -> cb.equal(root.get(ContestBundleModel_.slug), contestBundleSlug))
                .unique();
    }

    @Override
    public List<ContestBundleModel> selectAllBySlugs(Collection<String> contestBundleSlugs) {
        return select().where(columnIn(ContestBundleModel_.slug, contestBundleSlugs)).all();
    }

    private static class ContestBundleHibernateQueryBuilder extends HibernateQueryBuilder<ContestBundleModel> implements ContestBundleQueryBuilder {
        public ContestBundleHibernateQueryBuilder(Session currentSession) {
            super(currentSession, ContestBundleModel.class);
        }

        @Override
        public ContestBundleQueryBuilder whereContestIn(String contestJid) {
            where(hasContest(contestJid));
            return this;
        }

        @Override
        public ContestBundleQueryBuilder whereNameLike(String name) {
            where(columnLike(ContestBundleModel_.name, name));
            return this;
        }

        @Override
        public ContestBundleQueryBuilder whereUserCanView(String userJid) {
            where(userCanView(userJid));
            return this;
        }
    }

    static CriteriaPredicate<ContestBundleModel> hasContest(String contestJid) {
        return (cb, cq, root) -> {
            Subquery<String> subquery = cq.subquery(String.class);
            Root<ContestModel> contestRoot = subquery.from(ContestModel.class);
            subquery.select(contestRoot.get(ContestModel_.bundleJid))
                    .where(cb.equal(contestRoot.get(ContestModel_.jid), contestJid));
            return cb.equal(root.get(ContestBundleModel_.jid), subquery);
        };
    }
}
