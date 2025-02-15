import { HTMLTable, Button, Intent } from '@blueprintjs/core';
import { Trash } from '@blueprintjs/icons';

import { UserRef } from '../../../../components/UserRef/UserRef';

export function BundleEditManagersTable({ managers, profilesMap, onRemoveManager }) {
  const renderHeader = () => {
    return (
      <thead>
        <tr>
          <th className="col-user">User</th>
          <th className="col-actions">Actions</th>
        </tr>
      </thead>
    );
  };

  const renderRows = () => {
    const sortedManagers = managers.page.slice().sort((c1, c2) => {
      const username1 = (profilesMap[c1.userJid] && profilesMap[c1.userJid].username) || 'ZZ';
      const username2 = (profilesMap[c2.userJid] && profilesMap[c2.userJid].username) || 'ZZ';
      return username1.localeCompare(username2);
    });

    const rows = sortedManagers.map(manager => (
      <tr key={manager.userJid}>
        <td>
          <UserRef profile={profilesMap[manager.userJid]} />
        </td>
        <td>
          <Button small intent={Intent.DANGER} icon={<Trash />} onClick={() => onRemoveManager('remove', {'usernames': profilesMap[manager.userJid].username})} />
        </td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };

  return (
    <HTMLTable striped className="table-list-condensed">
      {renderHeader()}
      {renderRows()}
    </HTMLTable>
  );
}