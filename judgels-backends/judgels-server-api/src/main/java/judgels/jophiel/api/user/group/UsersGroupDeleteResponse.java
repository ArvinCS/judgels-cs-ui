package judgels.jophiel.api.user.group;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUsersGroupDeleteResponse.class)
public interface UsersGroupDeleteResponse {
    Map<String, Set<String>> getDeletedGroupUsernamesMap();
    Map<String, Set<String>> getNotGroupUsernamesMap();

    class Builder extends ImmutableUsersGroupDeleteResponse.Builder {}
}
