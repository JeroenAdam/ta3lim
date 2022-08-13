import dayjs from 'dayjs';
import { IResource } from 'app/shared/model/resource.model';

export interface ISkill {
  id?: number;
  label?: string | null;
  creationDate?: string | null;
  resources?: IResource[] | null;
}

export const defaultValue: Readonly<ISkill> = {};
