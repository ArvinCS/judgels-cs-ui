package judgels.gabriel.languages.sql;

import com.google.common.collect.ImmutableList;
import java.util.List;
import judgels.gabriel.api.GradingLanguage;

public class SqlGradingLanguage implements GradingLanguage {
    @Override
    public String getName() {
        return "SQL";
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public List<String> getAllowedExtensions() {
        return ImmutableList.of("sql");
    }

    @Override
    public List<String> getCompilationCommand(String sourceFilename, String... sourceFilenames) {
        return ImmutableList.of();
    }

    @Override
    public String getExecutableFilename(String sourceFilename) {
        return sourceFilename;
    }

    @Override
    public List<String> getExecutionCommand(String sourceFilename) {
        String executableFilename = getExecutableFilename(sourceFilename);
        return ImmutableList.of(executableFilename);
    }
}
