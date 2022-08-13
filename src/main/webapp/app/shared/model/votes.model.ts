import { IUser } from 'app/shared/model/user.model';
import { IResource } from 'app/shared/model/resource.model';

export interface IVotes {
  id?: number;
  user?: IUser | null;
  resource?: IResource | null;
}

export const defaultValue: Readonly<IVotes> = {};
