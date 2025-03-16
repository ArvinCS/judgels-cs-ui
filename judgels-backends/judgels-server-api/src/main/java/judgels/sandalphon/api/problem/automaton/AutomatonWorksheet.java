package judgels.sandalphon.api.problem.automaton;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.sandalphon.api.problem.ProblemStatement;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableAutomatonWorksheet.class)
public interface AutomatonWorksheet {
    ProblemStatement getStatement();
    AutomatonLimits getLimits();
    AutomatonSubmissionConfig getSubmissionConfig();
    Optional<String> getReasonNotAllowedToSubmit();

    class Builder extends ImmutableAutomatonWorksheet.Builder {}
}
