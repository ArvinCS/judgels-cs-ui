package judgels.uriel.bundle.contestant;

import java.util.Optional;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import judgels.persistence.api.Page;
import judgels.uriel.api.bundle.contestant.ContestBundleContestant;
import judgels.uriel.persistence.ContestBundleContestantDao;
import judgels.uriel.persistence.ContestBundleContestantModel;

public class ContestBundleContestantStore {
    private final ContestBundleContestantDao contestBundleContestantDao;

    @Inject
    public ContestBundleContestantStore(ContestBundleContestantDao contestBundleContestantDao) {
        this.contestBundleContestantDao = contestBundleContestantDao;
    }

    public boolean upsertContestant(String contestBundleJid, String userJid) {
        Optional<ContestBundleContestantModel> maybeModel = contestBundleContestantDao
                .selectByContestBundleJidAndUserJid(contestBundleJid, userJid);
        if (maybeModel.isPresent()) {
            return false;
        }

        ContestBundleContestantModel model = new ContestBundleContestantModel();
        model.bundleJid = contestBundleJid;
        model.userJid = userJid;
        contestBundleContestantDao.insert(model);

        return true;
    }

    public boolean deleteContestant(String contestBundleJid, String userJid) {
        Optional<ContestBundleContestantModel> maybeModel = contestBundleContestantDao
                .selectByContestBundleJidAndUserJid(contestBundleJid, userJid);
        if (!maybeModel.isPresent()) {
            return false;
        }

        contestBundleContestantDao.delete(maybeModel.get());
        return true;
    }

    public Page<ContestBundleContestant> getContestants(String contestBundleJid, int pageNumber, int pageSize) {
        return contestBundleContestantDao
                .selectByContestBundleJid(contestBundleJid)
                .paged(pageNumber, pageSize)
                .mapPage(p -> Lists.transform(p, ContestBundleContestantStore::fromModel));
    }

    private static ContestBundleContestant fromModel(ContestBundleContestantModel model) {
        return new ContestBundleContestant.Builder()
                .userJid(model.userJid)
                .build();
    }
}
