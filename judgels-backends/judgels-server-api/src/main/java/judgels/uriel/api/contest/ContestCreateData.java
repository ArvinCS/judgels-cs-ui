package judgels.uriel.api.contest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import java.util.Set;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestCreateData.class)
public interface ContestCreateData {
    String getSlug();
    Optional<String> getBundleJid();
    Optional<Boolean> getIsInsertDefaultSupervisor();
    Optional<Set<SupervisorManagementPermission>> getSupervisorPermissions();
    Optional<Boolean> getIsInsertDefaultContestant();

    class Builder extends ImmutableContestCreateData.Builder {}
}
