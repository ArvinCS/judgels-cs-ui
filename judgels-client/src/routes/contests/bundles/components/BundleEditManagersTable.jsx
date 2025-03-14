import { Button, HTMLTable, Intent, Spinner } from '@blueprintjs/core';
import { Trash } from '@blueprintjs/icons';
import { Component } from 'react';
import { connect } from 'react-redux';

import PaginationTable from '../../../../components/PaginationTable/PaginationTable';
import { UserRef } from '../../../../components/UserRef/UserRef';

import * as bundleManagerAction from '../modules/bundleManagerActions';

class BundleEditManagersTable extends Component {
  static PAGE_SIZE = 10;

  state;

  constructor(props) {
    super(props);

    this.state = {
      response: undefined,
      managers: undefined,
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
          {this.props.canDelete && <th className="col-actions">Actions</th>}
        </tr>
      </thead>
    );
  };

  renderRows = () => {
    const { managers, profilesMap } = this.state;

    const sortedManagers = managers.page.slice().sort((c1, c2) => {
      const username1 = (profilesMap[c1.userJid] && profilesMap[c1.userJid].username) || 'ZZ';
      const username2 = (profilesMap[c2.userJid] && profilesMap[c2.userJid].username) || 'ZZ';
      return username1.localeCompare(username2);
    });

    const rows = sortedManagers.map(manager => (
      <tr key={manager.userJid}>
        <td>
          <UserRef profile={profilesMap[manager.userJid]} />
        </td>
        {this.props.canDelete && (
          <td>
            <Button
              small
              intent={Intent.DANGER}
              icon={<Trash />}
              onClick={async () => await this.deleteManager(profilesMap[manager.userJid].username)}
            />
          </td>
        )}
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };

  renderPagination = () => {
    return (
      <PaginationTable
        pageSize={BundleEditManagersTable.PAGE_SIZE}
        forceUpdate={this.state.forceUpdate}
        onChangePage={this.onChangePage}
      />
    );
  };

  onChangePage = async nextPage => {
    if (this.state.response) {
      this.setState({ response: { ...this.state.response, data: undefined } });
    }

    const response = await this.props.onGetBundleManagersByJid(this.props.bundleJid, nextPage);

    const managers = response.data;
    const profilesMap = response.profilesMap;

    this.setState({ response, managers, profilesMap, loading: false, forceUpdate: false });
    return response.data.totalCount;
  };

  deleteManager = async username => {
    this.setState({ loading: true });
    await this.props.onEditManagers('remove', { usernames: username });
    this.setState({ loading: false, forceUpdate: true });
  };
}

const mapDispatchToProps = {
  onGetBundleManagersByJid: bundleManagerAction.getContestBundleManagersByJid,
};
export default connect(undefined, mapDispatchToProps)(BundleEditManagersTable);
