import { push } from 'connected-react-router';

import { ForbiddenError } from '../../../modules/api/error';
import { sessionAPI } from '../../../modules/api/jophiel/session';
import { userAccountAPI } from '../../../modules/api/jophiel/userAccount';

import { afterLogin } from '../login/modules/loginActions';

export function logIn(data) {
  return async dispatch => {
    let session;
    try {
      session = await sessionAPI.logInWithSSO(data);
    } catch (error) {
      if (error instanceof ForbiddenError) {
        return false;
      }
      throw error;
    }
    await dispatch(afterLogin(session));
  };
}
