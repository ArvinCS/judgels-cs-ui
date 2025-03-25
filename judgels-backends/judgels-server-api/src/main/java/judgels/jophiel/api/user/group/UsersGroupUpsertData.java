package judgels.jophiel.api.user.group;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUsersGroupUpsertData.class)
public interface UsersGroupUpsertData {
    Set<String> getUsernames();
    Set<String> getGroups();

    static UsersGroupUpsertData of(Set<String> usernames, Set<String> groups) {
        return ImmutableUsersGroupUpsertData.builder()
                .usernames(usernames)
                .groups(groups)
                .build();
    }
}
