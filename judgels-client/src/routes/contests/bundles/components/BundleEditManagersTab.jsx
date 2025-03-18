import { Button, Intent, Spinner } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Component } from 'react';
import { connect } from 'react-redux';

import BundleEditManagerForm from './BundleEditManagersForm';
import BundleEditManagersTable from './BundleEditManagersTable';

import * as bundleManagerAction from '../modules/bundleManagerActions';

import './BundleEditManagersTab.scss';

class BundleEditManagersTab extends Component {
  state = {
    isEditing: false,
    loading: true,
  };

  render() {
    return (
      <>
        <h4>
          Managers settings
          {this.props.role === 'ADMIN' && this.renderEditButton()}
        </h4>
        <hr />
        {this.renderContent()}
      </>
    );
  }

  renderEditButton = () => {
    return (
      !this.state.isEditing && (
        <Button small className="right-action-button" intent={Intent.PRIMARY} icon={<Edit />} onClick={this.toggleEdit}>
          Edit
        </Button>
      )
    );
  };

  renderContent = () => {
    if (this.state.isEditing) {
      const formProps = {
        onCancel: this.toggleEdit,
        renderFormComponents: this.renderDialogForm,
        onSubmit: this.editManagers,
      };
      return <BundleEditManagerForm {...formProps} />;
    }
    return (
      <BundleEditManagersTable
        bundleJid={this.props.bundle.jid}
        onEditManagers={this.editManagers}
        canDelete={this.props.role === 'ADMIN'}
      />
    );
  };

  renderDialogForm = (fields, addButton, removeButton) => (
    <>
      <div>{fields}</div>
      <div>
        <div className="bundle-manager-dialog-footer">
          <Button text="Cancel" onClick={this.toggleEdit} />
          <div className="bundle-manager-dialog-footer-actions">
            {removeButton}
            {addButton}
          </div>
        </div>
      </div>
    </>
  );

  editManagers = async (action, data) => {
    const usernames = data.usernames
      .split('\n')
      .map(s => s.trim())
      .filter(s => s.length > 0);
    if (action === 'add') {
      await this.props.onUpsertManagers(this.props.bundle.jid, usernames);
    } else if (action === 'remove') {
      await this.props.onDeleteManagers(this.props.bundle.jid, usernames);
    }
    if (this.state.isEditing) {
      this.toggleEdit();
    }
  };

  toggleEdit = () => {
    this.setState(prevState => ({
      isEditing: !prevState.isEditing,
    }));
  };
}

const mapDispatchToProps = {
  onUpsertManagers: bundleManagerAction.upsertManagers,
  onDeleteManagers: bundleManagerAction.deleteManagers,
};
export default connect(undefined, mapDispatchToProps)(BundleEditManagersTab);
