package judgels.jophiel.hibernate;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.persistence.UserDao;
import judgels.jophiel.persistence.UserModel;
import judgels.jophiel.persistence.UserModel_;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.HibernateQueryBuilder;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import org.hibernate.Session;

public class UserHibernateDao extends JudgelsHibernateDao<UserModel> implements UserDao {
    @Inject
    public UserHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public UserHibernateQueryBuilder select() {
        return new UserHibernateQueryBuilder(currentSession());
    }

    @Override
    public Optional<UserModel> selectByUsername(String username) {
        return select().where(columnEq(UserModel_.username, username)).unique();
    }

    @Override
    public Optional<UserModel> selectByEmail(String email) {
        return select().where(columnEq(UserModel_.email, email)).unique();
    }

    @Override
    public List<UserModel> selectAllByUsernames(Collection<String> usernames) {
        return select().where(columnIn(UserModel_.username, usernames)).all();
    }

    private static class UserHibernateQueryBuilder extends HibernateQueryBuilder<UserModel> implements UserQueryBuilder {
        UserHibernateQueryBuilder(Session currentSession) {
            super(currentSession, UserModel.class);
        }

        public UserHibernateQueryBuilder whereUsernameLike(String username) {
            where(columnLike(UserModel_.username, username));
            return this;
        }
    }
}
