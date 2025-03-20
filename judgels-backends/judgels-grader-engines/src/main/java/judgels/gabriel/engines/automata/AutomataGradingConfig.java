package judgels.gabriel.engines.automata;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.gabriel.engines.SingleSourceFileWithoutSubtasksGradingConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableAutomataGradingConfig.class)
public interface AutomataGradingConfig extends SingleSourceFileWithoutSubtasksGradingConfig {
    @JsonInclude(Include.NON_ABSENT)
    Optional<String> getCustomScorer();

    class Builder extends ImmutableAutomataGradingConfig.Builder {}
}
