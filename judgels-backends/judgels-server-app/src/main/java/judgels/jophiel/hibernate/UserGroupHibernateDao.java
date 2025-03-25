package judgels.jophiel.hibernate;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.persistence.UserGroupDao;
import judgels.jophiel.persistence.UserGroupModel;
import judgels.jophiel.persistence.UserGroupModel_;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

public class UserGroupHibernateDao extends HibernateDao<UserGroupModel> implements UserGroupDao {
    @Inject
    public UserGroupHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<UserGroupModel> selectByUserJid(String userJid) {
        return select().where(columnEq(UserGroupModel_.userJid, userJid)).unique();
    }

    @Override
    public List<UserGroupModel> selectAllByUserJids(Collection<String> userJids) {
        return select().where(columnIn(UserGroupModel_.userJid, userJids)).all();
    }
}
