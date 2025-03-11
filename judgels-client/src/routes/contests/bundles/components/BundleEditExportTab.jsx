import { Button, Intent, Spinner } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Component } from 'react';
import { connect } from 'react-redux';

import * as bundleActions from '../modules/bundleActions';

import './BundleEditExportTab.scss';

class BundleEditExportTab extends Component {
  componentDidMount() {
    // this.fetchBundleManagersData();
  }

  render() {
    return (
      <>
        <h4>
          Export settings
        </h4>
        <hr />
        {this.renderContent()}
      </>
    );
  }

  renderContent = () => {
    const { bundle } = this.props;
    return <>
      <div className='option-row-container'>
        <div className='option-row-description'>
          <p>Export your contest bundle settings here.</p>
        </div>
        <div className='option-row-action'>
          <Button icon="export" intent={Intent.PRIMARY} text="Export" onClick={this.exportCSV} />
        </div>
      </div>
    </>;
  };

  exportCSV = async () => {
    await this.props.onExportScoreboard(this.props.bundle.jid);
  }
}

const mapDispatchToProps = {
  onExportScoreboard: bundleActions.exportScoreboard,
};
export default connect(undefined, mapDispatchToProps)(BundleEditExportTab);