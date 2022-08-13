import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';

export interface IMessage {
  id?: number;
  creationDate?: string | null;
  messageText?: string | null;
  isDelivered?: boolean | null;
  receiver?: IUser | null;
  sender?: IUser | null;
}

export const defaultValue: Readonly<IMessage> = {
  isDelivered: false,
};
