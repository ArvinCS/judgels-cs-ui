import { selectToken } from '../../../../../../modules/session/sessionSelectors';
import { contestFileAPI } from '../../../../../../modules/api/uriel/contestFile';
import * as toastActions from '../../../../../../modules/toast/toastActions';

export function getFiles(contestJid: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestFileAPI.getFiles(token, contestJid);
  };
}

export function uploadFile(contestJid: string, file: File) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await contestFileAPI.uploadFile(token, contestJid, file);

    toastActions.showSuccessToast('File uploaded.');
  };
}
