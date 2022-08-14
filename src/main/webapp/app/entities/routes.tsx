import React from 'react';
import { Route, Navigate } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Resource from './resource';
import Subject from './subject';
import Topic from './topic';
import Skill from './skill';
import Votes from './votes';
import Notification from './notification';
import Message from './message';
import Favorite from './favorite';
import UserExtended from './user-extended';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="*" element={<Navigate replace to="/resource" />} />
        <Route path="resource/*" element={<Resource />} />
        <Route path="subject/*" element={<Subject />} />
        <Route path="topic/*" element={<Topic />} />
        <Route path="skill/*" element={<Skill />} />
        <Route path="votes/*" element={<Votes />} />
        <Route path="notification/*" element={<Notification />} />
        <Route path="message/*" element={<Message />} />
        <Route path="favorite/*" element={<Favorite />} />
        <Route path="user-extended/*" element={<UserExtended />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
