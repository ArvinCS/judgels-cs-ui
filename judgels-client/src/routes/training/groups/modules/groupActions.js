import { groupAPI } from '../../../../modules/api/jophiel/group';
import { selectToken } from '../../../../modules/session/sessionSelectors';

import { toastActions } from '../../../../modules/toast/toastActions';

export function getGroupOfUsers(page, name, orderBy, orderDir) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await groupAPI.getGroupOfUsers(token, page, name, orderBy, orderDir);
  };
}

export function getGroup(userJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await groupAPI.getGroup(token, userJid);
  };
}

export function updateGroup(userJid, data) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    try {
      await groupAPI.updateGroup(token, userJid, data);
    } catch (error) {
      throw error;
    }
    toastActions.showSuccessToast("User's group updated.");
  };
}

export function upsertUsersWithGroups(usernames, groups) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    try {
      await groupAPI.upsertUsersWithGroups(token, usernames, groups);
    } catch (error) {
      throw error;
    }
    toastActions.showSuccessToast("User's group updated.");
  };
}

export function deleteUsersWithGroups(usernames, groups) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    try {
      await groupAPI.deleteUsersWithGroups(token, usernames, groups);
    } catch (error) {
      throw error;
    }
    toastActions.showSuccessToast("User's group deleted.");
  };
}
