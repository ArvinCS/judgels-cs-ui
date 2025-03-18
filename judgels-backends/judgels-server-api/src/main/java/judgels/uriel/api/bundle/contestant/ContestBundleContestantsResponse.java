package judgels.uriel.api.bundle.contestant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.persistence.api.Page;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestBundleContestantsResponse.class)
public interface ContestBundleContestantsResponse {
    Page<ContestBundleContestant> getData();

    Map<String, Profile> getProfilesMap();

    class Builder extends ImmutableContestBundleContestantsResponse.Builder {
    }
}
