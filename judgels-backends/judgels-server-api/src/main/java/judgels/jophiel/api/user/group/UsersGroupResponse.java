package judgels.jophiel.api.user.group;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.jophiel.api.user.User;
import judgels.persistence.api.Page;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUsersGroupResponse.class)
public interface UsersGroupResponse {
    Page<User> getData();
    Map<String, List<String>> getGroupsMap();

    class Builder extends ImmutableUsersGroupResponse.Builder {}
}
