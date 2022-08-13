import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './favorite.reducer';

export const FavoriteDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const favoriteEntity = useAppSelector(state => state.favorite.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="favoriteDetailsHeading">
          <Translate contentKey="ta3LimApp.favorite.detail.title">Favorite</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{favoriteEntity.id}</dd>
          <dt>
            <span id="creationDate">
              <Translate contentKey="ta3LimApp.favorite.creationDate">Creation Date</Translate>
            </span>
          </dt>
          <dd>
            {favoriteEntity.creationDate ? (
              <TextFormat value={favoriteEntity.creationDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="ta3LimApp.favorite.user">User</Translate>
          </dt>
          <dd>{favoriteEntity.user ? favoriteEntity.user.login : ''}</dd>
          <dt>
            <Translate contentKey="ta3LimApp.favorite.resource">Resource</Translate>
          </dt>
          <dd>{favoriteEntity.resource ? favoriteEntity.resource.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/favorite" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/favorite/${favoriteEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default FavoriteDetail;
