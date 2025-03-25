package judgels.jophiel.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import judgels.persistence.Dao;

public interface UserGroupDao extends Dao<UserGroupModel> {
    Optional<UserGroupModel> selectByUserJid(String userJid);
    List<UserGroupModel> selectAllByUserJids(Collection<String> userJids);
}
