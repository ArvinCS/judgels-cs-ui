package judgels.gabriel.engines.automata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import judgels.gabriel.aggregators.SumAggregator;
import judgels.gabriel.api.Aggregator;
import judgels.gabriel.api.Compiler;
import judgels.gabriel.api.Evaluator;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.GradingLanguage;
import judgels.gabriel.api.GradingOptions;
import judgels.gabriel.api.PreparationException;
import judgels.gabriel.api.Sandbox;
import judgels.gabriel.api.SandboxFactory;
import judgels.gabriel.api.Scorer;
import judgels.gabriel.api.TestGroup;
import judgels.gabriel.compilers.SingleSourceFileCompiler;
import judgels.gabriel.engines.BlackboxGradingEngine;
import judgels.gabriel.engines.DefaultConfig;
import judgels.gabriel.helpers.scorer.ScorerRegistry;

public class AutomataGradingEngine extends BlackboxGradingEngine {
    private final SingleSourceFileCompiler compiler;
    private final AutomataEvaluator evaluator;
    private final SumAggregator aggregator;

    private Sandbox compilerSandbox;
    private Sandbox evaluatorSandbox;
    private Sandbox scorerSandbox;

    public AutomataGradingEngine() {
        this.compiler = new SingleSourceFileCompiler();
        this.evaluator = new AutomataEvaluator();
        this.aggregator = new SumAggregator();
    }

    @Override
    public String getName() {
        return "Automata";
    }

    @Override
    public GradingConfig createDefaultConfig() {
        return new AutomataGradingConfig.Builder()
                .timeLimit(DefaultConfig.TIME_LIMIT)
                .memoryLimit(DefaultConfig.MEMORY_LIMIT)
                .addTestData(TestGroup.of(0, ImmutableList.of()))
                .addTestData(TestGroup.of(-1, ImmutableList.of()))
                .build();
    }

    @Override
    public GradingConfig parseConfig(ObjectMapper mapper, String json) throws IOException {
        return mapper.readValue(json, AutomataGradingConfig.class);
    }

    @Override
    public Compiler getCompiler() {
        return compiler;
    }

    @Override
    public Evaluator getEvaluator() {
        return evaluator;
    }

    @Override
    public Aggregator getAggregator() {
        return aggregator;
    }

    @Override
    public void prepare(
            GradingConfig config,
            GradingOptions options,
            GradingLanguage language,
            Map<String, File> sourceFiles,
            Map<String, File> helperFiles,
            SandboxFactory sandboxFactory,
            File compilationDir,
            File evaluationDir) throws PreparationException {

        AutomataGradingConfig cfg = (AutomataGradingConfig) config;

        String sourceKey = config.getSourceFileFields().keySet().iterator().next();
        File sourceFile = sourceFiles.get(sourceKey);

        compilerSandbox = sandboxFactory.newSandbox();
        compiler.prepare(compilerSandbox, compilationDir, language);

        Scorer scorer = ScorerRegistry.getAndPrepare(Optional.empty(), helperFiles, scorerSandbox, evaluationDir);

        evaluatorSandbox = sandboxFactory.newSandbox();

        boolean shouldRevealEvaluation = options.getShouldRevealEvaluation()
                && config.getTestData().size() == 2
                && config.getTestData().get(0).getTestCases().isEmpty() // no sample test data
                && config.getTestData().get(1).getTestCases().size() <= 3; // official test data consists of at most 3 test cases

        evaluator.prepare(
                evaluatorSandbox,
                scorer,
                compilationDir,
                evaluationDir,
                language,
                sourceFile,
                cfg.getTimeLimit(),
                cfg.getMemoryLimit(),
                shouldRevealEvaluation);
    }

    @Override
    public void cleanUp() {
        if (compilerSandbox != null) {
            compilerSandbox.cleanUp();
        }
        if (evaluatorSandbox != null) {
            evaluatorSandbox.cleanUp();
        }
        if (scorerSandbox != null) {
            scorerSandbox.cleanUp();
        }
    }
}
