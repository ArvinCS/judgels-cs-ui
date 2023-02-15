package judgels.michael.login;

import io.dropwizard.hibernate.UnitOfWork;
import java.net.URI;
import java.time.Duration;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.api.user.User;
import judgels.jophiel.session.SessionStore;
import judgels.jophiel.session.SessionTokenGenerator;
import judgels.jophiel.user.UserStore;
import judgels.jophiel.user.account.UserRegistrationEmailStore;
import judgels.michael.BaseResource;
import judgels.michael.MichaelConfiguration;
import judgels.michael.template.HtmlTemplate;

@Path("/login")
@Produces(MediaType.TEXT_HTML)
public class LoginResource extends BaseResource {
    private static final URI POST_LOGIN_URI = URI.create("/problems");

    private final SessionStore sessionStore;
    private final UserStore userStore;
    private final UserRegistrationEmailStore userRegistrationEmailStore;

    @Inject
    public LoginResource(
            MichaelConfiguration config,
            SessionStore sessionStore,
            UserStore userStore,
            UserRegistrationEmailStore userRegistrationEmailStore) {

        super(config);
        this.sessionStore = sessionStore;
        this.userStore = userStore;
        this.userRegistrationEmailStore = userRegistrationEmailStore;
    }

    @GET
    @UnitOfWork(readOnly = true)
    public Response logIn(@CookieParam("JOPHIEL_TOKEN") String token) {
        if (token != null) {
            if (sessionStore.getSessionByToken(token).isPresent()) {
                return Response.seeOther(POST_LOGIN_URI).build();
            }
        }

        HtmlTemplate template = newTemplate();
        return renderLogIn(template);
    }

    @POST
    @UnitOfWork
    public Response postLogIn(
            @Context UriInfo uriInfo,
            @FormParam("username") String username,
            @FormParam("password") String password) {

        HtmlTemplate template = newTemplate();

        Optional<User> maybeUser = userStore.getUserByUsernameAndPassword(username, password);
        if (!maybeUser.isPresent()) {
            template.setGlobalFormErrorMessage("Username or password incorrect.");
            return renderLogIn(template);
        }

        User user = maybeUser.get();
        if (!userRegistrationEmailStore.isUserActivated(user.getJid())) {
            template.setGlobalFormErrorMessage("Username or password incorrect.");
            return renderLogIn(template);
        }

        Session session = sessionStore.createSession(SessionTokenGenerator.newToken(), user.getJid());
        return Response
                .seeOther(POST_LOGIN_URI)
                .cookie(new NewCookie(
                        "JOPHIEL_TOKEN",
                        session.getToken(),
                        "/",
                        uriInfo.getBaseUri().getHost(),
                        null,
                        (int) Duration.ofDays(7).getSeconds(),
                        false,
                        true))
                .build();
    }

    private Response renderLogIn(HtmlTemplate template) {
        template.setSingleColumn();
        template.setTitle("Log in");
        return renderView(new LoginView(template));
    }
}
