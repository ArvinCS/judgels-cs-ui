package judgels.jophiel.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import judgels.persistence.JudgelsDao;
import judgels.persistence.QueryBuilder;

public interface UserDao extends JudgelsDao<UserModel> {
    UserQueryBuilder select();
    Optional<UserModel> selectByUsername(String username);
    Optional<UserModel> selectByEmail(String email);
    List<UserModel> selectAllByUsernames(Collection<String> usernames);

    interface UserQueryBuilder extends QueryBuilder<UserModel> {
        UserQueryBuilder whereUsernameLike(String username);
    }
}
