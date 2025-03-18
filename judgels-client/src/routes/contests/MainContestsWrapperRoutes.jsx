import { Route } from 'react-router';

import ContestsRoutes from './ContestsRoutes';

function MainContestsWrapperRoutes() {
  return (
    <div>
      <Route path="/contests" component={ContestsRoutes} />
    </div>
  );
}

export default MainContestsWrapperRoutes;
