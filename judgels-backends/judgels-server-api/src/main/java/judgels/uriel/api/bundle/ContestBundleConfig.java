package judgels.uriel.api.bundle;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestBundleConfig.class)
public interface ContestBundleConfig {
    boolean getCanAdminister();

    class Builder extends ImmutableContestBundleConfig.Builder {}
}
