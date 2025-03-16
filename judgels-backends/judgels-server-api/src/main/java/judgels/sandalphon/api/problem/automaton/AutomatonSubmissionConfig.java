package judgels.sandalphon.api.problem.automaton;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import java.util.Map;
import judgels.gabriel.api.AutomatonRestriction;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableAutomatonSubmissionConfig.class)
public interface AutomatonSubmissionConfig {
    Map<String, String> getSourceKeys();
    String getGradingEngine();
    AutomatonRestriction getAutomatonRestriction();
    Instant getGradingLastUpdateTime();

    class Builder extends ImmutableAutomatonSubmissionConfig.Builder {}
}
