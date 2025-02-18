package judgels.uriel.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import judgels.persistence.JudgelsDao;
import judgels.persistence.QueryBuilder;

public interface ContestBundleDao extends JudgelsDao<ContestBundleModel> {
    ContestBundleQueryBuilder select();
    Optional<ContestBundleModel> selectBySlug(String contestBundleSlug);
    List<ContestBundleModel> selectAllBySlugs(Collection<String> contestBundleSlugs);

    interface ContestBundleQueryBuilder extends QueryBuilder<ContestBundleModel> {
        ContestBundleQueryBuilder whereContestIn(String contestJid);
        ContestBundleQueryBuilder whereNameLike(String name);
        ContestBundleQueryBuilder whereUserCanView(String userJid);
    }
}
