import './footer.scss';

import React from 'react';
import { Translate } from 'react-jhipster';
import { Col, Row } from 'reactstrap';

const Footer = () => (
  <div className="footer page-content">
    <Row>
      <Col md="12">
        <p>
          &nbsp;Contact the{' '}
          <a href="https://www.adambahri.com/contact" target="_blank" rel="noopener noreferrer">
            <Translate contentKey="footer">Your footer</Translate>
          </a>{' '}
          of this application
        </p>
      </Col>
    </Row>
  </div>
);

export default Footer;
