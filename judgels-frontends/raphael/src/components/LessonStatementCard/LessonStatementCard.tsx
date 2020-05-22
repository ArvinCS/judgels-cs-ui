import * as React from 'react';

import { ContentCard } from '../../components/ContentCard/ContentCard';
import { LessonStatement } from '../../modules/api/sandalphon/lesson';
import { KatexText } from '../KatexText/KatexText';

import './LessonStatementCard.css';

export interface LessonStatementCardProps {
  alias: string;
  statement: LessonStatement;
}

export class LessonStatementCard extends React.PureComponent<LessonStatementCardProps> {
  render() {
    const { alias, statement } = this.props;
    return (
      <ContentCard className="lesson-statement">
        <h2 className="lesson-statement__name">
          {alias}. {statement.title}
        </h2>
        <div className="lesson-statement__text">
          <KatexText>{statement.text}</KatexText>
        </div>
      </ContentCard>
    );
  }
}
