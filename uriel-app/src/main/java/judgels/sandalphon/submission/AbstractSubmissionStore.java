package judgels.sandalphon.submission;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import judgels.gabriel.api.GradingResultDetails;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.sandalphon.api.submission.Grading;
import judgels.sandalphon.api.submission.Submission;
import judgels.sandalphon.persistence.AbstractGradingModel;
import judgels.sandalphon.persistence.AbstractSubmissionModel;
import judgels.sandalphon.persistence.BaseGradingDao;
import judgels.sandalphon.persistence.BaseSubmissionDao;

public abstract class AbstractSubmissionStore<SM extends AbstractSubmissionModel, GM extends AbstractGradingModel> {
    private final BaseSubmissionDao<SM> submissionDao;
    private final BaseGradingDao<GM> gradingDao;
    private final ObjectMapper mapper;

    public AbstractSubmissionStore(
            BaseSubmissionDao<SM> submissionDao,
            BaseGradingDao<GM> gradingDao,
            ObjectMapper mapper) {

        this.submissionDao = submissionDao;
        this.gradingDao = gradingDao;
        this.mapper = mapper;
    }

    public Optional<Submission> findSubmissionById(long submissionId) {
        return submissionDao.select(submissionId).map(model -> {
            Optional<GM> gradingModel = gradingDao.selectLatestBySubmissionJid(model.jid);
            return submissionFromModels(model, gradingModel.orElse(null));
        });
    }

    public Page<Submission> getSubmissions(String containerJid, String userJid, SelectionOptions options) {
        Page<SM> submissionModels = submissionDao.selectPaged(containerJid, userJid, options);
        Set<String> submissionJids = submissionModels.getData().stream().map(m -> m.jid).collect(Collectors.toSet());
        Map<String, GM> gradingModels = gradingDao.selectAllLatestBySubmissionJids(submissionJids);

        return submissionModels.mapData(data ->
                Lists.transform(data, sm -> submissionFromModels(sm, gradingModels.get(sm.jid))));
    }

    public Submission submissionFromModels(SM model, GM gradingModel) {
        return new Submission.Builder()
                .id(model.id)
                .jid(model.jid)
                .userJid(model.createdBy)
                .problemJid(model.problemJid)
                .containerJid(model.containerJid)
                .gradingEngine(model.gradingEngine)
                .gradingLanguage(model.gradingLanguage)
                .time(model.createdAt)
                .latestGrading(gradingFromModel(gradingModel))
                .build();
    }

    public Optional<Grading> gradingFromModel(@Nullable GM model) {
        if (model == null) {
            return Optional.empty();
        }

        Grading.Builder grading = new Grading.Builder()
                .id(model.id)
                .jid(model.jid)
                .verdict(model.verdictCode)
                .score(model.score);

        if (model.details != null) {
            RawGradingResultDetails raw;
            try {
                raw = mapper.readValue(model.details, RawGradingResultDetails.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Map<String, byte[]> compilationOutputs = raw.getCompilationOutputs().entrySet()
                    .stream()
                    .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getBytes()));

            grading.details(new GradingResultDetails.Builder()
                    .compilationOutputs(compilationOutputs)
                    .testDataResults(raw.getTestDataResults())
                    .subtaskResults(raw.getSubtaskResults())
                    .build());
        }

        return Optional.of(grading.build());
    }
}
