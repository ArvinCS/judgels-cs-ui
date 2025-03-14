import { contestBundleContestantAPI } from '../../../../modules/api/uriel/contestBundleContestant';
import { selectToken } from '../../../../modules/session/sessionSelectors';

import * as toastActions from '../../../../modules/toast/toastActions';

export function getContestBundleContestantsByJid(contestBundleJid, page) {
    return async (dispatch, getState) => {
        const token = selectToken(getState());
        const contestants = await contestBundleContestantAPI.getContestants(token, contestBundleJid, page);

        return contestants;
    }
}

export function upsertContestants(bundleJid, usernames) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const response = await contestBundleContestantAPI.upsertContestants(token, bundleJid, usernames);
    toastActions.showSuccessToast('Contestants added.');
    return response;
  };
}

export function deleteContestants(bundleJid, usernames) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const response = await contestBundleContestantAPI.deleteContestants(token, bundleJid, usernames);
    toastActions.showSuccessToast('Contestants removed.');
    return response;
  };
}