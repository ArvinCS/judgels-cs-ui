import { selectToken } from '../../../../../../../../../../modules/session/sessionSelectors';
import { NotFoundError } from '../../../../../../../../../../modules/api/error';
import { ProblemSubmissionFormData } from '../../../../../../../../../../components/ProblemWorksheetCard/ProblemSubmissionForm';

export const contestSubmissionActions = {
  fetchMyList: (contestJid: string, page: number) => {
    return async (dispatch, getState, { contestSubmissionAPI }) => {
      const token = selectToken(getState());
      return await contestSubmissionAPI.getMySubmissions(token, contestJid, page);
    };
  },

  fetchWithSource: (contestJid: string, submissionId: number) => {
    return async (dispatch, getState, { contestSubmissionAPI }) => {
      const token = selectToken(getState());
      const submissionWithSource = await contestSubmissionAPI.getSubmissionWithSource(token, submissionId);
      if (contestJid !== submissionWithSource.data.submission.containerJid) {
        throw new NotFoundError();
      }
      return submissionWithSource;
    };
  },

  submit: (contestJid: string, problemJid: string, data: ProblemSubmissionFormData) => {
    console.log(data);
    return async (dispatch, getState, { contestSubmissionAPI }) => {
      const token = selectToken(getState());
      return await contestSubmissionAPI.createSubmission(
        token,
        contestJid,
        problemJid,
        data.gradingLanguage,
        data.sourceFiles
      );
    };
  },
};
