package judgels.jophiel.api.user.group;

import java.util.List;
import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserGroup.class)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public interface UserGroup {
    Optional<String> getUserJid();
    Optional<List<String>> getGroups();

    class Builder extends ImmutableUserGroup.Builder {}
}
