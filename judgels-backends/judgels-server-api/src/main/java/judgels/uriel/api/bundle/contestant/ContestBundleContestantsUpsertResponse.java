package judgels.uriel.api.bundle.contestant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestBundleContestantsUpsertResponse.class)
public interface ContestBundleContestantsUpsertResponse {
    Map<String, Profile> getInsertedManagerProfilesMap();
    Map<String, Profile> getAlreadyManagerProfilesMap();

    class Builder extends ImmutableContestBundleContestantsUpsertResponse.Builder {}
}
