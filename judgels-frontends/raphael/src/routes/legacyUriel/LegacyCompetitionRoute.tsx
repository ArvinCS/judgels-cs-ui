import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router';
import { push } from 'connected-react-router';

import { Contest } from '../../modules/api/uriel/contest';
import * as contestActions from '../contests/contests/modules/contestActions';

interface LegacyCompetitionRouteProps extends RouteComponentProps<{ contestSlug: string }> {
  onGetContest: (contestSlug: string) => Promise<Contest>;
  onRedirect: (path: string) => any;
}

class LegacyCompetitionRoute extends React.Component<LegacyCompetitionRouteProps> {
  async componentDidMount() {
    const contest = await this.props.onGetContest(this.props.match.params.contestSlug);
    this.props.onRedirect(`/contests/${contest.slug}`);
  }

  render() {
    return null;
  }
}

const mapDispatchToProps = {
  onGetContest: contestActions.getContestBySlug,
  onRedirect: push,
};
export default connect(undefined, mapDispatchToProps)(LegacyCompetitionRoute);
