package judgels.uriel.api.bundle.contestant;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestBundleContestant.class)
public interface ContestBundleContestant {
    String getUserJid();

    class Builder extends ImmutableContestBundleContestant.Builder {}
}
