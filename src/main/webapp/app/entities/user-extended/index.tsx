import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import UserExtended from './user-extended';
import UserExtendedDetail from './user-extended-detail';
import UserExtendedUpdate from './user-extended-update';
import UserExtendedDeleteDialog from './user-extended-delete-dialog';

const UserExtendedRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<UserExtended />} />
    <Route path="new" element={<UserExtendedUpdate />} />
    <Route path=":id">
      <Route index element={<UserExtendedDetail />} />
      <Route path="edit" element={<UserExtendedUpdate />} />
      <Route path="delete" element={<UserExtendedDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default UserExtendedRoutes;
