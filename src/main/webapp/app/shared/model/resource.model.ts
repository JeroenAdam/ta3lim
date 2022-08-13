import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { ISubject } from 'app/shared/model/subject.model';
import { ITopic } from 'app/shared/model/topic.model';
import { ISkill } from 'app/shared/model/skill.model';
import { ResourceType } from 'app/shared/model/enumerations/resource-type.model';
import { AgeRange } from 'app/shared/model/enumerations/age-range.model';

export interface IResource {
  id?: number;
  title?: string;
  creationDate?: string | null;
  description?: string | null;
  resourceType?: ResourceType | null;
  angeRage?: AgeRange | null;
  fileContentType?: string | null;
  file?: string | null;
  url?: string | null;
  author?: string | null;
  lastUpdated?: string | null;
  activated?: boolean | null;
  views?: number | null;
  votes?: number | null;
  approvedBy?: string | null;
  user?: IUser | null;
  subject?: ISubject | null;
  topics?: ITopic[] | null;
  skills?: ISkill[] | null;
}

export const defaultValue: Readonly<IResource> = {
  activated: false,
};
