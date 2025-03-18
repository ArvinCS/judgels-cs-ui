import { Button, HTMLTable, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { FormTableTextArea } from '../../../../components/forms/FormTableTextArea/FormTableTextArea';
import { FormTableTextInput } from '../../../../components/forms/FormTableTextInput/FormTableTextInput';
import { Required, Slug, composeValidators } from '../../../../components/forms/validations';
import { withSubmissionError } from '../../../../modules/form/submissionError';

export default function BundleEditGeneralForm({ onSubmit, initialValues, onCancel }) {
  const slugField = {
    name: 'slug',
    label: 'Slug',
    validate: composeValidators(Required, Slug),
  };

  const nameField = {
    name: 'name',
    label: 'Name',
    validate: Required,
  };

  const descriptionField = {
    name: 'description',
    label: 'Description',
    rows: 5,
  };

  return (
    <Form onSubmit={withSubmissionError(onSubmit)} initialValues={initialValues}>
      {({ handleSubmit, submitting }) => (
        <form onSubmit={handleSubmit}>
          <HTMLTable striped>
            <tbody>
              <Field component={FormTableTextInput} {...slugField} />
              <Field component={FormTableTextInput} {...nameField} />
              <Field component={FormTableTextArea} {...descriptionField} />
            </tbody>
          </HTMLTable>
          <hr />
          <ActionButtons>
            <Button text="Cancel" disabled={submitting} onClick={onCancel} />
            <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={submitting} />
          </ActionButtons>
        </form>
      )}
    </Form>
  );
}
