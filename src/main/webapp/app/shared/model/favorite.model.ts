import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { IResource } from 'app/shared/model/resource.model';

export interface IFavorite {
  id?: number;
  creationDate?: string | null;
  user?: IUser | null;
  resource?: IResource | null;
}

export const defaultValue: Readonly<IFavorite> = {};
