import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm, ValidatedBlobField } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/shared/reducers/user-management';
import { ISubject } from 'app/shared/model/subject.model';
import { getEntities as getSubjects } from 'app/entities/subject/subject.reducer';
import { ITopic } from 'app/shared/model/topic.model';
import { getEntities as getTopics } from 'app/entities/topic/topic.reducer';
import { ISkill } from 'app/shared/model/skill.model';
import { getEntities as getSkills } from 'app/entities/skill/skill.reducer';
import { IResource } from 'app/shared/model/resource.model';
import { ResourceType } from 'app/shared/model/enumerations/resource-type.model';
import { AgeRange } from 'app/shared/model/enumerations/age-range.model';
import { getEntity, updateEntity, createEntity, reset } from './resource.reducer';

export const ResourceUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const subjects = useAppSelector(state => state.subject.entities);
  const topics = useAppSelector(state => state.topic.entities);
  const skills = useAppSelector(state => state.skill.entities);
  const resourceEntity = useAppSelector(state => state.resource.entity);
  const loading = useAppSelector(state => state.resource.loading);
  const updating = useAppSelector(state => state.resource.updating);
  const updateSuccess = useAppSelector(state => state.resource.updateSuccess);
  const resourceTypeValues = Object.keys(ResourceType);
  const ageRangeValues = Object.keys(AgeRange);

  const account = useAppSelector(state => state.authentication.account);

  const handleClose = () => {
    navigate('/resource' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
    dispatch(getSubjects({}));
    dispatch(getTopics({}));
    dispatch(getSkills({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...resourceEntity,
      ...values,
      topics: mapIdList(values.topics),
      skills: mapIdList(values.skills),
      user: users.find(it => it.id.toString() === values.user.toString()),
      subject: subjects.find(it => it.id.toString() === values.subject.toString()),
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
          resourceType: 'ARTICLES',
          angeRage: 'AGE_ALL',
          ...resourceEntity,
          user: resourceEntity?.user?.id,
          subject: resourceEntity?.subject?.id,
          topics: resourceEntity?.topics?.map(e => e.id.toString()),
          skills: resourceEntity?.skills?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="ta3LimApp.resource.home.createOrEditLabel" data-cy="ResourceCreateUpdateHeading">
            {isNew ? (
              <Translate contentKey="ta3LimApp.resource.home.createLabelNew">Create a Resource</Translate>
            ) : (
              <Translate contentKey="ta3LimApp.resource.home.EditLabel">Edit a Resource</Translate>
            )}
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
                  hidden
                  id="resource-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label=""
                placeholder={translate('ta3LimApp.resource.title')}
                id="resource-title"
                name="title"
                data-cy="title"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <Link to="/subject/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
                <FontAwesomeIcon icon="plus" />
                &nbsp;New Subject
              </Link>
              <ValidatedField id="resource-subject" name="subject" data-cy="subject" label="" type="select">
                <option value="" disabled selected>
                  Choose a {translate('ta3LimApp.resource.subject')}
                </option>
                {subjects
                  ? subjects.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.label}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                row
                style={{ height: 100 }}
                label=""
                placeholder="Description (you can resize this box by dragging the mouse on the bottom right corner)"
                id="resource-description"
                name="description"
                data-cy="description"
                type="textarea"
              />
              <ValidatedField label="" id="resource-resourceType" name="resourceType" data-cy="resourceType" type="select">
                <option value="" disabled selected>
                  Choose a {translate('ta3LimApp.resource.resourceType')}
                </option>
                {resourceTypeValues.map(resourceType => (
                  <option value={resourceType} key={resourceType}>
                    {translate('ta3LimApp.ResourceType.' + resourceType)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField label="" id="resource-angeRage" name="angeRage" data-cy="angeRage" type="select">
                <option value="" disabled selected>
                  Choose a {translate('ta3LimApp.resource.ageRange')}
                </option>
                {ageRangeValues.map(ageRange => (
                  <option value={ageRange} key={ageRange}>
                    {translate('ta3LimApp.AgeRange.' + ageRange)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedBlobField
                label=""
                id="resource-file"
                name="file"
                data-cy="file"
                openActionLabel={translate('entity.action.open')}
              />
              <ValidatedField
                label=""
                placeholder={translate('ta3LimApp.resource.urlplaceholder')}
                id="resource-url"
                name="url"
                data-cy="url"
                type="text"
              />
              <ValidatedField
                label=""
                placeholder={translate('ta3LimApp.resource.authorplaceholder')}
                id="resource-author"
                name="author"
                data-cy="author"
                type="text"
              />
              <ValidatedField
                id="resource-user"
                name="user"
                data-cy="user"
                label={translate('ta3LimApp.resource.user')}
                type="text"
                value={account.id}
                hidden
              ></ValidatedField>
              <Link to="/topic/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
                <FontAwesomeIcon icon="plus" />
                &nbsp;New Topic
              </Link>
              <ValidatedField label="" id="resource-topics" data-cy="topics" type="select" multiple name="topics">
                <option value="" disabled>
                  (optional) Choose topic(s) below
                </option>
                <option value="" selected key="0" />
                {topics
                  ? topics.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.label}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Link to="/skill/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
                <FontAwesomeIcon icon="plus" />
                &nbsp;New Skill
              </Link>
              <ValidatedField label="" id="resource-skills" data-cy="skills" type="select" multiple name="skills">
                <option value="" disabled>
                  (optional) Choose skill(s) below
                </option>
                <option value="" selected key="0" />
                {skills
                  ? skills.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.label}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              {!isNew ? (
                <p>
                  {translate('ta3LimApp.resource.lastUpdated')}: {resourceEntity.lastUpdated}
                </p>
              ) : null}
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/resource" replace color="info">
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

export default ResourceUpdate;
