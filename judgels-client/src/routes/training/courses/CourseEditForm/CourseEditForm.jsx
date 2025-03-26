import { Button, Intent } from '@blueprintjs/core';
import React, { useState } from 'react';
import { Field, Form } from 'react-final-form';

import { FormTextArea } from '../../../../components/forms/FormTextArea/FormTextArea';
import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { Required, Slug, composeValidators } from '../../../../components/forms/validations';
import { withSubmissionError } from '../../../../modules/form/submissionError';

export default function CourseEditForm({ onSubmit, initialValues, renderFormComponents }) {
  const GroupsField = ({ name, label }) => {
    const [tags, setTags] = useState([]);

    const handleKeyDown = (event, input) => {
      if (event.key === 'Enter' && event.target.value.trim()) {
        event.preventDefault();
        const newTags = [...tags, event.target.value.trim()];
        if (UserGroup(event.target.value)) {
          setError(UserGroup(event.target.value));
        } else {
          setError(undefined);
          setTags(newTags);
        }
        event.target.value = '';
        input.onChange(newTags);
      }
    };

    const removeTag = (tagToRemove, input) => {
      const newTags = tags.filter(tag => tag !== tagToRemove);
      setTags(newTags);
      input.onChange(newTags);
    };

    return (
      <Field name={name}>
        {({ input }) => {
          // Initialize tags from input value
          if (input.value && input.value.length > 0 && tags.length === 0) {
            setTags(input.value);
          }

          return (
            <div className="tags-wrapper">
              <label className="tags-header" htmlFor="tags">
                {label}
              </label>
              <input
                type="text"
                id="tags"
                placeholder="Add tags and press Enter"
                onKeyDown={event => handleKeyDown(event, input)}
              />
              {error && <div className="error-message">{error}</div>}
              <div className="tags-container">
                {tags.map((tag, index) => (
                  <div key={index} className="tag">
                    {tag}
                    <button type="button" onClick={() => removeTag(tag, input)}>
                      x
                    </button>
                  </div>
                ))}
              </div>
            </div>
          );
        }}
      </Field>
    );
  };

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
  const groupsField = {
    name: 'groups',
    label: 'Groups',
  };

  const fields = (
    <>
      <Field component={FormTextInput} {...slugField} />
      <Field component={FormTextInput} {...nameField} />
      <Field component={FormTextArea} {...descriptionField} />
      <GroupsField {...groupsField} />
    </>
  );

  return (
    <Form onSubmit={withSubmissionError(onSubmit)} initialValues={initialValues}>
      {({ handleSubmit, submitting }) => {
        const submitButton = <Button type="submit" text="Update" intent={Intent.PRIMARY} loading={submitting} />;
        return <form onSubmit={handleSubmit}>{renderFormComponents(fields, submitButton)}</form>;
      }}
    </Form>
  );
}
