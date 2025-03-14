package judgels.uriel.api.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.persistence.api.Page;
import judgels.uriel.api.bundle.role.ContestBundleRole;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestBundlesResponse.class)
public interface ContestBundlesResponse {
    Page<ContestBundle> getData();
    Map<String, ContestBundleRole> getRolesMap();
    ContestBundleConfig getConfig();

    class Builder extends ImmutableContestBundlesResponse.Builder {}
}
