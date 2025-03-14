import { parse } from 'query-string';
import { Component } from 'react';
import { connect } from 'react-redux';

import { Card } from '../../../../components/Card/Card';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import Pagination from '../../../../components/Pagination/Pagination';
import SearchBox from '../../../../components/SearchBox/SearchBox';
import BundleCard from '../BundleCard/BundleCard';
import { BundleCreateDialog } from '../BundleCreateDialog/BundleCreateDialog';

import * as contestActions from '../../contests/modules/contestActions';
import * as bundleActions from '../modules/bundleActions';

class BundlesPage extends Component {
  static PAGE_SIZE = 6;

  state;

  constructor(props) {
    super(props);

    const queries = parse(props.location.search);
    const name = queries.name;

    this.state = {
      response: undefined,
      filter: { name },
      isFilterLoading: false,
    };
  }

  render() {
    return (
      <Card title="Bundles">
        {this.renderHeader()}
        {this.renderBundles()}
        {this.renderPagination()}
      </Card>
    );
  }

  renderHeader = () => {
    return (
      <>
        <div className="float-left">{this.renderCreateDialog()}</div>
        <div className="float-right">{this.renderFilter()}</div>
        <div className="clearfix" />
        {this.renderFilterResultsBanner()}
      </>
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

  renderCreateDialog = () => {
    const { response } = this.state;
    if (!response) {
      return null;
    }
    const config = response.config;
    if (!config.canAdminister) {
      return null;
    }
    return <BundleCreateDialog onCreateBundle={this.props.onCreateBundle} />;
  };

  renderBundles = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingContentCard />;
    }

    const rolesMap = response.rolesMap;
    const bundles = response.data;
    if (!bundles) {
      return <LoadingContentCard />;
    }

    if (bundles.page.length === 0) {
      return (
        <p>
          <small>No bundles.</small>
        </p>
      );
    }

    return (
      <>
        {bundles.page.map(bundle => (
          <BundleCard key={bundle.jid} bundle={bundle} role={rolesMap[bundle.jid]} />
        ))}
      </>
    );
  };

  renderPagination = () => {
    return (
      <Pagination
        pageSize={BundlesPage.PAGE_SIZE}
        onChangePage={this.onChangePage}
        key={this.getNameFilter(this.state) || ''}
      />
    );
  };

  onChangePage = async nextPage => {
    if (this.state.response) {
      this.setState({ response: { ...this.state.response, data: undefined } });
    }
    const response = await this.props.onGetBundles(this.getNameFilter(this.state), nextPage);
    this.setState({ response, isFilterLoading: false });
    return response.data.totalCount;
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
  onGetBundles: bundleActions.getContestBundles,
  onCreateBundle: bundleActions.createContestBundle,
  onGetBundleByJid: bundleActions.getContestBundleByJid,
  onGetContestsInBundle: contestActions.getContestsInBundle,
  onCreateContest: contestActions.createContest,
};

export default connect(undefined, mapDispatchToProps)(BundlesPage);
