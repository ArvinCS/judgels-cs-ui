import { Button, Dialog, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import { Component } from 'react';
import { connect } from 'react-redux';

import GroupUserUpdateForm from '../GroupUserUpdateForm/GroupUserUpdateForm';

import * as groupActions from '../modules/groupActions';

import './GroupUserUpdateDialog.scss';

class GroupUserUpdateDialog extends Component {
  state = {
    isDialogOpen: false,
  };

  render() {
    return (
      <div className="content-card__section">
        {this.renderButton()}
        {this.renderDialog()}
      </div>
    );
  }

  renderButton = () => {
    return (
      <Button intent={Intent.PRIMARY} icon={<Plus />} onClick={this.toggleDialog} disabled={this.state.isDialogOpen}>
        Update batch
      </Button>
    );
  };

  toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen }));
  };

  renderDialog = () => {
    const props = {
      renderFormComponents: this.renderDialogForm,
      onSubmit: this.editGroups,
    };
    return (
      <Dialog
        isOpen={this.state.isDialogOpen}
        onClose={this.toggleDialog}
        title="Update batch"
        canOutsideClickClose={false}
      >
        <GroupUserUpdateForm className="group-user-update-form" {...props} />
      </Dialog>
    );
  };

  renderDialogForm = (fields, addButton, removeButton) => (
    <div className="group-user-update-form">
      <div>{fields}</div>
      <div>
        <div className="group-user-update-dialog-footer">
          <Button text="Cancel" onClick={this.toggleDialog} />
          <div className="group-user-update-dialog-footer-actions">
            {removeButton}
            {addButton}
          </div>
        </div>
      </div>
    </div>
  );

  editGroups = async (action, data) => {
    const usernames = data.usernames
      .split('\n')
      .map(s => s.trim())
      .filter(s => s.length > 0);
    const groups = data.groups
      .split('\n')
      .map(s => s.trim())
      .filter(s => s.length > 0);
    if (action === 'add') {
      await this.props.onUpsertUsersWithGroups(usernames, groups);
    } else if (action === 'remove') {
      await this.props.onDeleteUsersWithGroups(usernames, groups);
    }
    if (this.state.isDialogOpen) {
      this.toggleDialog();
    }
    this.props.onUpdateBatchGroup();
  };
}

const mapDispatchToProps = {
  onUpsertUsersWithGroups: groupActions.upsertUsersWithGroups,
  onDeleteUsersWithGroups: groupActions.deleteUsersWithGroups,
};
export default connect(undefined, mapDispatchToProps)(GroupUserUpdateDialog);
