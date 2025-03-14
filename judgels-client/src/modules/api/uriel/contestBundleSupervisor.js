import { stringify } from 'query-string';

import { get, post, put } from '../http';
import { baseContestBundleURL } from './contestBundle';

const baseURL = bundleJid => `${baseContestBundleURL(bundleJid)}/supervisors`;

export const contestBundleSupervisorAPI = {
    getSupervisors: (token, bundleJid, page) => {
      const params = stringify({ page });
      return get(`${baseURL(bundleJid)}?${params}`, token);
    },
  
    upsertSupervisors: (token, bundleJid, usernames) => {
      return post(`${baseURL(bundleJid)}/batch-upsert`, token, usernames);
    },
  
    deleteSupervisors: (token, bundleJid, usernames) => {
      return post(`${baseURL(bundleJid)}/batch-delete`, token, usernames);
    },
};
