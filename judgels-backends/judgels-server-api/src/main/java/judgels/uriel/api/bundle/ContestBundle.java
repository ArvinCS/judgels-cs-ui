package judgels.uriel.api.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestBundle.class)
public interface ContestBundle {
    long getId();
    String getJid();
    String getSlug();
    String getName();
    String getDescription();

    class Builder extends ImmutableContestBundle.Builder {}
}
