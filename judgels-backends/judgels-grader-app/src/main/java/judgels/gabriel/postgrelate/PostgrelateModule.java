package judgels.gabriel.postgrelate;

import dagger.Module;
import dagger.Provides;
import java.util.Optional;
import judgels.gabriel.sandboxes.postgrelate.PostgrelateSandboxFactory;

@Module
public class PostgrelateModule {
    private final Optional<PostgrelateConfiguration> config;

    public PostgrelateModule(Optional<PostgrelateConfiguration> config) {
        this.config = config;
    }

    @Provides
    Optional<PostgrelateSandboxFactory> sandboxFactory() {
        return config.map(config -> new PostgrelateSandboxFactory(config.getBaseDir()));
    }
}
