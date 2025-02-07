import { APP_CONFIG } from '../../../conf';
import { post } from '../http';

export const SessionErrors = {
  UserNotActivated: 'Jophiel:UserNotActivated',
  UserMaxConcurrentSessionsExceeded: 'Jophiel:UserMaxConcurrentSessionsExceeded',
  LogoutDisabled: 'Jophiel:LogoutDisabled',
  TicketInvalid: 'Jophiel:TicketInvalid',
};

const baseUrl = `${APP_CONFIG.apiUrl}/session`;

export const sessionAPI = {
  logIn: (usernameOrEmail, password) => {
    return post(`${baseUrl}/login`, undefined, { usernameOrEmail, password });
  },

  logInWithGoogle: idToken => {
    return post(`${baseUrl}/login-google`, undefined, { idToken });
  },

  logInWithSSO: data => {
    return post(`${baseUrl}/login-sso`, undefined, data);
  },

  logOut: token => {
    return post(`${baseUrl}/logout`, token);
  },
};
