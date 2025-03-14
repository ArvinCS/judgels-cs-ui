import { Console, GroupItem } from '@blueprintjs/icons';
import { Route, withRouter } from 'react-router';

import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import BundlesPage from './bundles/BundlesPage/BundlesPage';
import ContestsPage from './contests/ContestsPage/ContestsPage';

function ContestsRoutes() {
  const sidebarItems = [
    {
      id: '@',
      titleIcon: <Console />,
      title: 'Contests',
      routeComponent: Route,
      component: ContestsPage,
    },
    {
      id: 'bundle',
      titleIcon: <GroupItem />,
      title: 'Bundles',
      routeComponent: Route,
      component: BundlesPage,
    },
  ];

  const contentWithSidebarProps = {
    title: 'Menu',
    items: sidebarItems,
  };

  return (
    <FullPageLayout>
      <ContentWithSidebar {...contentWithSidebarProps} />
    </FullPageLayout>
  );
}

export default withRouter(ContestsRoutes);
