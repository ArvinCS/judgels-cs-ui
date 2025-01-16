import { Button } from '@blueprintjs/core';
import { Component, useEffect } from 'react';
import { connect } from 'react-redux';

import * as ssoAuthActions from '../../modules/ssoAuthActions';

import './SSOAuth.scss';

class SSOAuth extends Component {
  state = {
    isAuthorizing: false,
  };

  render() {
    useEffect(async () => {
      const urlParams = new URLSearchParams(window.location.search);
      const service = urlParams.get("service");
      const ticket = urlParams.get("ticket");

      const { protocol, hostname, port, pathname } = window.location;
      const baseUrl = `${protocol}//${hostname}${port ? `:${port}` : ""}${pathname}`;

      if (ticket) {
        await this.props.onLogIn({
          ticket: ticket,
          serviceUrl: service || baseUrl,
        });
      }
    }, []);

    return (
      <button
        className='sso-auth'
        onClick={this.logIn}
        disabled={this.state.isAuthorizing}
      >
        <img src="/logos/makara_ui.png" alt="Makara UI" className='makara' />
        {this.state.isAuthorizing ? 'Authorizing...' : 'Log in with SSO UI'}
      </button>
    );
  }

  logIn = async response => {
    const casUrl = 'https://sso.ui.ac.id/cas2/';
    this.setState({ isAuthorizing: true });
    window.location.href = `${casUrl}?service=${encodeURIComponent(window.location.href)}`;
  };
}

const mapDispatchToProps = {
  onLogIn: ssoAuthActions.logIn,
  onRegister: ssoAuthActions.register,
};

export default connect(undefined, mapDispatchToProps)(SSOAuth);
