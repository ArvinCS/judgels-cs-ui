package judgels.gabriel.postgrelate;

import java.nio.file.Path;

import org.immutables.value.Value;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutablePostgrelateConfiguration.class)
public interface PostgrelateConfiguration {
    Path getBaseDir();

    class Builder extends ImmutablePostgrelateConfiguration.Builder {}
}
