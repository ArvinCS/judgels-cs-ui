import { Button, HTMLTable, Intent, Spinner } from '@blueprintjs/core';
import { Trash } from '@blueprintjs/icons';
import { Component } from 'react';
import { connect } from 'react-redux';

import PaginationTable from '../../../../components/PaginationTable/PaginationTable';
import { UserRef } from '../../../../components/UserRef/UserRef';

import * as bundleContestantAction from '../modules/bundleContestantActions';

class BundleEditContestantTable extends Component {
  static PAGE_SIZE = 10;

  state;

  constructor(props) {
    super(props);

    this.state = {
      response: undefined,
      supervisors: undefined,
      loading: true,
      profilesMap: undefined,
    };
  }

  render() {
    return (
      <>
        {this.renderTable()}
        {this.renderPagination()}
      </>
    );
  }

  renderTable = () => {
    if (this.state.loading) {
      return <Spinner />;
    }

    return (
      <HTMLTable striped className="table-list-condensed">
        {this.renderHeader()}
        {this.renderRows()}
      </HTMLTable>
    );
  };

  renderHeader = () => {
    return (
      <thead>
        <tr>
          <th className="col-user">User</th>
          <th className="col-actions">Actions</th>
        </tr>
      </thead>
    );
  };

  renderRows = () => {
    const { contestants, profilesMap } = this.state;

    const sortedManagers = contestants.page.slice().sort((c1, c2) => {
      const username1 = (profilesMap[c1.userJid] && profilesMap[c1.userJid].username) || 'ZZ';
      const username2 = (profilesMap[c2.userJid] && profilesMap[c2.userJid].username) || 'ZZ';
      return username1.localeCompare(username2);
    });

    const rows = sortedManagers.map(supervisor => (
      <tr key={supervisor.userJid}>
        <td>
          <UserRef profile={profilesMap[supervisor.userJid]} />
        </td>
        <td>
          <Button
            small
            intent={Intent.DANGER}
            icon={<Trash />}
            onClick={async () => await this.deleteContestant(profilesMap[supervisor.userJid].username)}
          />
        </td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };

  renderPagination = () => {
    return (
      <PaginationTable
        pageSize={BundleEditContestantTable.PAGE_SIZE}
        forceUpdate={this.state.forceUpdate}
        onChangePage={this.onChangePage}
      />
    );
  };

  onChangePage = async nextPage => {
    if (this.state.response) {
      this.setState({ response: { ...this.state.response, data: undefined } });
    }

    const response = await this.props.onGetBundleContestantsByJid(this.props.bundleJid, nextPage);

    const contestants = response.data;
    const profilesMap = response.profilesMap;

    this.setState({ response, contestants, profilesMap, loading: false, forceUpdate: false });
    return response.data.totalCount;
  };

  deleteContestant = async username => {
    this.setState({ loading: true });
    await this.props.onEditContestants('remove', { usernames: username });
    this.setState({ loading: false, forceUpdate: true });
  };
}

const mapDispatchToProps = {
  onGetBundleContestantsByJid: bundleContestantAction.getContestBundleContestantsByJid,
};
export default connect(undefined, mapDispatchToProps)(BundleEditContestantTable);
