import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/resource">
        <Translate contentKey="global.menu.entities.resource" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/subject">
        <Translate contentKey="global.menu.entities.subject" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/topic">
        <Translate contentKey="global.menu.entities.topic" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/skill">
        <Translate contentKey="global.menu.entities.skill" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/votes">
        <Translate contentKey="global.menu.entities.votes" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/notification">
        <Translate contentKey="global.menu.entities.notification" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/message">
        <Translate contentKey="global.menu.entities.message" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/favorite">
        <Translate contentKey="global.menu.entities.favorite" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/user-extended">
        <Translate contentKey="global.menu.entities.userExtended" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
