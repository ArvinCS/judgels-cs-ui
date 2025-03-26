import { Button, Classes, Dialog } from '@blueprintjs/core';

import GroupUserEditForm from '../GroupUserEditForm/GroupUserEditForm';

export function GroupUserEditDialog({ userJid, groups, isOpen, onUpdateGroup, onCloseDialog }) {
  const renderDialogForm = (fields, submitButton) => (
    <>
      <div className={Classes.DIALOG_BODY}>{fields}</div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={onCloseDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  const updateGroup = async data => {
    await onUpdateGroup(userJid, data);
  };

  const initialValues = userJid && {
    groups,
  };

  const props = {
    renderFormComponents: renderDialogForm,
    onSubmit: updateGroup,
    initialValues,
  };

  return (
    <div className="content-card__section">
      <Dialog isOpen={isOpen} onClose={onCloseDialog} title="Edit group" canOutsideClickClose={false}>
        <GroupUserEditForm {...props} />
      </Dialog>
    </div>
  );
}
