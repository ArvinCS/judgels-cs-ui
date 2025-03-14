import { PureComponent } from 'react';
import { connect } from 'react-redux';

import './BundleCard.scss';
import { ContestCreateDialog } from '../../contests/ContestCreateDialog/ContestCreateDialog';
import { BundleEditDialog } from '../BundleEditDialog/BundleEditDialog';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { Collapse, Spinner } from '@blueprintjs/core';
import { ContestRoleTag } from '../../../../components/ContestRole/ContestRoleTag';
import { ChevronDown, ChevronRight, Edit } from '@blueprintjs/icons';
import { ContestCard } from '../../contests/ContestCard/ContestCard';
import PaginationTable from '../../../../components/PaginationTable/PaginationTable';

import * as contestActions from '../../contests/modules/contestActions';
import * as bundleActions from '../modules/bundleActions';

class BundleCard extends PureComponent {
    static PAGE_SIZE = 6;

    state;

    constructor(props) {
        super(props);
    
        this.state = {
            isOpen: false,
            bundle: props.bundle,
            role: props.role,
            contests: null,
            rolesMap: null,
            managers: null,
            profilesMap: null,
            loading: false,
            error: null,
            page: 1
        };
    }

    render() {
        return (
            <>
                <div onClick={this.toggleCollapse}>
                    <ContentCard className="bundle-card">
                        <h4 className="bundle-card-name">
                            {this.state.bundle.name}
                            <div className="bundle-card-status">
                                <BundleEditDialog canManage={this.state.role === 'ADMIN' || this.state.role === 'MANAGER'} role={this.state.role} bundle={this.state.bundle} onEditDone={this.fetchBundleData} />
                                {(this.state.role === 'ADMIN' || this.state.role == 'MANAGER') && (
                                    <div className='bundle-card-add-icon'>
                                        <ContestCreateDialog compact onCreateContest={this.props.onCreateContest} bundle={this.state.bundle} />
                                    </div>
                                )}
                                <ContestRoleTag role={this.state.role} />
                                {this.state.isOpen ? (
                                    <ChevronDown className='bundle-card-chevron'/>
                                ) : (
                                    <ChevronRight className='bundle-card-chevron'/>
                                )}
                            </div>
                        </h4>
                        {/* <p className="bundle-card-description">{bundle.description}</p> */}
                    </ContentCard>
                </div>
                <Collapse isOpen={this.state.isOpen}>
                    {this.state.loading && <Spinner/>}
                    {!this.state.loading && <>
                        {this.state.error && <div>Error loading bundle</div>}
                        {this.renderPagination()}
                        <div className='bundle-card-contest-container'>
                            {this.state.contests && this.state.contests.page.length > 0 ? (
                                this.state.contests.page.map(contest => (
                                    <ContestCard key={contest.jid} contest={contest} role={this.state.rolesMap[contest.jid]} />
                                ))
                            ) : (
                                !this.state.loading && <div className='bundle-card-contest-container'>No contests available</div>
                            )}
                        </div>
                    </>}
                </Collapse>
            </>
        );
    }

    renderPagination = () => {
        return (<>
            <div className='bundle-card-pagination-container'>
                <PaginationTable
                    pageSize={BundleCard.PAGE_SIZE}
                    onChangePage={this.onChangePage}
                />
            </div>
        </>);
    }

    onChangePage = async nextPage => {
        if (this.state.response) {
            this.setState({ response: { ...this.state.response, data: undefined } });
        }

        const response = await this.props.onGetContestsInBundle(this.state.bundle.slug, nextPage);

        const contests = response.data;
        const rolesMap = response.rolesMap;

        this.setState({ response, contests, rolesMap, loading: false });
        return response.data.totalCount;
    };

    toggleCollapse = () => {
        this.setState(prevState => {
            const isOpen = !prevState.isOpen;
            return { isOpen };
        });
    };

    fetchBundleData = () => {
        this.setState({ loading: true, error: null });
        this.props.onGetBundleByJid(this.state.bundle.jid)
            .then(response => {
                const bundle = response;

                this.setState({ bundle: bundle, loading: false });
            }).catch(error => this.setState({ error, loading: false }));
    };
}

const mapDispatchToProps = {
    onGetBundleByJid: bundleActions.getContestBundleByJid,
    onGetContestsInBundle: contestActions.getContestsInBundle,
    onCreateContest: contestActions.createContest,
};

export default connect(undefined, mapDispatchToProps)(BundleCard);