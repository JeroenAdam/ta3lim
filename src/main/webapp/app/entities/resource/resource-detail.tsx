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
        <p></p>
        <p>
          <strong>
            <span id="title">
              <Translate contentKey="ta3LimApp.resource.title">Title</Translate>
            </span>
          </strong>
          : {resourceEntity.title}
        </p>
        <p>
          <strong>
            <span id="creationDate">
              <Translate contentKey="ta3LimApp.resource.creationDate">Creation Date</Translate>
            </span>
          </strong>
          :{' '}
          {resourceEntity.creationDate ? (
            <TextFormat value={resourceEntity.creationDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
          ) : null}
        </p>
        <p>
          <strong>
            <span id="description">
              <Translate contentKey="ta3LimApp.resource.description">Description</Translate>
            </span>
          </strong>
          : {resourceEntity.description}
        </p>
        <p>
          <strong>
            <span id="resourceType">
              <Translate contentKey="ta3LimApp.resource.resourceType">Resource Type</Translate>
            </span>
          </strong>
          : {resourceEntity.resourceType}
        </p>
        <p>
          <strong>
            <span id="angeRage">
              <Translate contentKey="ta3LimApp.resource.ageRange">Ange Rage</Translate>
            </span>
          </strong>
          : {resourceEntity.angeRage}
        </p>
        <p>
          {resourceEntity.file ? (
            <div>
              {resourceEntity.fileContentType ? (
                <a onClick={openFile(resourceEntity.fileContentType, resourceEntity.file)}>
                  <Translate contentKey="entity.action.open">File: Open</Translate>&nbsp;
                </a>
              ) : null}
              <span>
                {resourceEntity.fileContentType}, {byteSize(resourceEntity.file)}
              </span>
            </div>
          ) : null}
        </p>
        <p>
          <strong>
            <span id="url">
              <Translate contentKey="ta3LimApp.resource.url">Url</Translate>
            </span>
          </strong>
          :{' '}
          <a href={resourceEntity.url} target="_blank" rel="noopener noreferrer">
            {resourceEntity.url}
          </a>
        </p>
        <p>
          <strong>
            <span id="author">
              <Translate contentKey="ta3LimApp.resource.author">Author</Translate>
            </span>
          </strong>
          : {resourceEntity.author}
        </p>
        <p>
          <strong>
            <Translate contentKey="ta3LimApp.resource.user">Submitter</Translate>
          </strong>
          : {resourceEntity.user ? resourceEntity.user.login : ''}
        </p>
        <p>
          <strong>
            <span id="views">
              <Translate contentKey="ta3LimApp.resource.views">Views</Translate>
            </span>
          </strong>
          : {resourceEntity.views}
        </p>
        <p>
          <strong>
            <span id="votes">
              <Translate contentKey="ta3LimApp.resource.votes">Votes</Translate>
            </span>
          </strong>
          : {resourceEntity.votes}
        </p>
        <p>
          <strong>
            <Translate contentKey="ta3LimApp.resource.subject">Subject</Translate>
          </strong>
          : {resourceEntity.subject ? resourceEntity.subject.label : ''}
        </p>
        <p>
          <strong>
            <Translate contentKey="ta3LimApp.resource.topics">Topics</Translate>
          </strong>
          :{' '}
          {resourceEntity.topics
            ? resourceEntity.topics.map((val, i) => (
                <span key={val.id}>
                  <a>{val.label}</a>
                  {resourceEntity.topics && i === resourceEntity.topics.length - 1 ? '' : ', '}
                </span>
              ))
            : null}
        </p>
        <p>
          <strong>
            <Translate contentKey="ta3LimApp.resource.skills">Skills</Translate>
          </strong>
          :{' '}
          {resourceEntity.skills
            ? resourceEntity.skills.map((val, i) => (
                <span key={val.id}>
                  <a>{val.label}</a>
                  {resourceEntity.skills && i === resourceEntity.skills.length - 1 ? '' : ', '}
                </span>
              ))
            : null}
        </p>
        <p>
          <strong>
            <span id="lastUpdated">
              <Translate contentKey="ta3LimApp.resource.lastUpdated">Last Updated</Translate>
            </span>
          </strong>
          :{' '}
          {resourceEntity.lastUpdated ? <TextFormat value={resourceEntity.lastUpdated} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
        </p>
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
