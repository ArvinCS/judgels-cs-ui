package judgels.jophiel.persistence;

import java.util.Optional;
import judgels.persistence.Dao;

public interface UserGroupDao extends Dao<UserGroupModel> {
    Optional<UserGroupModel> selectByUserJid(String userJid);
}
