package judgels.jophiel.api.session;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSSOCredentials.class)
public interface SSOCredentials {
    String getTicket();
    String getServiceUrl();

    static SSOCredentials of(String ticket, String serviceUrl) {
        return ImmutableSSOCredentials.builder()
                .ticket(ticket)
                .serviceUrl(serviceUrl)
                .build();
    }
}
