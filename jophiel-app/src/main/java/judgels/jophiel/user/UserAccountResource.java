package judgels.jophiel.user;

import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserAccountService;
import judgels.jophiel.api.user.password.PasswordResetData;
import judgels.jophiel.api.user.registration.UserRegistrationData;
import judgels.jophiel.user.password.UserPasswordResetter;
import judgels.jophiel.user.registration.UserRegisterer;

public class UserAccountResource implements UserAccountService {
    private final UserStore userStore;
    private final Optional<UserRegisterer> userRegisterer;
    private final Optional<UserPasswordResetter> userPasswordResetter;

    @Inject
    public UserAccountResource(
            UserStore userStore,
            Optional<UserRegisterer> userRegisterer,
            Optional<UserPasswordResetter> userPasswordResetter) {

        this.userStore = userStore;
        this.userRegisterer = userRegisterer;
        this.userPasswordResetter = userPasswordResetter;
    }

    @Override
    @UnitOfWork
    public User registerUser(UserRegistrationData userRegistrationData) {
        return checkFound(userRegisterer).register(userRegistrationData);
    }

    @Override
    @UnitOfWork
    public void activateUser(String emailCode) {
        checkFound(userRegisterer).activate(emailCode);
    }

    @Override
    @UnitOfWork
    public void requestToResetPassword(String email) {
        User user = checkFound(userStore.findUserByEmail(email));
        checkFound(userPasswordResetter).request(user, email);
    }

    @Override
    @UnitOfWork
    public void resetPassword(PasswordResetData passwordResetData) {
        checkFound(userPasswordResetter).reset(passwordResetData);
    }
}
