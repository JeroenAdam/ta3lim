import dayjs from 'dayjs';

export interface ISubject {
  id?: number;
  label?: string | null;
  creationDate?: string | null;
}

export const defaultValue: Readonly<ISubject> = {};
