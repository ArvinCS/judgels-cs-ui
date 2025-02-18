package judgels.uriel.api.bundle.manager;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestBundleManagerConfig.class)
public interface ContestBundleManagerConfig {
    boolean getCanManage();

    class Builder extends ImmutableContestBundleManagerConfig.Builder {}
}
