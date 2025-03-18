import { FormTable } from '../../../../components/forms/FormTable/FormTable';

export function BundleEditGeneralTable({ bundle }) {
  const rows = [
    { key: 'jid', title: 'JID', value: bundle.jid },
    { key: 'slug', title: 'Slug', value: bundle.slug },
    { key: 'name', title: 'Name', value: bundle.name },
    { key: 'description', title: 'Description', value: bundle.description },
  ];

  return <FormTable rows={rows} />;
}
