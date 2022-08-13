import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/shared/reducers/user-management';
import { INotification } from 'app/shared/model/notification.model';
import { NotificationType } from 'app/shared/model/enumerations/notification-type.model';
import { getEntity, updateEntity, createEntity, reset } from './notification.reducer';

export const NotificationUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const notificationEntity = useAppSelector(state => state.notification.entity);
  const loading = useAppSelector(state => state.notification.loading);
  const updating = useAppSelector(state => state.notification.updating);
  const updateSuccess = useAppSelector(state => state.notification.updateSuccess);
  const notificationTypeValues = Object.keys(NotificationType);

  const handleClose = () => {
    navigate('/notification' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...notificationEntity,
      ...values,
      user: users.find(it => it.id.toString() === values.user.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          notificationType: 'UNREAD_MESSAGES',
          ...notificationEntity,
          user: notificationEntity?.user?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="ta3LimApp.notification.home.createOrEditLabel" data-cy="NotificationCreateUpdateHeading">
            <Translate contentKey="ta3LimApp.notification.home.createOrEditLabel">Create or edit a Notification</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="notification-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('ta3LimApp.notification.creationDate')}
                id="notification-creationDate"
                name="creationDate"
                data-cy="creationDate"
                type="date"
              />
              <ValidatedField
                label={translate('ta3LimApp.notification.notificationDate')}
                id="notification-notificationDate"
                name="notificationDate"
                data-cy="notificationDate"
                type="date"
              />
              <ValidatedField
                label={translate('ta3LimApp.notification.notificationType')}
                id="notification-notificationType"
                name="notificationType"
                data-cy="notificationType"
                type="select"
              >
                {notificationTypeValues.map(notificationType => (
                  <option value={notificationType} key={notificationType}>
                    {translate('ta3LimApp.NotificationType.' + notificationType)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('ta3LimApp.notification.notificationText')}
                id="notification-notificationText"
                name="notificationText"
                data-cy="notificationText"
                type="text"
              />
              <ValidatedField
                label={translate('ta3LimApp.notification.isDelivered')}
                id="notification-isDelivered"
                name="isDelivered"
                data-cy="isDelivered"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('ta3LimApp.notification.isDeleted')}
                id="notification-isDeleted"
                name="isDeleted"
                data-cy="isDeleted"
                check
                type="checkbox"
              />
              <ValidatedField
                id="notification-user"
                name="user"
                data-cy="user"
                label={translate('ta3LimApp.notification.user')}
                type="select"
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/notification" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default NotificationUpdate;
