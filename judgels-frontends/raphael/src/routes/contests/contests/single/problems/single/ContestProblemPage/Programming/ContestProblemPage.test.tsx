import { mount, ReactWrapper } from 'enzyme';
import { createMemoryHistory, MemoryHistory } from 'history';
import * as React from 'react';
import { Provider } from 'react-redux';
import { Route } from 'react-router';
import { connectRouter, routerMiddleware, ConnectedRouter } from 'connected-react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { webPrefsReducer } from '../../../../../../../../modules/webPrefs/webPrefsReducer';
import { ContestProblemStatus } from '../../../../../../../../modules/api/uriel/contestProblem';
import { contest, contestJid, problemJid } from '../../../../../../../../fixtures/state';
import ContestProblemPage from './ContestProblemPage';
import { contestReducer, PutContest } from '../../../../../modules/contestReducer';
import * as contestProblemActions from '../../../modules/contestProblemActions';
import * as contestSubmissionActions from '../../../../submissions/Programming/modules/contestSubmissionActions';
import * as webPrefsActions from '../../../../../../../../modules/webPrefs/webPrefsActions';
import * as breadcrumbsActions from '../../../../../../../../modules/breadcrumbs/breadcrumbsActions';

jest.mock('../../../modules/contestProblemActions');
jest.mock('../../../../submissions/Programming/modules/contestSubmissionActions');
jest.mock('../../../../../../../../modules/webPrefs/webPrefsActions');
jest.mock('../../../../../../../../modules/breadcrumbs/breadcrumbsActions');

describe('ProgrammingContestProblemPage', () => {
  let wrapper: ReactWrapper<any, any>;
  let history: MemoryHistory;

  beforeEach(() => {
    (contestProblemActions.getProgrammingProblemWorksheet as jest.Mock).mockReturnValue(() =>
      Promise.resolve({
        problem: {
          problemJid,
          alias: 'C',
          status: ContestProblemStatus.Open,
          submissionsLimit: 0,
        },
        totalSubmissions: 2,
        worksheet: {
          statement: {
            name: 'Problem',
            text: 'Lorem ipsum',
          },
          limits: {
            timeLimit: 2000,
            memoryLimit: 65536,
          },
          submissionConfig: {
            sourceKeys: { encoder: 'Encoder', decoder: 'Decoder' },
            gradingEngine: 'Batch',
            gradingLanguageRestriction: { allowedLanguageNames: ['Cpp11', 'Pascal'] },
          },
        },
      })
    );

    (webPrefsActions.updateGradingLanguage as jest.Mock).mockReturnValue(() => Promise.resolve({}));
    (contestSubmissionActions.createSubmission as jest.Mock).mockReturnValue(() => Promise.resolve({}));
    (breadcrumbsActions.pushBreadcrumb as jest.Mock).mockReturnValue({ type: 'push' });
    (breadcrumbsActions.popBreadcrumb as jest.Mock).mockReturnValue({ type: 'pop' });

    history = createMemoryHistory({ initialEntries: [`/contests/${contestJid}/problems/C`] });

    const store: any = createStore(
      combineReducers({
        form: formReducer,
        webPrefs: webPrefsReducer,
        uriel: combineReducers({ contest: contestReducer }),
        router: connectRouter(history),
      }),
      applyMiddleware(thunk, routerMiddleware(history))
    );
    store.dispatch(PutContest.create(contest));

    wrapper = mount(
      <Provider store={store}>
        <ConnectedRouter history={history}>
          <Route path="/contests/:contestSlug/problems/:problemAlias" component={ContestProblemPage} />
        </ConnectedRouter>
      </Provider>
    );
  });

  test('navigation', async () => {
    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();
    expect(breadcrumbsActions.pushBreadcrumb).toHaveBeenCalledWith(`/contests/${contestJid}/problems/C`, 'Problem C');

    history.push('/contests/ioi/');
    await new Promise(resolve => setImmediate(resolve));
    expect(breadcrumbsActions.popBreadcrumb).toHaveBeenCalledWith(`/contests/${contestJid}/problems/C`);
  });

  test('submission form', async () => {
    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();

    const encoder = wrapper.find('input[name="sourceFiles.encoder"]');
    encoder.simulate('change', { target: { files: [{ name: 'encoder.cpp', size: 1000 }] } });

    const decoder = wrapper.find('input[name="sourceFiles.decoder"]');
    decoder.simulate('change', { target: { files: [{ name: 'decoder.cpp', size: 2000 }] } });

    // TODO(fushar): make this work
    // See https://github.com/FezVrasta/popper.js/issues/478

    // const gradingLanguageButton = wrapper.find('button[data-key="gradingLanguage"]');
    // gradingLanguageButton.simulate('click');

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(webPrefsActions.updateGradingLanguage).toHaveBeenCalledWith('Cpp11');
    expect(contestSubmissionActions.createSubmission).toHaveBeenCalledWith(contestJid, 'contest-a', problemJid, {
      gradingLanguage: 'Cpp11',
      sourceFiles: {
        encoder: { name: 'encoder.cpp', size: 1000 } as File,
        decoder: { name: 'decoder.cpp', size: 2000 } as File,
      },
    });
  });
});
