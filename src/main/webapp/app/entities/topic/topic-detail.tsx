import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './topic.reducer';

export const TopicDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const topicEntity = useAppSelector(state => state.topic.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="topicDetailsHeading">
          <Translate contentKey="ta3LimApp.topic.detail.title">Topic</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{topicEntity.id}</dd>
          <dt>
            <span id="label">
              <Translate contentKey="ta3LimApp.topic.label">Label</Translate>
            </span>
          </dt>
          <dd>{topicEntity.label}</dd>
          <dt>
            <span id="creationDate">
              <Translate contentKey="ta3LimApp.topic.creationDate">Creation Date</Translate>
            </span>
          </dt>
          <dd>
            {topicEntity.creationDate ? <TextFormat value={topicEntity.creationDate} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
        </dl>
        <Button tag={Link} to="/topic" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/topic/${topicEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TopicDetail;
