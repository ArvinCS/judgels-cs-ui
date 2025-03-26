import { parse } from 'query-string';
import { Component } from 'react';
import { connect } from 'react-redux';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import Pagination from '../../../../components/Pagination/Pagination';
import SearchBox from '../../../../components/SearchBox/SearchBox';
import { GroupUserEditDialog } from '../GroupUserEditDialog/GroupUserEditDialog';
import GroupUserUpdateDialog from '../GroupUserUpdateDialog/GroupUserUpdateDialog';
import { GroupUsersTable } from '../GroupUsersTable/GroupUsersTable';

import * as groupActions from '../modules/groupActions';

class GroupsPage extends Component {
  static PAGE_SIZE = 30;

  state;

  constructor(props) {
    super(props);

    const queries = parse(this.props.location.search);
    const page = queries.page;
    const name = queries.name;

    this.state = {
      page,
      response: undefined,
      isEditDialogOpen: false,
      isFilterLoading: false,
      editedUserJid: undefined,
      filter: { name },
    };
  }

  componentDidUpdate() {
    const queries = parse(this.props.location.search);
    const page = queries.page;

    if (page !== this.state.page) {
      this.setState({ page });
    }
  }

  render() {
    return (
      <ContentCard>
        <h3>Users Groups</h3>
        <hr />
        <div className="float-left">{this.renderUpdateGroupBatchDialog()}</div>
        <div className="float-right">{this.renderFilter()}</div>
        <div className="clearfix" />
        {this.renderFilterResultsBanner()}
        {this.renderEditDialog()}
        {this.renderUsers()}
        {this.renderPagination()}
      </ContentCard>
    );
  }

  renderUpdateGroupBatchDialog = () => {
    return <GroupUserUpdateDialog onUpdateBatchGroup={this.editGroup} />;
  };

  renderFilter = () => {
    const name = this.getNameFilter(this.state);
    return (
      <SearchBox
        onRouteChange={this.searchBoxUpdateQueries}
        initialValue={name || ''}
        isLoading={this.state.isFilterLoading}
      />
    );
  };

  renderFilterResultsBanner = () => {
    const name = this.getNameFilter(this.state);
    if (!name) {
      return null;
    }

    return (
      <div className="content-card__section">
        Search results for: <b>{name}</b>
        <hr />
      </div>
    );
  };

  renderEditDialog = () => {
    const { isEditDialogOpen, editedUserJid, response } = this.state;
    const groups = response ? response.groupsMap[editedUserJid] : [];

    return (
      <GroupUserEditDialog
        isOpen={isEditDialogOpen}
        userJid={editedUserJid}
        groups={groups}
        onUpdateGroup={this.updateGroup}
        onCloseDialog={() => this.editGroup(undefined)}
      />
    );
  };

  renderUsers = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data, groupsMap } = response;
    if (!data) {
      return <LoadingContentCard />;
    }
    if (groupsMap.length === 0) {
      return (
        <p>
          <small>No users.</small>
        </p>
      );
    }

    return <GroupUsersTable users={data.page} groupsMap={groupsMap} onEditGroup={this.editGroup} />;
  };

  renderPagination = () => {
    return (
      <Pagination
        pageSize={GroupsPage.PAGE_SIZE}
        onChangePage={this.onChangePage}
        key={this.getNameFilter(this.state) || ''}
      />
    );
  };

  onChangePage = async nextPage => {
    if (this.state.response) {
      this.setState({ response: { ...this.state.response, data: undefined } });
    }
    const response = await this.props.onGetGroupOfUsers(nextPage, this.getNameFilter(this.state));
    this.setState({ response, isFilterLoading: false });
    return response.data.totalCount;
  };

  editGroup = () => {
    this.onChangePage(this.state.page);
  };

  updateGroup = async (userJid, data) => {
    await this.props.onUpdateGroup(userJid, data);
    this.editGroup(undefined);
    await this.refreshUsers();
  };

  searchBoxUpdateQueries = (name, queries) => {
    this.setState(prevState => {
      const isFilterLoading = this.getNameFilter(prevState) !== name;
      return {
        filter: {
          name,
        },
        isFilterLoading,
        isContestsListLoading: isFilterLoading,
      };
    });
    return { ...queries, page: undefined, name };
  };

  getNameFilter = state => {
    if (!state.filter.name) {
      return '';
    }
    return state.filter.name;
  };
}

const mapDispatchToProps = {
  onGetGroupOfUsers: groupActions.getGroupOfUsers,
  onGetGroup: groupActions.getGroup,
  onUpdateGroup: groupActions.updateGroup,
};
export default connect(undefined, mapDispatchToProps)(GroupsPage);
