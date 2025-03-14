package judgels.gabriel.sandboxes.postgrelate;

import java.nio.file.Path;
import judgels.gabriel.api.Sandbox;
import judgels.gabriel.api.SandboxFactory;
import judgels.gabriel.api.SandboxInteractor;

public class PostgrelateSandboxFactory implements SandboxFactory {
    private final Path postgrelatePath;

    public PostgrelateSandboxFactory(Path basePostgrelateDir) {
        this.postgrelatePath = basePostgrelateDir;
    }

    @Override
    public Sandbox newSandbox() {
        return new PostgrelateSandbox(postgrelatePath.toString(), PostgrelateBoxIdFactory.newBoxId());
    }

    @Override
    public SandboxInteractor newSandboxInteractor() {
        return null;
    }
}
