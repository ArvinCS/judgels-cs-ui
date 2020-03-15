import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { IntlProvider } from 'react-intl';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { contest, contestJid, contestSlug, contestStyle } from '../../../../../../fixtures/state';
import { parseDateTime } from '../../../../../../utils/datetime';
import { parseDuration } from '../../../../../../utils/duration';

import ContestEditGeneralTab from './ContestEditGeneralTab';
import { contestReducer, PutContest } from '../../../modules/contestReducer';
import * as contestActions from '../../../modules/contestActions';
import * as contestWebActions from '../../modules/contestWebActions';

jest.mock('../../../modules/contestActions');
jest.mock('../../modules/contestWebActions');

describe('ContestEditGeneralTab', () => {
  let wrapper: ReactWrapper<any, any>;

  beforeEach(() => {
    (contestWebActions.getContestByJidWithWebConfig as jest.Mock).mockReturnValue(() => Promise.resolve({}));
    (contestActions.updateContest as jest.Mock).mockReturnValue(() => Promise.resolve({}));

    const store: any = createStore(
      combineReducers({ uriel: combineReducers({ contest: contestReducer }), form: formReducer }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest.create(contest));

    wrapper = mount(
      <IntlProvider locale={navigator.language}>
        <Provider store={store}>
          <MemoryRouter>
            <ContestEditGeneralTab />
          </MemoryRouter>
        </Provider>
      </IntlProvider>
    );
  });

  test('contest edit general tab form', () => {
    const button = wrapper.find('button');
    button.simulate('click');

    wrapper.update();

    const slug = wrapper.find('input[name="slug"]');
    slug.simulate('change', { target: { value: 'contest-b' } });

    const name = wrapper.find('input[name="name"]');
    name.simulate('change', { target: { value: 'Contest B' } });

    const beginTime = wrapper.find('input[name="beginTime"]');
    beginTime.simulate('change', { target: { value: '2018-09-10 17:00' } });

    const duration = wrapper.find('input[name="duration"]');
    duration.simulate('change', { target: { value: '6h' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(contestActions.updateContest).toHaveBeenCalledWith(contestJid, contestSlug, {
      slug: 'contest-b',
      name: 'Contest B',
      style: contestStyle,
      beginTime: parseDateTime('2018-09-10 17:00').getTime(),
      duration: parseDuration('6h'),
    });
  });
});
