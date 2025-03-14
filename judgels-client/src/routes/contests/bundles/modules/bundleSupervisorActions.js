import { contestBundleSupervisorAPI } from '../../../../modules/api/uriel/contestBundleSupervisor';
import { selectToken } from '../../../../modules/session/sessionSelectors';

import * as toastActions from '../../../../modules/toast/toastActions';

export function getContestBundleSupervisorsByJid(contestBundleJid, page) {
    return async (dispatch, getState) => {
        const token = selectToken(getState());
        const supervisors = await contestBundleSupervisorAPI.getSupervisors(token, contestBundleJid, page);

        return supervisors;
    }
}

export function upsertSupervisors(bundleJid, usernames) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const response = await contestBundleSupervisorAPI.upsertSupervisors(token, bundleJid, usernames);
    toastActions.showSuccessToast('Supervisors added.');
    return response;
  };
}

export function deleteSupervisors(bundleJid, usernames) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const response = await contestBundleSupervisorAPI.deleteSupervisors(token, bundleJid, usernames);
    toastActions.showSuccessToast('Supervisors removed.');
    return response;
  };
}