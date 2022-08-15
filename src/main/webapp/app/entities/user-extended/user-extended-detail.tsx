import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './user-extended.reducer';

export const UserExtendedDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const userExtendedEntity = useAppSelector(state => state.userExtended.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="userExtendedDetailsHeading">User {userExtendedEntity.user ? userExtendedEntity.user.login : ''}</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="aboutMe">
              <Translate contentKey="ta3LimApp.userExtended.aboutMe">About Me</Translate>
            </span>
          </dt>
          <dd>{userExtendedEntity.aboutMe}</dd>
          <dt>
            <span id="occupation">
              <Translate contentKey="ta3LimApp.userExtended.occupation">Occupation</Translate>
            </span>
          </dt>
          <dd>{userExtendedEntity.occupation}</dd>
          <dt>
            <span id="socialMedia">
              <Translate contentKey="ta3LimApp.userExtended.socialMedia">Social Media</Translate>
            </span>
          </dt>
          <dd>
            <a href={userExtendedEntity.socialMedia} target="_blank" rel="noopener noreferrer">
              {userExtendedEntity.socialMedia}
            </a>
          </dd>
          <dt>
            <span id="civilStatus">
              <Translate contentKey="ta3LimApp.userExtended.civilStatus">Civil Status</Translate>
            </span>
          </dt>
          <dd>{userExtendedEntity.civilStatus}</dd>
          <dt>
            <span id="firstchild">
              <Translate contentKey="ta3LimApp.userExtended.firstchild">Firstchild</Translate>
            </span>
          </dt>
          <dd>{userExtendedEntity.firstchild}</dd>
          <dt>
            <span id="secondchild">
              <Translate contentKey="ta3LimApp.userExtended.secondchild">Secondchild</Translate>
            </span>
          </dt>
          <dd>{userExtendedEntity.secondchild}</dd>
          <dt>
            <span id="thirdchild">
              <Translate contentKey="ta3LimApp.userExtended.thirdchild">Thirdchild</Translate>
            </span>
          </dt>
          <dd>{userExtendedEntity.thirdchild}</dd>
          <dt>
            <span id="fourthchild">
              <Translate contentKey="ta3LimApp.userExtended.fourthchild">Fourthchild</Translate>
            </span>
          </dt>
          <dd>{userExtendedEntity.fourthchild}</dd>
          <dt>
            <span id="filesquota">
              <Translate contentKey="ta3LimApp.userExtended.filesquota">Filesquota</Translate>
            </span>
          </dt>
          <dd>{userExtendedEntity.filesquota}</dd>
          <dt>
            <span id="approverSince">
              <Translate contentKey="ta3LimApp.userExtended.approverSince">Approver Since</Translate>
            </span>
          </dt>
          <dd>
            {userExtendedEntity.approverSince ? (
              <TextFormat value={userExtendedEntity.approverSince} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="lastApproval">
              <Translate contentKey="ta3LimApp.userExtended.lastApproval">Last Approval</Translate>
            </span>
          </dt>
          <dd>
            {userExtendedEntity.lastApproval ? (
              <TextFormat value={userExtendedEntity.lastApproval} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
        </dl>
        <Button tag={Link} to="/" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/user-extended/${userExtendedEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default UserExtendedDetail;
