package judgels.gabriel.languages.automatons;

import com.google.common.collect.ImmutableList;
import java.util.List;
import judgels.gabriel.api.AutomatonMachine;

public class FiniteStateAutomatonMachine implements AutomatonMachine {
    @Override
    public String getName() {
        return "Finite State Automaton";
    }

    @Override
    public boolean isVisible() {
        return true;
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
            "-tFSA");
    }
}
