import { ForbiddenError } from '../../../modules/api/error';
import { sessionAPI } from '../../../modules/api/jophiel/session';
import { SessionErrors } from '../../../modules/api/jophiel/session';

import { afterLogin } from '../login/modules/loginActions';

export function logIn(data) {
  return async dispatch => {
    let session;
    try {
      session = await sessionAPI.logInWithSSO(data);
    } catch (error) {
      if (error.message === SessionErrors.UserNotAllowed) {
        throw new Error('Login failed because you are not allowed.');
      } else if (error instanceof ForbiddenError) {
        return false;
      }
      throw error;
    }
    await dispatch(afterLogin(session));
  };
}
