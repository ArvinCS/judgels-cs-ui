package judgels.jophiel.api.user.group;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUsersGroupUpsertResponse.class)
public interface UsersGroupUpsertResponse {
    Map<String, Set<String>> getInsertedGroupUsernamesMap();
    Map<String, Set<String>> getAlreadyGroupUsernamesMap();

    class Builder extends ImmutableUsersGroupUpsertResponse.Builder {}
}
