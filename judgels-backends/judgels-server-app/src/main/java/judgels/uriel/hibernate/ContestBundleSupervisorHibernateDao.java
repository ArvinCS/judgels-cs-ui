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
import judgels.uriel.persistence.ContestBundleSupervisorDao;
import judgels.uriel.persistence.ContestBundleSupervisorModel;
import judgels.uriel.persistence.ContestBundleSupervisorModel_;

public class ContestBundleSupervisorHibernateDao extends HibernateDao<ContestBundleSupervisorModel>
        implements ContestBundleSupervisorDao {
    @Inject
    public ContestBundleSupervisorHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public QueryBuilder<ContestBundleSupervisorModel> selectByContestBundleJid(String bundleJid) {
        return new HibernateQueryBuilder<>(currentSession(), ContestBundleSupervisorModel.class)
                .where(columnEq(ContestBundleSupervisorModel_.bundleJid, bundleJid));
    }

    @Override
    public Optional<ContestBundleSupervisorModel> selectByContestBundleJidAndUserJid(String bundleJid, String userJid) {
        return selectByContestBundleJid(bundleJid)
                .where(columnEq(ContestBundleSupervisorModel_.userJid, userJid))
                .unique();
    }

    @Override
    public void dump(PrintWriter output, String bundleJid) {
        List<ContestBundleSupervisorModel> results = selectByContestBundleJid(bundleJid)
                .orderBy(Model_.ID, OrderDir.ASC)
                .all();

        if (results.isEmpty()) {
            return;
        }

        output.write(
                "INSERT IGNORE INTO uriel_contest_bundle_supervisor (bundleJid, userJid, createdBy, createdAt, updatedAt) VALUES\n");

        for (int i = 0; i < results.size(); i++) {
            ContestBundleSupervisorModel m = results.get(i);
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
