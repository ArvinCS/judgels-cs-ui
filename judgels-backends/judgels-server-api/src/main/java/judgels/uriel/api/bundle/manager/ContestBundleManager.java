package judgels.uriel.api.bundle.manager;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestBundleManager.class)
public interface ContestBundleManager {
    String getUserJid();

    class Builder extends ImmutableContestBundleManager.Builder {}
}
