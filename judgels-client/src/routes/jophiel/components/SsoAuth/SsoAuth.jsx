import { Component, useEffect } from 'react';
import { connect } from 'react-redux';

import * as ssoAuthActions from '../../modules/ssoAuthActions';

import './SsoAuth.scss';

class SsoAuth extends Component {
  state = {
    isAuthorizing: false,
  };

  componentDidMount() {
    const urlParams = new URLSearchParams(window.location.search);
    const service = urlParams.get('service');
    const ticket = urlParams.get('ticket');
  
    const { protocol, hostname, port, pathname } = window.location;
    const baseUrl = `${protocol}//${hostname}${port ? `:${port}` : ''}${pathname}`;
  
    if (ticket) {
      this.handleLoginWithTicket(ticket, service || baseUrl, urlParams);
    }
  }

  handleLoginWithTicket = async (ticket, service, urlParams) => {
    try {
      await this.props.onLogIn({
        ticket: ticket,
        serviceUrl: service,
      });
    } catch (error) {
      urlParams.delete('ticket');
      const newQuery = urlParams.toString();
      const newUrl = window.location.pathname + (newQuery ? '?' + newQuery : '');
      window.history.replaceState({}, '', newUrl);
    }
  };

  render() {
    return (
      <button className="sso-auth" onClick={this.logIn} disabled={this.state.isAuthorizing}>
        <img src="/logos/makara_ui.png" alt="Makara UI" className="makara" />
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
};

export default connect(undefined, mapDispatchToProps)(SsoAuth);
