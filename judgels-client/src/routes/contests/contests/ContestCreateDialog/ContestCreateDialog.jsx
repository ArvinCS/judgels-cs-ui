import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import { Component } from 'react';

import { SupervisorManagementPermission } from '../../../../modules/api/uriel/contestSupervisor';
import ContestCreateForm from '../ContestCreateForm/ContestCreateForm';

export class ContestCreateDialog extends Component {
  state = { isDialogOpen: false, hasBundle: false, isInsertDefaultSupervisor: false, isInsertDefaultContestant: false };

  constructor(props) {
    super(props);
    this.state.hasBundle = props.bundle !== undefined;
  }

  render() {
    return (
      <div className="content-card__section">
        {this.renderButton()}
        {this.renderDialog()}
      </div>
    );
  }

  renderButton = () => {
    return this.props.compact ? (
      <Button minimal icon={<Plus />} onClick={this.toggleDialog} disabled={this.state.isDialogOpen} />
    ) : (
      <Button intent={Intent.PRIMARY} icon={<Plus />} onClick={this.toggleDialog} disabled={this.state.isDialogOpen}>
        New contest
      </Button>
    );
  };

  toggleDialog = e => {
    e.stopPropagation();
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen }));
  };

  renderDialog = () => {
    const props = {
      hasBundle: this.props.bundle !== undefined,
      renderFormComponents: this.renderDialogForm,
      onSubmit: this.createContest,
    };
    return (
      <Dialog
        isOpen={this.state.isDialogOpen}
        onClose={this.toggleDialog}
        title="Create new contest"
        canOutsideClickClose={false}
      >
        <ContestCreateForm {...props} />
      </Dialog>
    );
  };

  renderDialogForm = (fields, submitButton) => (
    <>
      <div className={Classes.DIALOG_BODY}>{fields}</div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={this.toggleDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  getPermissionList(managementPermissions) {
    return !managementPermissions
      ? []
      : Object.keys(managementPermissions)
          .filter(p => managementPermissions[p])
          .map(p => SupervisorManagementPermission[p]);
  }

  createContest = async data => {
    if (this.state.hasBundle) {
      const { supervisorPermissions, ...restData } = data;
      data = {
        ...restData,
        supervisorPermissions: this.getPermissionList(supervisorPermissions),
        bundleJid: this.props.bundle.jid,
      };
    }
    await this.props.onCreateContest(data);
    this.setState({ isDialogOpen: false });
  };
}
