package judgels.uriel.persistence;

import java.io.PrintWriter;
import java.util.Optional;

import judgels.persistence.Dao;
import judgels.persistence.QueryBuilder;

public interface ContestBundleSupervisorDao extends Dao<ContestBundleSupervisorModel> {
    QueryBuilder<ContestBundleSupervisorModel> selectByContestBundleJid(String bundleJid);
    Optional<ContestBundleSupervisorModel> selectByContestBundleJidAndUserJid(String bundleJid, String userJid);
    void dump(PrintWriter output, String bundleJid);
}
