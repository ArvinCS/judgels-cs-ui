package judgels.uriel.api.bundle.manager;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.persistence.api.Page;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestBundleManagersResponse.class)
public interface ContestBundleManagersResponse {
    Page<ContestBundleManager> getData();
    Map<String, Profile> getProfilesMap();
    ContestBundleManagerConfig getConfig();

    class Builder extends ImmutableContestBundleManagersResponse.Builder {}
}
