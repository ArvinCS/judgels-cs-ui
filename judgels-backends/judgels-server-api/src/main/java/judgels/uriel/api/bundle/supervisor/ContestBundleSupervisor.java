package judgels.uriel.api.bundle.supervisor;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestBundleSupervisor.class)
public interface ContestBundleSupervisor {
    String getUserJid();

    class Builder extends ImmutableContestBundleSupervisor.Builder {}
}
