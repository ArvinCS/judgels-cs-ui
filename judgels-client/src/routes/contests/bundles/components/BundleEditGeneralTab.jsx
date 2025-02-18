import { Button, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Component } from 'react';
import { connect } from 'react-redux';
import BundleEditGeneralForm from './BundleEditGeneralForm';
import { BundleEditGeneralTable } from './BundleEditGeneralTable';

import * as bundleActions from '../modules/bundleActions';

class BundleEditGeneralTab extends Component {
  state = {
    isEditing: false,
  };

  render() {
    return (
      <>
        <h4>
          General settings
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
    if (this.state.isEditing) {
      const initialValues = {
        slug: bundle.slug,
        name: bundle.name,
        description: bundle.description,
      };
      const formProps = {
        onCancel: this.toggleEdit,
      };
      return <BundleEditGeneralForm initialValues={initialValues} onSubmit={this.updateBundle} {...formProps} />;
    }
    return <BundleEditGeneralTable bundle={bundle} />;
  };

  updateBundle = async data => {
    const updateData = {
      slug: data.slug,
      name: data.name,
      description: data.description,
    };
    await this.props.onUpdateBundle(this.props.bundle.jid, this.props.bundle.slug, updateData);
    this.props.onEditDone();
    this.toggleEdit();
  };

  toggleEdit = () => {
    this.setState(prevState => ({
      isEditing: !prevState.isEditing,
    }));
  };
}

const mapDispatchToProps = {
  onUpdateBundle: bundleActions.updateContestBundle,
  onGetBundleByJid: bundleActions.getContestBundles,
};
export default connect(undefined, mapDispatchToProps)(BundleEditGeneralTab);
