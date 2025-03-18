package judgels.uriel.bundle;

import com.google.common.collect.Lists;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.uriel.api.bundle.ContestBundle;
import judgels.uriel.api.bundle.ContestBundleCreateData;
import judgels.uriel.api.bundle.ContestBundleErrors;
import judgels.uriel.api.bundle.ContestBundleUpdateData;
import judgels.uriel.persistence.ContestBundleDao;
import judgels.uriel.persistence.ContestBundleDao.ContestBundleQueryBuilder;
import judgels.uriel.persistence.ContestBundleModel;
import judgels.uriel.persistence.ContestBundleModel_;

@Singleton
public class ContestBundleStore {
    private final ContestBundleDao contestBundleDao;

    @Inject
    public ContestBundleStore(ContestBundleDao contestBundleDao) {
        this.contestBundleDao = contestBundleDao;
    }

    public Page<ContestBundle> getContestBundles(Optional<String> userJid, Optional<String> nameFilter, int pageNumber, int pageSize) {
        ContestBundleQueryBuilder query = contestBundleDao.select();

        if (userJid.isPresent()) {
            query.whereUserCanView(userJid.get());
        }
        if (nameFilter.isPresent()) {
            query.whereNameLike(nameFilter.get());
        }

        return query
                .orderBy(ContestBundleModel_.UPDATED_AT, OrderDir.DESC)
                .paged(pageNumber, pageSize)
                .mapPage(p -> Lists.transform(p, ContestBundleStore::fromModel));
    }

    public Optional<ContestBundle> getContestBundleByJid(String jid) {
        return contestBundleDao.selectByJid(jid).map(ContestBundleStore::fromModel);
    }

    public Optional<ContestBundle> getContestBundleBySlug(String slug) {
        return contestBundleDao.selectBySlug(slug).map(ContestBundleStore::fromModel);
    }

    public ContestBundle createContestBundle(ContestBundleCreateData contestBundleCreateData) {
        if (contestBundleDao.selectBySlug(contestBundleCreateData.getSlug()).isPresent()) {
            throw ContestBundleErrors.slugAlreadyExists(contestBundleCreateData.getSlug());
        }

        ContestBundleModel model = new ContestBundleModel();
        model.slug = contestBundleCreateData.getSlug();
        model.name = contestBundleCreateData.getSlug();
        model.description = "";

        return fromModel(contestBundleDao.insert(model));
    }

    public ContestBundle updateContestBundle(String bundleJid, ContestBundleUpdateData data) {
        ContestBundleModel model = contestBundleDao.findByJid(bundleJid);
        if (data.getSlug().isPresent()) {
            String newSlug = data.getSlug().get();
            if (model.slug == null || !model.slug.equals(newSlug)) {
                if (contestBundleDao.selectBySlug(newSlug).isPresent()) {
                    throw ContestBundleErrors.slugAlreadyExists(newSlug);
                }
            }
        }

        data.getSlug().ifPresent(slug -> model.slug = slug);
        data.getName().ifPresent(name -> model.name = name);
        data.getDescription().ifPresent(description -> model.description = description);
        return fromModel(contestBundleDao.update(model));
    }

    private static ContestBundle fromModel(ContestBundleModel model) {
        return new ContestBundle.Builder()
                .id(model.id)
                .jid(model.jid)
                .slug(model.slug)
                .name(model.name)
                .description(model.description)
                .build();
    }
}
