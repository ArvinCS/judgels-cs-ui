import { selectToken } from '../../../../../modules/session/sessionSelectors';
import { contestContestantAPI } from '../../../../../modules/api/uriel/contestContestant';
import * as toastActions from '../../../../../modules/toast/toastActions';

export function getMyContestantState(contestJid: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestContestantAPI.getMyContestantState(token, contestJid);
  };
}

export function getContestants(contestJid: string, page?: number) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestContestantAPI.getContestants(token, contestJid, page);
  };
}

export function getApprovedContestantsCount(contestJid: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestContestantAPI.getApprovedContestantsCount(token, contestJid);
  };
}

export function getApprovedContestants(contestJid: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestContestantAPI.getApprovedContestants(token, contestJid);
  };
}

export function registerMyselfAsContestant(contestJid: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await contestContestantAPI.registerMyselfAsContestant(token, contestJid);
    toastActions.showSuccessToast('Successfully registered to the contest.');
  };
}

export function unregisterMyselfAsContestant(contestJid: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await contestContestantAPI.unregisterMyselfAsContestant(token, contestJid);
    toastActions.showSuccessToast('Successfully unregistered from the contest.');
  };
}

export function upsertContestants(contestJid: string, usernames: string[]) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const response = await contestContestantAPI.upsertContestants(token, contestJid, usernames);
    if (Object.keys(response.insertedContestantProfilesMap).length === usernames.length) {
      toastActions.showSuccessToast('Contestants added.');
    }
    return response;
  };
}

export function deleteContestants(contestJid: string, usernames: string[]) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const response = await contestContestantAPI.deleteContestants(token, contestJid, usernames);
    if (Object.keys(response.deletedContestantProfilesMap).length === usernames.length) {
      toastActions.showSuccessToast('Contestants removed.');
    }
    return response;
  };
}
