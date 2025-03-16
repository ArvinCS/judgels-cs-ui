package judgels.gabriel.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.HashSet;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableAutomatonRestriction.class)
public interface AutomatonRestriction {
    Set<String> getAllowedAutomatonNames();

    @JsonIgnore
    default Set<String> getAllowedAutomatons() {
        return getAllowedAutomatonNames();
    }

    @JsonIgnore
    default boolean isAllowedAll() {
        return getAllowedAutomatons().isEmpty();
    }

    static AutomatonRestriction noRestriction() {
        return ImmutableAutomatonRestriction.builder().build();
    }

    static AutomatonRestriction of(Set<String> allowedAutomatonNames) {
        return ImmutableAutomatonRestriction.builder().allowedAutomatonNames(allowedAutomatonNames).build();
    }

    static AutomatonRestriction combine(AutomatonRestriction r1, AutomatonRestriction r2) {
        if (r1.isAllowedAll()) {
            return r2;
        }
        if (r2.isAllowedAll()) {
            return r1;
        }
        Set<String> result = new HashSet<>(r1.getAllowedAutomatons());
        result.retainAll(r2.getAllowedAutomatons());
        return AutomatonRestriction.of(result);
    }
}
