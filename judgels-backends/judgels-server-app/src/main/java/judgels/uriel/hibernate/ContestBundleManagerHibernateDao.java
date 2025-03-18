package judgels.uriel.hibernate;

import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import judgels.persistence.Model_;
import judgels.persistence.QueryBuilder;
import judgels.persistence.api.OrderDir;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.HibernateQueryBuilder;
import judgels.uriel.persistence.ContestBundleManagerDao;
import judgels.uriel.persistence.ContestBundleManagerModel;
import judgels.uriel.persistence.ContestBundleManagerModel_;

public class ContestBundleManagerHibernateDao extends HibernateDao<ContestBundleManagerModel> implements ContestBundleManagerDao {
    @Inject
    public ContestBundleManagerHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public QueryBuilder<ContestBundleManagerModel> selectByContestBundleJid(String bundleJid) {
        return new HibernateQueryBuilder<>(currentSession(), ContestBundleManagerModel.class)
                .where(columnEq(ContestBundleManagerModel_.bundleJid, bundleJid));
    }

    @Override
    public Optional<ContestBundleManagerModel> selectByContestBundleJidAndUserJid(String bundleJid, String userJid) {
        return selectByContestBundleJid(bundleJid)
                .where(columnEq(ContestBundleManagerModel_.userJid, userJid))
                .unique();
    }

    @Override
    public void dump(PrintWriter output, String bundleJid) {
        List<ContestBundleManagerModel> results = selectByContestBundleJid(bundleJid)
                .orderBy(Model_.ID, OrderDir.ASC)
                .all();

        if (results.isEmpty()) {
            return;
        }

        output.write("INSERT IGNORE INTO uriel_contest_bundle_manager (bundleJid, userJid, createdBy, createdAt, updatedAt) VALUES\n");

        for (int i = 0; i < results.size(); i++) {
            ContestBundleManagerModel m = results.get(i);
            if (i > 0) {
                output.write(",\n");
            }
            output.write(String.format("(%s, %s, %s, %s, %s)",
                    escape(m.bundleJid),
                    escape(m.userJid),
                    escape(m.createdBy),
                    escape(m.createdAt),
                    escape(m.updatedAt)));
        }
        output.write(";\n");
    }
}
