import { Button, Classes, Dialog, Intent, Tab, Tabs } from '@blueprintjs/core';
import { ChevronRight, Edit } from '@blueprintjs/icons';
import { Component } from 'react';

import './BundleEditDialog.scss';
import BundleEditGeneralTab from '../components/BundleEditGeneralTab';
import BundleEditManagersTab from '../components/BundleEditManagersTab';
import BundleEditSupervisorTab from '../components/BundleEditSupervisorTab';
import BundleEditContestantTab from '../components/BundleEditContestantTab';

export class BundleEditDialog extends Component {
  state = {
    isDialogOpen: false,
  };

  async componentDidMount() {
    if (this.props.isEditingContest) {
      this.setState({ isDialogOpen: true });
    }
  }

  render() {
    return (
      <>
        {this.renderButton()}
        {this.renderDialog()}
      </>
    );
  }

  renderButton = () => {
    if (!this.props.canManage) {
      return null;
    }
    return (
        <Edit 
            className='bundle-card-edit-icon' 
            onClick={this.toggleDialog}
            disabled={this.state.isDialogOpen}
        />
    );
  };

  toggleDialog = (e) => {
    e.stopPropagation();
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen }));
  };
    
  renderDialog = () => {
    return (
      <Dialog
        className="contest-edit-dialog"
        isOpen={this.state.isDialogOpen}
        onClose={this.toggleDialog}
        title="Bundle settings"
        canOutsideClickClose={false}
        enforceFocus={false}
      >
        <div className={Classes.DIALOG_BODY}>
          <Tabs id="contest-edit-dialog-tabs" vertical renderActiveTabPanelOnly animate={false}>
            <Tab id="general" panel={<BundleEditGeneralTab {...this.props} />}>
              <span>General</span>
              <ChevronRight className="contest-edit-dialog__arrow" />
            </Tab>
            <Tab id="managers" panel={<BundleEditManagersTab {...this.props} />}>
              <span>Managers</span>
              <ChevronRight className="contest-edit-dialog__arrow" />
            </Tab>
            <Tab id="supervisors" panel={<BundleEditSupervisorTab {...this.props} />}>
              <span>Supervisors</span>
              <ChevronRight className="contest-edit-dialog__arrow" />
            </Tab>
            <Tab id="contestants" panel={<BundleEditContestantTab {...this.props} />}>
              <span>Contestants</span>
              <ChevronRight className="contest-edit-dialog__arrow" />
            </Tab>
            {/* <Tab id="description" panel={<ContestEditDescriptionTab />}>
              <span>Description</span>
              <ChevronRight className="contest-edit-dialog__arrow" />
            </Tab>
            <Tab id="modules" panel={<ContestEditModulesTab />}>
              <span>Modules</span>
              <ChevronRight className="contest-edit-dialog__arrow" />
            </Tab>
            <Tab id="configs" panel={<ContestEditConfigsTab />}>
              <span>Configs</span>
              <ChevronRight className="contest-edit-dialog__arrow" />
            </Tab> */}
          </Tabs>
        </div>
        <div className={Classes.DIALOG_FOOTER}>
          <hr />
          <div className={Classes.DIALOG_FOOTER_ACTIONS}>
            <Button text="Close" onClick={this.toggleDialog} />
          </div>
        </div>
      </Dialog>
    );
  };
}
