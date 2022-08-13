import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Resource from './resource';
import ResourceDetail from './resource-detail';
import ResourceUpdate from './resource-update';
import ResourceDeleteDialog from './resource-delete-dialog';

const ResourceRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Resource />} />
    <Route path="new" element={<ResourceUpdate />} />
    <Route path=":id">
      <Route index element={<ResourceDetail />} />
      <Route path="edit" element={<ResourceUpdate />} />
      <Route path="delete" element={<ResourceDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ResourceRoutes;
