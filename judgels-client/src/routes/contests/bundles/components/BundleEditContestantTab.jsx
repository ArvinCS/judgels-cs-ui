import { Button, Intent, Spinner } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Component } from 'react';
import { connect } from 'react-redux';

import BundleEditContestantForm from './BundleEditContestantForm';
import BundleEditContestantTable from './BundleEditContestantTable';

import * as bundleContestantActions from '../modules/bundleContestantActions';

import './BundleEditContestantTab.scss';

class BundleEditContestantTab extends Component {
  state = {
    isEditing: false,
    loading: true,
  };

  render() {
    return (
      <>
        <h4>
          Contestants settings
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
        onSubmit: this.editContestants,
      };
      return <BundleEditContestantForm {...formProps} />;
    }
    return <BundleEditContestantTable bundleJid={this.props.bundle.jid} onEditContestants={this.editContestants} />;
  };

  renderDialogForm = (fields, addButton, removeButton) => (
    <>
      <div>{fields}</div>
      <div>
        <div className="bundle-contestant-dialog-footer">
          <Button text="Cancel" onClick={this.toggleEdit} />
          <div className="bundle-contestant-dialog-footer-actions">
            {removeButton}
            {addButton}
          </div>
        </div>
      </div>
    </>
  );

  editContestants = async (action, data) => {
    const usernames = data.usernames
      .split('\n')
      .map(s => s.trim())
      .filter(s => s.length > 0);
    if (action === 'add') {
      await this.props.onUpsertContestants(this.props.bundle.jid, usernames);
    } else if (action === 'remove') {
      await this.props.onDeleteContestants(this.props.bundle.jid, usernames);
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
  onUpsertContestants: bundleContestantActions.upsertContestants,
  onDeleteContestants: bundleContestantActions.deleteContestants,
};
export default connect(undefined, mapDispatchToProps)(BundleEditContestantTab);
