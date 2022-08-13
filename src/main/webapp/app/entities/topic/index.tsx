import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Topic from './topic';
import TopicDetail from './topic-detail';
import TopicUpdate from './topic-update';
import TopicDeleteDialog from './topic-delete-dialog';

const TopicRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Topic />} />
    <Route path="new" element={<TopicUpdate />} />
    <Route path=":id">
      <Route index element={<TopicDetail />} />
      <Route path="edit" element={<TopicUpdate />} />
      <Route path="delete" element={<TopicDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TopicRoutes;
