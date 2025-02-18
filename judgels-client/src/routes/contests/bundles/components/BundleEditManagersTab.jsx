import { Button, Intent, Spinner } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Component } from 'react';
import { connect } from 'react-redux';

import * as bundleActions from '../modules/bundleActions';
import { BundleEditManagersTable } from './BundleEditManagersTable';
import BundleEditManagerForm from './BundleEditManagersForm';

import './BundleEditManagersTab.scss';

class BundleEditGeneralTab extends Component {
  state = {
    isEditing: false,
    page: 1,
    loading: true,
  };

  componentDidMount() {
    this.fetchBundleManagersData();
  }

  render() {
    return (
      <>
        <h4>
          Managers settings
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
    const { bundle } = this.props;
    if (this.state.loading) {
      return <Spinner />;
    }
    if (this.state.isEditing) {
      const initialValues = {
        slug: bundle.slug,
        name: bundle.name,
        description: bundle.description,
      };
      const formProps = {
        onCancel: this.toggleEdit,
        renderFormComponents: this.renderDialogForm,
        onSubmit: this.editManagers,
      };
      return <BundleEditManagerForm {...formProps} />;
    }
    return <BundleEditManagersTable managers={this.state.managers} profilesMap={this.state.profilesMap} onRemoveManager={this.editManagers} />;
  };

  renderDialogForm = (fields, addButton, removeButton) => (
    <>
      <div>{fields}</div>
      <div>
        <div className='bundle-manager-dialog-footer'>
          <Button text="Cancel" onClick={this.toggleEdit} />
          <div className='bundle-manager-dialog-footer-actions'>
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
    this.fetchBundleManagersData();
    if (this.state.isEditing) {
      this.toggleEdit();
    }
  };

  toggleEdit = () => {
    this.setState(prevState => ({
      isEditing: !prevState.isEditing,
    }));
  };

  fetchBundleManagersData = async () => {
    const response = await this.props.onGetBundleManagersByJid(this.props.bundle.jid, this.state.page);

    const managers = response.data;
    const profilesMap = response.profilesMap;

    this.setState({ managers, profilesMap, loading: false });
  }
}

const mapDispatchToProps = {
  onGetBundleManagersByJid: bundleActions.getContestBundleManagersByJid,
  onUpsertManagers: bundleActions.upsertManagers,
  onDeleteManagers: bundleActions.deleteManagers,
};
export default connect(undefined, mapDispatchToProps)(BundleEditGeneralTab);