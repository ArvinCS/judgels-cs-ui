import { stringify } from 'query-string';

import { APP_CONFIG } from '../../../conf';
import { get, post, put } from '../http';

const baseURL = `${APP_CONFIG.apiUrl}/users`;

export const groupAPI = {
  getGroupOfUsers: (token, page, name, orderBy, orderDir) => {
    const params = stringify({ page, name, orderBy, orderDir });
    return get(`${baseURL}/group?${params}`, token);
  },

  getGroup: (token, userJid) => {
    return get(`${baseURL}/${userJid}/group`, token);
  },

  updateGroup: (token, userJid, data) => {
    return put(`${baseURL}/${userJid}/group`, token, data);
  },

  upsertUsersWithGroups: (token, usernames, groups) => {
    const data = { usernames, groups };
    return post(`${baseURL}/group/batch-upsert`, token, data);
  },

  deleteUsersWithGroups: (token, usernames, groups) => {
    const data = { usernames, groups };
    return post(`${baseURL}/group/batch-delete`, token, data);
  },
};
