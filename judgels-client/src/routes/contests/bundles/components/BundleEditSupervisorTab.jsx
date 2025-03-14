import { Button, Intent, Spinner } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Component } from 'react';
import { connect } from 'react-redux';

import * as bundleSupervisorAction from '../modules/bundleSupervisorActions';

import './BundleEditSupervisorTab.scss';
import BundleEditSupervisorTable from './BundleEditSupervisorTable';
import BundleEditSupervisorForm from './BundleEditSupervisorForm';

class BundleEditSupervisorTab extends Component {
  state = {
    isEditing: false,
    loading: true,
  };

  render() {
    return (
      <>
        <h4>
          Supervisors settings
          {this.renderEditButton()}
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
        onSubmit: this.editSupervisors,
      };
      return <BundleEditSupervisorForm {...formProps} />;
    }
    return <BundleEditSupervisorTable bundleJid={this.props.bundle.jid} onEditSupervisors={this.editSupervisors}/>;
  };

  renderDialogForm = (fields, addButton, removeButton) => (
    <>
      <div>{fields}</div>
      <div>
        <div className='bundle-supervisor-dialog-footer'>
          <Button text="Cancel" onClick={this.toggleEdit} />
          <div className='bundle-supervisor-dialog-footer-actions'>
            {removeButton}
            {addButton}
          </div>
        </div>
      </div>
    </>
  );

  editSupervisors = async (action, data) => {
    const usernames = data.usernames
      .split('\n')
      .map(s => s.trim())
      .filter(s => s.length > 0);
    if (action === 'add') {
      await this.props.onUpsertSupervisors(this.props.bundle.jid, usernames);
    } else if (action === 'remove') {
      await this.props.onDeleteSupervisors(this.props.bundle.jid, usernames);
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
  onUpsertSupervisors: bundleSupervisorAction.upsertSupervisors,
  onDeleteSupervisors: bundleSupervisorAction.deleteSupervisors,
};
export default connect(undefined, mapDispatchToProps)(BundleEditSupervisorTab);