package judgels.jophiel.user.group;

import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.api.user.group.UserGroup;
import judgels.jophiel.persistence.UserGroupDao;
import judgels.jophiel.persistence.UserGroupModel;

public class UserGroupStore {
    private final UserGroupDao userGroupDao;

    @Inject
    public UserGroupStore(UserGroupDao userGroupDao) {
        this.userGroupDao = userGroupDao;
    }

    public UserGroup getGroup(String userJid) {
        return userGroupDao.selectByUserJid(userJid)
            .map(UserGroupStore::fromModel)
            .orElse(new UserGroup.Builder().userJid(userJid).groups(List.of()).build());
    }

    public UserGroup upsertGroup(String userJid, UserGroup group) {
        Optional<UserGroupModel> maybeModel = userGroupDao.selectByUserJid(userJid);

        if (maybeModel.isPresent()) {
            UserGroupModel model = maybeModel.get();
            toModel(userJid, group, model);
            return fromModel(userGroupDao.update(model));
        } else {
            UserGroupModel model = new UserGroupModel();
            toModel(userJid, group, model);
            return fromModel(userGroupDao.insert(model));
        }
    }

    private static void toModel(String userJid, UserGroup group, UserGroupModel model) {
        model.userJid = userJid;
        model.groups = group.getGroups().orElse(List.of());
    }

    private static UserGroup fromModel(UserGroupModel model) {
        return new UserGroup.Builder()
                .userJid(model.userJid)
                .groups(model.groups)
                .build();
    }
}
