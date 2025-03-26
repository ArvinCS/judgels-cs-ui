import { HTMLTable } from '@blueprintjs/core';
import { Edit, Properties } from '@blueprintjs/icons';

import './GroupUsersTable.scss';

export function GroupUsersTable({ users, groupsMap, onEditGroup }) {
  const renderHeader = () => {
    return (
      <thead>
        <tr>
          <th className="col-jid">JID</th>
          <th className="col-username">Username</th>
          <th>Groups</th>
          <th className="col-actions" />
        </tr>
      </thead>
    );
  };

  const renderRows = () => {
    const rows = users.map(userData => (
      <tr key={userData.jid}>
        <td>{userData.jid}</td>
        <td>{userData.username}</td>
        <td>
          {groupsMap[userData.jid] && groupsMap[userData.jid].length > 0 && (
            <>
              {groupsMap[userData.jid].map((tag, index) => (
                <div key={index} className="tag">
                  {tag}
                </div>
              ))}
            </>
          )}
          {(!groupsMap[userData.jid] || groupsMap[userData.jid].length == 0) && 'No groups'}
        </td>
        <td>
          <Edit className="action" intent="primary" onClick={() => onEditGroup(userData.jid)} />
        </td>
      </tr>
    ));

    return <tbody>{rows}</tbody>;
  };

  return (
    <HTMLTable striped className="table-list-condensed group-users-table">
      {renderHeader()}
      {renderRows()}
    </HTMLTable>
  );
}
