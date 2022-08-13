import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Favorite from './favorite';
import FavoriteDetail from './favorite-detail';
import FavoriteUpdate from './favorite-update';
import FavoriteDeleteDialog from './favorite-delete-dialog';

const FavoriteRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Favorite />} />
    <Route path="new" element={<FavoriteUpdate />} />
    <Route path=":id">
      <Route index element={<FavoriteDetail />} />
      <Route path="edit" element={<FavoriteUpdate />} />
      <Route path="delete" element={<FavoriteDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default FavoriteRoutes;
