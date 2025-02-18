package judgels.uriel.api.bundle.manager;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestBundleManagersUpsertResponse.class)
public interface ContestBundleManagersUpsertResponse {
    Map<String, Profile> getInsertedManagerProfilesMap();
    Map<String, Profile> getAlreadyManagerProfilesMap();

    class Builder extends ImmutableContestBundleManagersUpsertResponse.Builder {}
}
