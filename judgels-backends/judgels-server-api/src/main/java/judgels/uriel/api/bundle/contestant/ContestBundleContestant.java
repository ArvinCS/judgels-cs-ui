package judgels.uriel.api.bundle.contestant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestBundleContestant.class)
public interface ContestBundleContestant {
    String getUserJid();

    class Builder extends ImmutableContestBundleContestant.Builder {}
}
