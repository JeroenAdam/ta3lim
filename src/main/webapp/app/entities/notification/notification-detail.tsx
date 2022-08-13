import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './notification.reducer';

export const NotificationDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const notificationEntity = useAppSelector(state => state.notification.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="notificationDetailsHeading">
          <Translate contentKey="ta3LimApp.notification.detail.title">Notification</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{notificationEntity.id}</dd>
          <dt>
            <span id="creationDate">
              <Translate contentKey="ta3LimApp.notification.creationDate">Creation Date</Translate>
            </span>
          </dt>
          <dd>
            {notificationEntity.creationDate ? (
              <TextFormat value={notificationEntity.creationDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="notificationDate">
              <Translate contentKey="ta3LimApp.notification.notificationDate">Notification Date</Translate>
            </span>
          </dt>
          <dd>
            {notificationEntity.notificationDate ? (
              <TextFormat value={notificationEntity.notificationDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="notificationType">
              <Translate contentKey="ta3LimApp.notification.notificationType">Notification Type</Translate>
            </span>
          </dt>
          <dd>{notificationEntity.notificationType}</dd>
          <dt>
            <span id="notificationText">
              <Translate contentKey="ta3LimApp.notification.notificationText">Notification Text</Translate>
            </span>
          </dt>
          <dd>{notificationEntity.notificationText}</dd>
          <dt>
            <span id="isDelivered">
              <Translate contentKey="ta3LimApp.notification.isDelivered">Is Delivered</Translate>
            </span>
          </dt>
          <dd>{notificationEntity.isDelivered ? 'true' : 'false'}</dd>
          <dt>
            <span id="isDeleted">
              <Translate contentKey="ta3LimApp.notification.isDeleted">Is Deleted</Translate>
            </span>
          </dt>
          <dd>{notificationEntity.isDeleted ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="ta3LimApp.notification.user">User</Translate>
          </dt>
          <dd>{notificationEntity.user ? notificationEntity.user.login : ''}</dd>
        </dl>
        <Button tag={Link} to="/notification" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/notification/${notificationEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default NotificationDetail;
