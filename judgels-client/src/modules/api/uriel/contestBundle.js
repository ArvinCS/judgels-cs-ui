import { stringify } from 'query-string';

import { APP_CONFIG } from '../../../conf';
import { get, post, put } from '../http';

export const ContestBundleErrors = {
  SlugAlreadyExists: 'Uriel:BundleSlugAlreadyExists',
};

export const baseContestBundlesURL = `${APP_CONFIG.apiUrl}/contest-bundles`;

export function baseContestBundleURL(bundleJid) {
  return `${baseContestBundlesURL}/${bundleJid}`;
}

export const contestBundleAPI = {
  createContestBundle: (token, data) => {
    return post(baseContestBundlesURL, token, data);
  },

  updateContestBundle: (token, bundleJid, data) => {
    return post(`${baseContestBundleURL(bundleJid)}`, token, data);
  },

  getContestBundles: (token, name, page) => {
    const params = stringify({ name, page });
    return get(`${baseContestBundlesURL}?${params}`, token);
  },

  getContestBundleBySlug: (token, bundleSlug) => {
    return get(`${baseContestBundlesURL}/slug/${bundleSlug}`, token);
  },

  getContestBundleByJid: (token, bundleJid) => {
    return get(`${baseContestBundlesURL}/${bundleJid}`, token);
  },
};
