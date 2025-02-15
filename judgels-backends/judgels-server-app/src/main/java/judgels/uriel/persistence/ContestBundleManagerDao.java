package judgels.uriel.persistence;

import java.io.PrintWriter;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.QueryBuilder;

public interface ContestBundleManagerDao extends Dao<ContestBundleManagerModel> {
    QueryBuilder<ContestBundleManagerModel> selectByContestBundleJid(String bundleJid);
    Optional<ContestBundleManagerModel> selectByContestBundleJidAndUserJid(String bundleJid, String userJid);
    void dump(PrintWriter output, String bundleJid);
}
