import { push } from 'connected-react-router';

import { BadRequestError } from '../../../../modules/api/error';
import { contestBundleAPI, ContestBundleErrors } from '../../../../modules/api/uriel/contestBundle';
import { selectToken } from '../../../../modules/session/sessionSelectors';

import * as toastActions from '../../../../modules/toast/toastActions';
import { contestBundleManagerAPI } from '../../../../modules/api/uriel/contestBundleManager';

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
        dispatch(push(`/contest-bundles/${data.slug}`));
        // dispatch(EditContest(true));
    }
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
    
    // if (data.slug && data.slug !== bundleSlug) {
    //     dispatch(push(`/contest-bundles/${data.slug}`));
    // }
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
        // dispatch(PutContest(contest));
        return contestBundle;
    }
}

export function getContestBundleByJid(contestBundleJid) {
    return async (dispatch, getState) => {
        const token = selectToken(getState());
        const contestBundle = await contestBundleAPI.getContestBundleByJid(token, contestBundleJid);
        // dispatch(PutContest(contest));
        return contestBundle;
    }
}

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

export function exportScoreboard(bundleJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await contestBundleAPI.exportScoreboard(token, bundleJid);
    toastActions.showSuccessToast('Scoreboard downloading.');
  };
}