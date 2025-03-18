package judgels.uriel.api.bundle.supervisor;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestBundleSupervisorsDeleteResponse.class)
public interface ContestBundleSupervisorsDeleteResponse {
    Map<String, Profile> getDeletedManagerProfilesMap();
    Map<String, Profile> getNotManagerProfilesMap();

    class Builder extends ImmutableContestBundleSupervisorsDeleteResponse.Builder {}
}
