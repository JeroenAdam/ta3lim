import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, openFile, byteSize, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './resource.reducer';

export const ResourceDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const resourceEntity = useAppSelector(state => state.resource.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="resourceDetailsHeading">
          <Translate contentKey="ta3LimApp.resource.detail.title">Resource</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{resourceEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="ta3LimApp.resource.title">Title</Translate>
            </span>
          </dt>
          <dd>{resourceEntity.title}</dd>
          <dt>
            <span id="creationDate">
              <Translate contentKey="ta3LimApp.resource.creationDate">Creation Date</Translate>
            </span>
          </dt>
          <dd>
            {resourceEntity.creationDate ? (
              <TextFormat value={resourceEntity.creationDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="description">
              <Translate contentKey="ta3LimApp.resource.description">Description</Translate>
            </span>
          </dt>
          <dd>{resourceEntity.description}</dd>
          <dt>
            <span id="resourceType">
              <Translate contentKey="ta3LimApp.resource.resourceType">Resource Type</Translate>
            </span>
          </dt>
          <dd>{resourceEntity.resourceType}</dd>
          <dt>
            <span id="angeRage">
              <Translate contentKey="ta3LimApp.resource.angeRage">Ange Rage</Translate>
            </span>
          </dt>
          <dd>{resourceEntity.angeRage}</dd>
          <dt>
            <span id="file">
              <Translate contentKey="ta3LimApp.resource.file">File</Translate>
            </span>
          </dt>
          <dd>
            {resourceEntity.file ? (
              <div>
                {resourceEntity.fileContentType ? (
                  <a onClick={openFile(resourceEntity.fileContentType, resourceEntity.file)}>
                    <Translate contentKey="entity.action.open">Open</Translate>&nbsp;
                  </a>
                ) : null}
                <span>
                  {resourceEntity.fileContentType}, {byteSize(resourceEntity.file)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="url">
              <Translate contentKey="ta3LimApp.resource.url">Url</Translate>
            </span>
          </dt>
          <dd>{resourceEntity.url}</dd>
          <dt>
            <span id="author">
              <Translate contentKey="ta3LimApp.resource.author">Author</Translate>
            </span>
          </dt>
          <dd>{resourceEntity.author}</dd>
          <dt>
            <span id="lastUpdated">
              <Translate contentKey="ta3LimApp.resource.lastUpdated">Last Updated</Translate>
            </span>
          </dt>
          <dd>
            {resourceEntity.lastUpdated ? (
              <TextFormat value={resourceEntity.lastUpdated} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="activated">
              <Translate contentKey="ta3LimApp.resource.activated">Activated</Translate>
            </span>
          </dt>
          <dd>{resourceEntity.activated ? 'true' : 'false'}</dd>
          <dt>
            <span id="views">
              <Translate contentKey="ta3LimApp.resource.views">Views</Translate>
            </span>
          </dt>
          <dd>{resourceEntity.views}</dd>
          <dt>
            <span id="votes">
              <Translate contentKey="ta3LimApp.resource.votes">Votes</Translate>
            </span>
          </dt>
          <dd>{resourceEntity.votes}</dd>
          <dt>
            <span id="approvedBy">
              <Translate contentKey="ta3LimApp.resource.approvedBy">Approved By</Translate>
            </span>
          </dt>
          <dd>{resourceEntity.approvedBy}</dd>
          <dt>
            <Translate contentKey="ta3LimApp.resource.user">User</Translate>
          </dt>
          <dd>{resourceEntity.user ? resourceEntity.user.login : ''}</dd>
          <dt>
            <Translate contentKey="ta3LimApp.resource.subject">Subject</Translate>
          </dt>
          <dd>{resourceEntity.subject ? resourceEntity.subject.label : ''}</dd>
          <dt>
            <Translate contentKey="ta3LimApp.resource.topics">Topics</Translate>
          </dt>
          <dd>
            {resourceEntity.topics
              ? resourceEntity.topics.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.label}</a>
                    {resourceEntity.topics && i === resourceEntity.topics.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
          <dt>
            <Translate contentKey="ta3LimApp.resource.skills">Skills</Translate>
          </dt>
          <dd>
            {resourceEntity.skills
              ? resourceEntity.skills.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.label}</a>
                    {resourceEntity.skills && i === resourceEntity.skills.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/resource" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/resource/${resourceEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ResourceDetail;
