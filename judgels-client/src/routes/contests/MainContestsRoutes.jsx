import { Route, Switch, withRouter } from 'react-router';

import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ContestsRoutes from './ContestsRoutes';
import MainContestsWrapperRoutes from './MainContestsWrapperRoutes';
import MainSingleContestRoutes from './contests/single/MainSingleContestRoutes';

function MainContestRoutes() {
  return (
    <div>
      <Switch>
        {/* <Route exact path="/contests" component={ContestsRoutes} /> */}
        <Route exact path="/contests" component={MainContestsWrapperRoutes} />
        <Route path="/contests/bundle" component={MainContestsWrapperRoutes} />
        <Route path="/contests/:contestSlug([a-zA-Z0-9-]+)" component={MainSingleContestRoutes} />
      </Switch>
    </div>
  );
}

export default withRouter(withBreadcrumb('Contests')(MainContestRoutes));
