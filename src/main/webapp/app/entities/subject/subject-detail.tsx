import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './subject.reducer';

export const SubjectDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const subjectEntity = useAppSelector(state => state.subject.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="subjectDetailsHeading">
          <Translate contentKey="ta3LimApp.subject.detail.title">Subject</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{subjectEntity.id}</dd>
          <dt>
            <span id="label">
              <Translate contentKey="ta3LimApp.subject.label">Label</Translate>
            </span>
          </dt>
          <dd>{subjectEntity.label}</dd>
          <dt>
            <span id="creationDate">
              <Translate contentKey="ta3LimApp.subject.creationDate">Creation Date</Translate>
            </span>
          </dt>
          <dd>
            {subjectEntity.creationDate ? (
              <TextFormat value={subjectEntity.creationDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
        </dl>
        <Button tag={Link} to="/subject" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/subject/${subjectEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default SubjectDetail;
