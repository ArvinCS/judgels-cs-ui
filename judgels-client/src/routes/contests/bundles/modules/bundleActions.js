import { push } from 'connected-react-router';

import { BadRequestError } from '../../../../modules/api/error';
import { ContestBundleErrors, contestBundleAPI } from '../../../../modules/api/uriel/contestBundle';
import { selectToken } from '../../../../modules/session/sessionSelectors';

import * as toastActions from '../../../../modules/toast/toastActions';

export function createContestBundle(data) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    try {
      await contestBundleAPI.createContestBundle(token, data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === ContestBundleErrors.SlugAlreadyExists) {
        throw new SubmissionError({ slug: 'Slug already exists' });
      }
      throw error;
    }
    toastActions.showSuccessToast('Contest Bundle created.');
    dispatch(push('/contests/bundle'));
  };
}

export function updateContestBundle(bundleJid, bundleSlug, data) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    try {
      await contestBundleAPI.updateContestBundle(token, bundleJid, data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === ContestBundleErrors.SlugAlreadyExists) {
        throw new SubmissionError({ slug: 'Slug already exists' });
      }
      throw error;
    }
    toastActions.showSuccessToast('Contest Bundle updated.');
  };
}

export function getContestBundles(name, page) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestBundleAPI.getContestBundles(token, name, page);
  };
}

export function getContestBundleBySlug(contestBundleSlug) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const contestBundle = await contestBundleAPI.getContestBundleBySlug(token, contestBundleSlug);
    return contestBundle;
  };
}

export function getContestBundleByJid(contestBundleJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const contestBundle = await contestBundleAPI.getContestBundleByJid(token, contestBundleJid);
    return contestBundle;
  };
}

export function exportScoreboard(bundleJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await contestBundleAPI.exportScoreboard(token, bundleJid);
    toastActions.showSuccessToast('Scoreboard downloading.');
  };
}
