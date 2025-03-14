package judgels.uriel.bundle.supervisor;

import com.google.common.collect.Lists;
import java.util.Optional;
import javax.inject.Inject;
import judgels.persistence.api.Page;
import judgels.uriel.api.bundle.supervisor.ContestBundleSupervisor;
import judgels.uriel.persistence.ContestBundleSupervisorDao;
import judgels.uriel.persistence.ContestBundleSupervisorModel;

public class ContestBundleSupervisorStore {
    private final ContestBundleSupervisorDao contestBundleSupervisorDao;

    @Inject
    public ContestBundleSupervisorStore(ContestBundleSupervisorDao contestBundleSupervisorDao) {
        this.contestBundleSupervisorDao = contestBundleSupervisorDao;
    }

    public boolean upsertSupervisor(String contestBundleJid, String userJid) {
        Optional<ContestBundleSupervisorModel> maybeModel = contestBundleSupervisorDao
                .selectByContestBundleJidAndUserJid(contestBundleJid, userJid);
        if (maybeModel.isPresent()) {
            return false;
        }

        ContestBundleSupervisorModel model = new ContestBundleSupervisorModel();
        model.bundleJid = contestBundleJid;
        model.userJid = userJid;
        contestBundleSupervisorDao.insert(model);

        return true;
    }

    public boolean deleteSupervisor(String contestBundleJid, String userJid) {
        Optional<ContestBundleSupervisorModel> maybeModel = contestBundleSupervisorDao
                .selectByContestBundleJidAndUserJid(contestBundleJid, userJid);
        if (!maybeModel.isPresent()) {
            return false;
        }

        contestBundleSupervisorDao.delete(maybeModel.get());
        return true;
    }

    public Page<ContestBundleSupervisor> getSupervisors(String contestBundleJid, int pageNumber, int pageSize) {
        return contestBundleSupervisorDao
                .selectByContestBundleJid(contestBundleJid)
                .paged(pageNumber, pageSize)
                .mapPage(p -> Lists.transform(p, ContestBundleSupervisorStore::fromModel));
    }

    private static ContestBundleSupervisor fromModel(ContestBundleSupervisorModel model) {
        return new ContestBundleSupervisor.Builder()
                .userJid(model.userJid)
                .build();
    }
}
