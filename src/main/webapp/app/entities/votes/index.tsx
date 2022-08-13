import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Votes from './votes';
import VotesDetail from './votes-detail';
import VotesUpdate from './votes-update';
import VotesDeleteDialog from './votes-delete-dialog';

const VotesRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Votes />} />
    <Route path="new" element={<VotesUpdate />} />
    <Route path=":id">
      <Route index element={<VotesDetail />} />
      <Route path="edit" element={<VotesUpdate />} />
      <Route path="delete" element={<VotesDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default VotesRoutes;
