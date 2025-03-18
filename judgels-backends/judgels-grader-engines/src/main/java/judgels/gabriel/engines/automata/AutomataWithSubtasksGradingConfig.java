package judgels.gabriel.engines.automata;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.gabriel.engines.SingleSourceFileWithSubtasksGradingConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableAutomataWithSubtasksGradingConfig.class)
public interface AutomataWithSubtasksGradingConfig extends SingleSourceFileWithSubtasksGradingConfig  {
    @JsonInclude(Include.NON_ABSENT)
    Optional<String> getCustomScorer();

    class Builder extends ImmutableAutomataWithSubtasksGradingConfig.Builder {}
}
