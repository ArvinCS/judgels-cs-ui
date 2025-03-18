package judgels.gabriel.engines;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import judgels.gabriel.api.GradingConfig;
import org.immutables.value.Value;

public interface SingleSourceFileGradingConfig extends GradingConfig {
    @JsonIgnore
    @Override
    @Value.Default
    default Map<String, String> getSourceFileFields() {
        return ImmutableMap.of("source", "Source code");
    }
}
