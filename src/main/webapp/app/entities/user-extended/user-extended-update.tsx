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
import { IUserExtended } from 'app/shared/model/user-extended.model';
import { CivilStatus } from 'app/shared/model/enumerations/civil-status.model';
import { Children } from 'app/shared/model/enumerations/children.model';
import { getEntity, updateEntity, createEntity, reset } from './user-extended.reducer';

export const UserExtendedUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const userExtendedEntity = useAppSelector(state => state.userExtended.entity);
  const loading = useAppSelector(state => state.userExtended.loading);
  const updating = useAppSelector(state => state.userExtended.updating);
  const updateSuccess = useAppSelector(state => state.userExtended.updateSuccess);
  const civilStatusValues = Object.keys(CivilStatus);
  const childrenValues = Object.keys(Children);

  const handleClose = () => {
    navigate('/user-extended' + location.search);
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
      ...userExtendedEntity,
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
          civilStatus: 'MARRIED',
          firstchild: 'AGE_00_03',
          secondchild: 'AGE_00_03',
          thirdchild: 'AGE_00_03',
          fourthchild: 'AGE_00_03',
          ...userExtendedEntity,
          user: userExtendedEntity?.user?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="ta3LimApp.userExtended.home.createOrEditLabel" data-cy="UserExtendedCreateUpdateHeading">
            <Translate contentKey="ta3LimApp.userExtended.home.createOrEditLabel">Create or edit a UserExtended</Translate>
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
                  id="user-extended-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('ta3LimApp.userExtended.lastLogin')}
                id="user-extended-lastLogin"
                name="lastLogin"
                data-cy="lastLogin"
                type="date"
              />
              <ValidatedField
                label={translate('ta3LimApp.userExtended.aboutMe')}
                id="user-extended-aboutMe"
                name="aboutMe"
                data-cy="aboutMe"
                type="text"
              />
              <ValidatedField
                label={translate('ta3LimApp.userExtended.occupation')}
                id="user-extended-occupation"
                name="occupation"
                data-cy="occupation"
                type="text"
              />
              <ValidatedField
                label={translate('ta3LimApp.userExtended.socialMedia')}
                id="user-extended-socialMedia"
                name="socialMedia"
                data-cy="socialMedia"
                type="text"
              />
              <ValidatedField
                label={translate('ta3LimApp.userExtended.civilStatus')}
                id="user-extended-civilStatus"
                name="civilStatus"
                data-cy="civilStatus"
                type="select"
              >
                {civilStatusValues.map(civilStatus => (
                  <option value={civilStatus} key={civilStatus}>
                    {translate('ta3LimApp.CivilStatus.' + civilStatus)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('ta3LimApp.userExtended.firstchild')}
                id="user-extended-firstchild"
                name="firstchild"
                data-cy="firstchild"
                type="select"
              >
                {childrenValues.map(children => (
                  <option value={children} key={children}>
                    {translate('ta3LimApp.Children.' + children)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('ta3LimApp.userExtended.secondchild')}
                id="user-extended-secondchild"
                name="secondchild"
                data-cy="secondchild"
                type="select"
              >
                {childrenValues.map(children => (
                  <option value={children} key={children}>
                    {translate('ta3LimApp.Children.' + children)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('ta3LimApp.userExtended.thirdchild')}
                id="user-extended-thirdchild"
                name="thirdchild"
                data-cy="thirdchild"
                type="select"
              >
                {childrenValues.map(children => (
                  <option value={children} key={children}>
                    {translate('ta3LimApp.Children.' + children)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('ta3LimApp.userExtended.fourthchild')}
                id="user-extended-fourthchild"
                name="fourthchild"
                data-cy="fourthchild"
                type="select"
              >
                {childrenValues.map(children => (
                  <option value={children} key={children}>
                    {translate('ta3LimApp.Children.' + children)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('ta3LimApp.userExtended.filesquota')}
                id="user-extended-filesquota"
                name="filesquota"
                data-cy="filesquota"
                type="text"
              />
              <ValidatedField
                label={translate('ta3LimApp.userExtended.approverSince')}
                id="user-extended-approverSince"
                name="approverSince"
                data-cy="approverSince"
                type="date"
              />
              <ValidatedField
                label={translate('ta3LimApp.userExtended.lastApproval')}
                id="user-extended-lastApproval"
                name="lastApproval"
                data-cy="lastApproval"
                type="date"
              />
              <ValidatedField
                id="user-extended-user"
                name="user"
                data-cy="user"
                label={translate('ta3LimApp.userExtended.user')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/user-extended" replace color="info">
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

export default UserExtendedUpdate;
