package judgels.uriel.persistence;

import java.io.PrintWriter;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.QueryBuilder;

public interface ContestBundleContestantDao extends Dao<ContestBundleContestantModel> {
    QueryBuilder<ContestBundleContestantModel> selectByContestBundleJid(String bundleJid);
    Optional<ContestBundleContestantModel> selectByContestBundleJidAndUserJid(String bundleJid, String userJid);
    void dump(PrintWriter output, String bundleJid);
}
