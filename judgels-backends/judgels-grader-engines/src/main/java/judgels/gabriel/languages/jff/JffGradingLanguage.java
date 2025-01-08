package judgels.gabriel.languages.jff;

import com.google.common.collect.ImmutableList;
import java.util.List;
import judgels.gabriel.api.GradingLanguage;

public class JffGradingLanguage implements GradingLanguage {
    @Override
    public String getName() {
        return "Jff";
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public List<String> getAllowedExtensions() {
        return ImmutableList.of("jff");
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
        return ImmutableList.of(
            "/usr/bin/java", 
            "-Djava.awt.headless=true",
            "-jar", 
            "/usr/bin/jflap-core.jar", 
            "runonce",
            executableFilename);
    }
}
