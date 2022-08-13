import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Skill from './skill';
import SkillDetail from './skill-detail';
import SkillUpdate from './skill-update';
import SkillDeleteDialog from './skill-delete-dialog';

const SkillRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Skill />} />
    <Route path="new" element={<SkillUpdate />} />
    <Route path=":id">
      <Route index element={<SkillDetail />} />
      <Route path="edit" element={<SkillUpdate />} />
      <Route path="delete" element={<SkillDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SkillRoutes;
