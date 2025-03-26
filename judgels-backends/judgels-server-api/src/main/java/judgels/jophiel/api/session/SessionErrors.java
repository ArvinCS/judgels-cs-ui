package judgels.jophiel.api.session;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Response.Status;
import judgels.service.api.JudgelsServiceException;

public class SessionErrors {
    private SessionErrors() {}

    public static final String USER_NOT_ACTIVATED = "Jophiel:UserNotActivated";
    public static final String USER_NOT_ALLOWED = "Jophiel:UserNotAllowed";
    public static final String USER_MAX_CONCURRENT_SESSIONS_EXCEEDED = "Jophiel:UserMaxConcurrentSessionsExceeded";
    public static final String LOGOUT_DISABLED = "Jophiel:LogoutDisabled";
    public static final String TICKET_INVALID = "Jophiel:TicketInvalid";

    public static JudgelsServiceException userNotActivated(String email) {
        Map<String, Object> args = new HashMap<>();
        args.put("email", email);
        return new JudgelsServiceException(Status.FORBIDDEN, USER_NOT_ACTIVATED, args);
    }

    public static JudgelsServiceException userNotAllowed() {
        return new JudgelsServiceException(Status.FORBIDDEN, USER_NOT_ALLOWED);
    }

    public static JudgelsServiceException userMaxConcurrentSessionsExceeded() {
        return new JudgelsServiceException(Status.FORBIDDEN, USER_MAX_CONCURRENT_SESSIONS_EXCEEDED);
    }

    public static JudgelsServiceException logoutDisabled() {
        return new JudgelsServiceException(Status.FORBIDDEN, LOGOUT_DISABLED);
    }

    public static JudgelsServiceException ticketInvalid() {
        return new JudgelsServiceException(Status.FORBIDDEN, TICKET_INVALID);
    }
}
