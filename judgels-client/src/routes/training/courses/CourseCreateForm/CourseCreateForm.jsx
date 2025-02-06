import { Button, Intent } from '@blueprintjs/core';
import React, { useState } from 'react';
import { Field, Form } from 'react-final-form';

import { ButtonLink } from '../../../../components/ButtonLink/ButtonLink';
import { FormTextArea } from '../../../../components/forms/FormTextArea/FormTextArea';
import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { Required, Slug, composeValidators } from '../../../../components/forms/validations';
import { withSubmissionError } from '../../../../modules/form/submissionError';

import './CourseCreateForm.scss';

export default function CourseCreateForm({ onSubmit, renderFormComponents }) {
  const GroupsField = ({ name, label }) => {
    const [tags, setTags] = useState([]);

    const handleKeyDown = (event, input) => {
      if (event.key === 'Enter' && event.target.value.trim()) {
        event.preventDefault();
        const newTags = [...tags, event.target.value.trim()];
        setTags(newTags);
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
        {({ input }) => (
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
        )}
      </Field>
    );
  };

  const slugField = {
    name: 'slug',
    label: 'Slug',
    validate: composeValidators(Required, Slug),
    autoFocus: true,
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

  const whiteListField = {
    name: 'whitelist',
    label: 'White List',
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
      {/* <Field component={FormTextArea} {...whiteListField} /> */}
      <GroupsField {...groupsField} />
    </>
  );

  return (
    <Form onSubmit={withSubmissionError(onSubmit)}>
      {({ handleSubmit, submitting }) => {
        const submitButton = <Button type="submit" text="Create" intent={Intent.PRIMARY} loading={submitting} />;
        return <form onSubmit={handleSubmit}>{renderFormComponents(fields, submitButton)}</form>;
      }}
    </Form>
  );
}
