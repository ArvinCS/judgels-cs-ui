import { Button, FormGroup, Intent, Section } from '@blueprintjs/core';
import { useState } from 'react';
import { Field, Form } from 'react-final-form';

import { FormCheckbox } from '../../../../components/forms/FormCheckbox/FormCheckbox';
import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { Required, Slug, composeValidators } from '../../../../components/forms/validations';
import { supervisorManagementPermissions } from '../../../../modules/api/uriel/contestSupervisor';
import { withSubmissionError } from '../../../../modules/form/submissionError';

import './ContestCreateForm.scss';

export default function ContestCreateForm({ hasBundle, onSubmit, renderFormComponents }) {
  const [isInsertionMenuOpen, setIsInsertionMenuOpen] = useState(false);

  const slugField = {
    name: 'slug',
    label: 'Slug',
    validate: composeValidators(Required, Slug),
    autoFocus: true,
  };

  const insertDefaultSupervisorField = {
    name: 'isInsertDefaultSupervisor',
    label: 'Insert Default Supervisor',
  };

  const insertDefaultContestantField = {
    name: 'isInsertDefaultContestant',
    label: 'Insert Default Contestant',
  };

  const allowAllPermissionsField = {
    name: 'supervisorPermissions.All',
    label: '(all)',
  };

  const permissionFields = supervisorManagementPermissions
    .filter(p => p !== 'All')
    .map(p => ({
      name: 'supervisorPermissions.' + p,
      label: p,
      small: true,
    }));

  const renderFields = values => {
    return (
      <>
        <Field component={FormTextInput} {...slugField} />
        {hasBundle && (
          <Section
            key="collapsable-insertion-section"
            collapsible={true}
            collapseProps={{
              isOpen: isInsertionMenuOpen,
              onToggle: () => setIsInsertionMenuOpen(!isInsertionMenuOpen),
            }}
            compact={true}
            title={'Additional Settings'}
          >
            <div className="form__section">
              <Field key={'isInsertDefaultSupervisor'} component={FormCheckbox} {...insertDefaultSupervisorField} />
              {values.isInsertDefaultSupervisor && (
                <>
                  <FormGroup label="Supervisor permissions" className="form__group--checkboxes">
                    <Field component={FormCheckbox} {...allowAllPermissionsField} />
                    {(values.supervisorPermissions === undefined || !values.supervisorPermissions.All) &&
                      permissionFields.map(f => <Field key={f.name} component={FormCheckbox} {...f} />)}
                  </FormGroup>
                </>
              )}
              <Field key={'isInsertDefaultContestant'} component={FormCheckbox} {...insertDefaultContestantField} />
            </div>
          </Section>
        )}
      </>
    );
  };

  return (
    <Form onSubmit={withSubmissionError(onSubmit)}>
      {({ handleSubmit, values, submitting }) => {
        const submitButton = <Button type="submit" text="Create" intent={Intent.PRIMARY} loading={submitting} />;
        return <form onSubmit={handleSubmit}>{renderFormComponents(renderFields(values), submitButton)}</form>;
      }}
    </Form>
  );
}
