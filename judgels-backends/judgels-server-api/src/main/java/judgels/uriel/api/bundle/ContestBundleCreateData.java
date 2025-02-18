package judgels.uriel.api.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestBundleCreateData.class)
public interface ContestBundleCreateData {
    String getSlug();

    class Builder extends ImmutableContestBundleCreateData.Builder {}
}
