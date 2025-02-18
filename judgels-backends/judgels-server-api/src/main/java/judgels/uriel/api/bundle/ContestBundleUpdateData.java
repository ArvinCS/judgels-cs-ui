package judgels.uriel.api.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestBundleUpdateData.class)
public interface ContestBundleUpdateData {
    Optional<String> getSlug();
    Optional<String> getName();
    Optional<String> getDescription();

    class Builder extends ImmutableContestBundleUpdateData.Builder {}
}
