package judgels.sandalphon.api.problem.automaton;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableAutomatonLimits.class)
public interface AutomatonLimits {
    int getTimeLimit();
    int getMemoryLimit();

    class Builder extends ImmutableAutomatonLimits.Builder {}
}
