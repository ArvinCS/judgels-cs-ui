package judgels.sandalphon.api.problem.automaton;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableAutomatonSkeleton.class)
public interface AutomatonSkeleton {
    Set<String> getLanguages();
    byte[] getContent();

    class Builder extends ImmutableAutomatonSkeleton.Builder {}
}
