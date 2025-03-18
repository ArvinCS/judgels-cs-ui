package judgels.uriel.bundle.manager;

import com.google.common.collect.Lists;
import java.util.Optional;
import javax.inject.Inject;
import judgels.persistence.api.Page;
import judgels.uriel.api.bundle.manager.ContestBundleManager;
import judgels.uriel.persistence.ContestBundleManagerDao;
import judgels.uriel.persistence.ContestBundleManagerModel;

public class ContestBundleManagerStore {
    private final ContestBundleManagerDao contestBundleManagerDao;

    @Inject
    public ContestBundleManagerStore(ContestBundleManagerDao contestBundleManagerDao) {
        this.contestBundleManagerDao = contestBundleManagerDao;
    }

    public boolean upsertManager(String contestBundleJid, String userJid) {
        Optional<ContestBundleManagerModel> maybeModel = contestBundleManagerDao
                .selectByContestBundleJidAndUserJid(contestBundleJid, userJid);
        if (maybeModel.isPresent()) {
            return false;
        }

        ContestBundleManagerModel model = new ContestBundleManagerModel();
        model.bundleJid = contestBundleJid;
        model.userJid = userJid;
        contestBundleManagerDao.insert(model);

        return true;
    }

    public boolean deleteManager(String contestBundleJid, String userJid) {
        Optional<ContestBundleManagerModel> maybeModel = contestBundleManagerDao
                .selectByContestBundleJidAndUserJid(contestBundleJid, userJid);
        if (!maybeModel.isPresent()) {
            return false;
        }

        contestBundleManagerDao.delete(maybeModel.get());
        return true;
    }

    public Page<ContestBundleManager> getManagers(String contestBundleJid, int pageNumber, int pageSize) {
        return contestBundleManagerDao
                .selectByContestBundleJid(contestBundleJid)
                .paged(pageNumber, pageSize)
                .mapPage(p -> Lists.transform(p, ContestBundleManagerStore::fromModel));
    }

    private static ContestBundleManager fromModel(ContestBundleManagerModel model) {
        return new ContestBundleManager.Builder()
                .userJid(model.userJid)
                .build();
    }
}
