import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './votes.reducer';

export const VotesDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const votesEntity = useAppSelector(state => state.votes.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="votesDetailsHeading">
          <Translate contentKey="ta3LimApp.votes.detail.title">Votes</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{votesEntity.id}</dd>
          <dt>
            <Translate contentKey="ta3LimApp.votes.user">User</Translate>
          </dt>
          <dd>{votesEntity.user ? votesEntity.user.login : ''}</dd>
          <dt>
            <Translate contentKey="ta3LimApp.votes.resource">Resource</Translate>
          </dt>
          <dd>{votesEntity.resource ? votesEntity.resource.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/votes" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/votes/${votesEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default VotesDetail;
