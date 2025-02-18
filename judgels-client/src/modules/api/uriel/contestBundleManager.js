import { stringify } from 'query-string';

import { get, post, put } from '../http';
import { baseContestBundleURL } from './contestBundle';

const baseURL = bundleJid => `${baseContestBundleURL(bundleJid)}/managers`;

export const contestBundleManagerAPI = {
    getManagers: (token, bundleJid, page) => {
      const params = stringify({ page });
      return get(`${baseURL(bundleJid)}?${params}`, token);
    },
  
    upsertManagers: (token, bundleJid, usernames) => {
      return post(`${baseURL(bundleJid)}/batch-upsert`, token, usernames);
    },
  
    deleteManagers: (token, bundleJid, usernames) => {
      return post(`${baseURL(bundleJid)}/batch-delete`, token, usernames);
    },
};
