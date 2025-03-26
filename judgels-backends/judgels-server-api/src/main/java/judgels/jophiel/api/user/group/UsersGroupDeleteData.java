package judgels.jophiel.api.user.group;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUsersGroupDeleteData.class)
public interface UsersGroupDeleteData {
    Set<String> getUsernames();
    Set<String> getGroups();

    static UsersGroupDeleteData of(Set<String> usernames, Set<String> groups) {
        return ImmutableUsersGroupDeleteData.builder()
                .usernames(usernames)
                .groups(groups)
                .build();
    }
}
