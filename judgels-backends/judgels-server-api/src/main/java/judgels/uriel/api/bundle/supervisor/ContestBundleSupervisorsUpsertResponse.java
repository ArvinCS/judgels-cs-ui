package judgels.uriel.api.bundle.supervisor;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestBundleSupervisorsUpsertResponse.class)
public interface ContestBundleSupervisorsUpsertResponse {
    Map<String, Profile> getInsertedManagerProfilesMap();
    Map<String, Profile> getAlreadyManagerProfilesMap();

    class Builder extends ImmutableContestBundleSupervisorsUpsertResponse.Builder {}
}
