package judgels.uriel.api.bundle.manager;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestBundleManagersDeleteResponse.class)
public interface ContestBundleManagersDeleteResponse {
    Map<String, Profile> getDeletedManagerProfilesMap();
    Map<String, Profile> getNotManagerProfilesMap();

    class Builder extends ImmutableContestBundleManagersDeleteResponse.Builder {}
}
