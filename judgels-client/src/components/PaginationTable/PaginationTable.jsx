import { Classes } from '@blueprintjs/core';
import classNames from 'classnames';
import { push, replace } from 'connected-react-router';
import { parse, stringify } from 'query-string';
import { Component } from 'react';
import ReactPaginate from 'react-paginate';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import './PaginationTable.scss';

function PaginationTable({ currentPage, pageSize, totalCount, onChangePage }) {
  const getTotalPages = () => {
    return Math.ceil(totalCount / pageSize);
  };

  const getRange = () => {
    return {
      start: (currentPage - 1) * pageSize + 1,
      end: currentPage * pageSize,
    };
  };

  const changePage = nextPage => {
    onChangePage(nextPage.selected + 1);
  };

  const renderText = () => {
    const { start, end } = getRange();

    if (totalCount === 0) {
      return null;
    }

    return (
      <small>
        <p className="pagination__helper-text" data-key="pagination-helper-text">
          Showing {start}..{Math.min(end, totalCount)} of {totalCount} results
        </p>
      </small>
    );
  };

  const renderNavigation = () => {
    return (
      <ReactPaginate
        forcePage={currentPage - 1}
        pageCount={getTotalPages()}
        pageRangeDisplayed={3}
        marginPagesDisplayed={2}
        pageClassName={classNames(Classes.BUTTON, 'pagination__item')}
        previousLabel="<"
        nextLabel=">"
        pageLinkClassName="pagination__link"
        nextLinkClassName="pagination__link"
        previousLinkClassName="pagination__link"
        breakClassName={classNames(Classes.BUTTON, Classes.DISABLED)}
        containerClassName={Classes.BUTTON_GROUP}
        activeClassName={classNames(Classes.BUTTON, Classes.ACTIVE, 'pagination__item')}
        previousClassName={classNames(Classes.BUTTON, 'pagination__item')}
        nextClassName={classNames(Classes.BUTTON, 'pagination__item')}
        onPageChange={changePage}
        disableInitialCallback
      />
    );
  };

  return (
    <div className={totalCount > 0 ? 'pagination' : 'pagination--hide'}>
      {renderText()}
      {renderNavigation()}
    </div>
  );
}

class PaginationContainer extends Component {
  state = {
    currentPage: 1,
    previousPage: 0,
    totalCount: 0,
  };

  componentDidMount() {
    this.refreshPagination();
  }

  componentDidUpdate() {
    if (this.props.forceUpdate || this.state.currentPage !== this.state.previousPage) {
      this.refreshPagination();
    }
  }

  render() {
    const { currentPage, totalCount } = this.state;
    const { pageSize } = this.props;

    if (totalCount == -0) {
      return null;
    }

    const props = {
      currentPage,
      pageSize,
      totalCount,
      onChangePage: this.onChangePage,
    };
    return <PaginationTable {...props} />;
  }

  onChangePage = async nextPage => {
    this.setState({ previousPage: this.state.currentPage, currentPage: nextPage });
  };

  refreshPagination = async () => {
    const { onChangePage } = this.props;
    const totalCount = await onChangePage(this.state.currentPage);
    this.setState({ totalCount, previousPage: this.state.currentPage });
  };
}

const mapDispatchToProps = {
  onPush: push,
  onReplace: replace,
};
export default withRouter(connect(undefined, mapDispatchToProps)(PaginationContainer));
