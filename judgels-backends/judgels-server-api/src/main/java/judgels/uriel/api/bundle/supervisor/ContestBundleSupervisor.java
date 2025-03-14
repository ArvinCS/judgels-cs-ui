package judgels.uriel.api.bundle.supervisor;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestBundleSupervisor.class)
public interface ContestBundleSupervisor {
    String getUserJid();

    class Builder extends ImmutableContestBundleSupervisor.Builder {}
}
