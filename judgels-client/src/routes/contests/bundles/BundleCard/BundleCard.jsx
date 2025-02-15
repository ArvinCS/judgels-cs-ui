import { PureComponent } from 'react';

import './BundleCard.scss';
import { ContestCreateDialog } from '../../contests/ContestCreateDialog/ContestCreateDialog';
import { BundleEditDialog } from '../BundleEditDialog/BundleEditDialog';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { Collapse } from '@blueprintjs/core';
import { ContestRoleTag } from '../../../../components/ContestRole/ContestRoleTag';
import { ChevronDown, ChevronRight, Edit } from '@blueprintjs/icons';
import { ContestCard } from '../../contests/ContestCard/ContestCard';

export class BundleCard extends PureComponent {
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
            error: null
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
                                <BundleEditDialog canManage={this.state.role === 'ADMIN'} bundle={this.state.bundle} onEditDone={this.fetchBundleData} />
                                {(this.state.role === 'ADMIN' || this.state.role == 'MANAGER') && (
                                    <ContestCreateDialog onCreateContest={this.props.onCreateContest} bundle={this.state.bundle} />
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
                    {this.state.loading && <div>Loading...</div>}
                    {this.state.error && <div>Error loading bundle</div>}
                    <div className='bundle-card-contest-container'>
                        {this.state.contests && this.state.contests.page.length > 0 ? (
                            this.state.contests.page.map(contest => (
                                <ContestCard key={contest.jid} contest={contest} role={this.state.rolesMap[contest.jid]} />
                            ))
                        ) : (
                            !this.state.loading && <div className='bundle-card-contest-container'>No contests available</div>
                        )}
                    </div>
                </Collapse>
            </>
        );
    }

    toggleCollapse = () => {
        this.setState(prevState => {
            const isOpen = !prevState.isOpen;
            if (isOpen && !prevState.contests) {
                this.fetchBundleData();
            }
            return { isOpen };
        });
    };

    fetchBundleData = () => {
        this.setState({ loading: true, error: null });
        this.props.onGetBundleByJid(this.state.bundle.jid)
            .then(response => {
                const bundle = response;

                this.setState({ bundle: bundle });
            }).catch(error => this.setState({ error, loading: false }));
        this.props.onGetContestsInBundle(this.state.bundle.slug)
            .then(response => {
                const contests = response.data;
                const rolesMap = response.rolesMap;

                this.setState({ contests: contests, rolesMap: rolesMap, loading: false });
            }).catch(error => this.setState({ error, loading: false }));
    };
}
