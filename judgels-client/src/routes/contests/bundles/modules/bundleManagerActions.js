import { contestBundleManagerAPI } from '../../../../modules/api/uriel/contestBundleManager';
import { selectToken } from '../../../../modules/session/sessionSelectors';

import * as toastActions from '../../../../modules/toast/toastActions';

export function getContestBundleManagersByJid(contestBundleJid, page) {
    return async (dispatch, getState) => {
        const token = selectToken(getState());
        const managers = await contestBundleManagerAPI.getManagers(token, contestBundleJid, page);

        return managers;
    }
}

export function upsertManagers(bundleJid, usernames) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const response = await contestBundleManagerAPI.upsertManagers(token, bundleJid, usernames);
    toastActions.showSuccessToast('Managers added.');
    return response;
  };
}

export function deleteManagers(bundleJid, usernames) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const response = await contestBundleManagerAPI.deleteManagers(token, bundleJid, usernames);
    toastActions.showSuccessToast('Managers removed.');
    return response;
  };
}