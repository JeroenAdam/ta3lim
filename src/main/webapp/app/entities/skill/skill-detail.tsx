import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './skill.reducer';

export const SkillDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const skillEntity = useAppSelector(state => state.skill.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="skillDetailsHeading">
          <Translate contentKey="ta3LimApp.skill.detail.title">Skill</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{skillEntity.id}</dd>
          <dt>
            <span id="label">
              <Translate contentKey="ta3LimApp.skill.label">Label</Translate>
            </span>
          </dt>
          <dd>{skillEntity.label}</dd>
          <dt>
            <span id="creationDate">
              <Translate contentKey="ta3LimApp.skill.creationDate">Creation Date</Translate>
            </span>
          </dt>
          <dd>
            {skillEntity.creationDate ? <TextFormat value={skillEntity.creationDate} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
        </dl>
        <Button tag={Link} to="/skill" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/skill/${skillEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default SkillDetail;
