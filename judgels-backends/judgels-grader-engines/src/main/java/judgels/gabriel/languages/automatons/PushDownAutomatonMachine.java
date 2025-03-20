package judgels.gabriel.languages.automatons;

import com.google.common.collect.ImmutableList;
import java.util.List;
import judgels.gabriel.api.AutomatonMachine;

public class PushDownAutomatonMachine implements AutomatonMachine {
    @Override
    public String getName() {
        return "Push Down Automaton";
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public boolean isAutomaton() {
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
            executableFilename,
            "-tPDA");
    }
}
