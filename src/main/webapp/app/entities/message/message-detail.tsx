import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './message.reducer';

export const MessageDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const messageEntity = useAppSelector(state => state.message.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="messageDetailsHeading">
          <Translate contentKey="ta3LimApp.message.detail.title">Message</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{messageEntity.id}</dd>
          <dt>
            <span id="creationDate">
              <Translate contentKey="ta3LimApp.message.creationDate">Creation Date</Translate>
            </span>
          </dt>
          <dd>
            {messageEntity.creationDate ? (
              <TextFormat value={messageEntity.creationDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="messageText">
              <Translate contentKey="ta3LimApp.message.messageText">Message Text</Translate>
            </span>
          </dt>
          <dd>{messageEntity.messageText}</dd>
          <dt>
            <span id="isDelivered">
              <Translate contentKey="ta3LimApp.message.isDelivered">Is Delivered</Translate>
            </span>
          </dt>
          <dd>{messageEntity.isDelivered ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="ta3LimApp.message.receiver">Receiver</Translate>
          </dt>
          <dd>{messageEntity.receiver ? messageEntity.receiver.login : ''}</dd>
          <dt>
            <Translate contentKey="ta3LimApp.message.sender">Sender</Translate>
          </dt>
          <dd>{messageEntity.sender ? messageEntity.sender.login : ''}</dd>
        </dl>
        <Button tag={Link} to="/message" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/message/${messageEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default MessageDetail;
