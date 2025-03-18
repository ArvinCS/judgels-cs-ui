package judgels.uriel.api.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestBundleConfig.class)
public interface ContestBundleConfig {
    boolean getCanAdminister();

    class Builder extends ImmutableContestBundleConfig.Builder {}
}
