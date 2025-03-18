package judgels.jophiel.api.user.account;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSsoUserRegistrationData.class)
public interface SsoUserRegistrationData {
    String getUsername();
    String getEmail();
    String getStudentId();

    class Builder extends ImmutableSsoUserRegistrationData.Builder {}
}
