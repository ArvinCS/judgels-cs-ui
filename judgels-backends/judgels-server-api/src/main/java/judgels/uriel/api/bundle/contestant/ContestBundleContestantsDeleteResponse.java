package judgels.uriel.api.bundle.contestant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestBundleContestantsDeleteResponse.class)
public interface ContestBundleContestantsDeleteResponse {
    Map<String, Profile> getDeletedManagerProfilesMap();
    Map<String, Profile> getNotManagerProfilesMap();

    class Builder extends ImmutableContestBundleContestantsDeleteResponse.Builder {}
}
