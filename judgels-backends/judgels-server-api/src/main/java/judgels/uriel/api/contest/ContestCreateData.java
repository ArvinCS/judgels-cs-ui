package judgels.uriel.api.contest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Optional;

import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestCreateData.class)
public interface ContestCreateData {
    String getSlug();
    Optional<String> getBundleJid();

    class Builder extends ImmutableContestCreateData.Builder {}
}
