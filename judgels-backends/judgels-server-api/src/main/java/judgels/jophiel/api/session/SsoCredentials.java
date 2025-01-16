package judgels.jophiel.api.session;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSSOCredentials.class)
public interface SsoCredentials {
    String getTicket();
    String getServiceUrl();

    static SsoCredentials of(String ticket, String serviceUrl) {
        return ImmutableSSOCredentials.builder()
                .ticket(ticket)
                .serviceUrl(serviceUrl)
                .build();
    }
}
