import React from 'react';

import { UncontrolledDropdown, DropdownToggle, DropdownMenu } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

export const NavDropdown = props => (
  <UncontrolledDropdown id={props.id} data-cy={props['data-cy']}>
    <DropdownToggle className="input-group-addon">
      &nbsp;
      <FontAwesomeIcon icon={props.icon} />
      <span>&nbsp;{props.name}</span>
    </DropdownToggle>
    <DropdownMenu end style={props.style}>
      {props.children}
    </DropdownMenu>
  </UncontrolledDropdown>
);
