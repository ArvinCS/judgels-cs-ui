package judgels.uriel.api.bundle.manager;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestBundleManagerConfig.class)
public interface ContestBundleManagerConfig {
    boolean getCanManage();

    class Builder extends ImmutableContestBundleManagerConfig.Builder {}
}
