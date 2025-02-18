package judgels.uriel.api.bundle.manager;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestBundleManager.class)
public interface ContestBundleManager {
    String getUserJid();

    class Builder extends ImmutableContestBundleManager.Builder {}
}
