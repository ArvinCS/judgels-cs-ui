import { stringify } from 'query-string';

import { get, post } from '../http';
import { baseContestBundleURL } from './contestBundle';

const baseURL = bundleJid => `${baseContestBundleURL(bundleJid)}/contestants`;

export const contestBundleContestantAPI = {
    getContestants: (token, bundleJid, page) => {
      const params = stringify({ page });
      return get(`${baseURL(bundleJid)}?${params}`, token);
    },
  
    upsertContestants: (token, bundleJid, usernames) => {
      return post(`${baseURL(bundleJid)}/batch-upsert`, token, usernames);
    },
  
    deleteContestants: (token, bundleJid, usernames) => {
      return post(`${baseURL(bundleJid)}/batch-delete`, token, usernames);
    },
};
