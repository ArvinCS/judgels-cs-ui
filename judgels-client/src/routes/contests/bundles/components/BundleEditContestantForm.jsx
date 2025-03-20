import { Button, Intent } from '@blueprintjs/core';
import { useCallback, useEffect, useState } from 'react';
import { Field, Form } from 'react-final-form';

import { FormTextArea } from '../../../../components/forms/FormTextArea/FormTextArea';
import { Max100Lines, Required, composeValidators } from '../../../../components/forms/validations';

export default function BundleEditContestantForm({ onCancel, onSubmit, renderFormComponents }) {
  const [submitAction, setSubmitAction] = useState(null);
  const [formData, setFormData] = useState(null);

  const usernamesField = {
    name: 'usernames',
    label: 'Usernames',
    labelHelper: '(one username per line, max 100 users)',
    rows: 20,
    isCode: true,
    validate: composeValidators(Required, Max100Lines),
    autoFocus: true,
  };

  const fields = <Field component={FormTextArea} {...usernamesField} />;

  const handleFormSubmit = useCallback(() => {
    if (formData) {
      onSubmit(submitAction, formData);
    }
  }, [submitAction, formData, onSubmit]);

  useEffect(() => {
    handleFormSubmit();
  }, [submitAction, formData, handleFormSubmit]);

  return (
    <Form onSubmit={data => setFormData(data)}>
      {({ handleSubmit, submitting }) => {
        const addButton = (
          <Button
            type="submit"
            text="Add"
            intent={Intent.PRIMARY}
            loading={submitting}
            onClick={e => {
              e.preventDefault();
              setSubmitAction('add');
              handleSubmit();
            }}
          />
        );
        const removeButton = (
          <Button
            type="submit"
            text="Remove"
            intent={Intent.DANGER}
            loading={submitting}
            onClick={e => {
              e.preventDefault();
              setSubmitAction('remove');
              handleSubmit();
            }}
          />
        );
        return <form>{renderFormComponents(fields, addButton, removeButton)}</form>;
      }}
    </Form>
  );
}
