import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { CivilStatus } from 'app/shared/model/enumerations/civil-status.model';
import { Children } from 'app/shared/model/enumerations/children.model';

export interface IUserExtended {
  id?: number;
  lastLogin?: string | null;
  aboutMe?: string | null;
  occupation?: string | null;
  socialMedia?: string | null;
  civilStatus?: CivilStatus | null;
  firstchild?: Children | null;
  secondchild?: Children | null;
  thirdchild?: Children | null;
  fourthchild?: Children | null;
  filesquota?: number | null;
  approverSince?: string | null;
  lastApproval?: string | null;
  user?: IUser | null;
}

export const defaultValue: Readonly<IUserExtended> = {};
