package judgels.jophiel.api.user.account;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSSOUserRegistrationData.class)
public interface SsoUserRegistrationData {
    String getUsername();
    String getEmail();

    class Builder extends ImmutableSSOUserRegistrationData.Builder {}
}
