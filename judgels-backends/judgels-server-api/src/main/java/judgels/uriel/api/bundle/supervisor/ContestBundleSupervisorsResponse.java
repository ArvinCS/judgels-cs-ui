package judgels.uriel.api.bundle.supervisor;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.persistence.api.Page;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestBundleSupervisorsResponse.class)
public interface ContestBundleSupervisorsResponse {
    Page<ContestBundleSupervisor> getData();

    Map<String, Profile> getProfilesMap();

    class Builder extends ImmutableContestBundleSupervisorsResponse.Builder {
    }
}
