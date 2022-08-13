import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { NotificationType } from 'app/shared/model/enumerations/notification-type.model';

export interface INotification {
  id?: number;
  creationDate?: string | null;
  notificationDate?: string | null;
  notificationType?: NotificationType | null;
  notificationText?: string | null;
  isDelivered?: boolean | null;
  isDeleted?: boolean | null;
  user?: IUser | null;
}

export const defaultValue: Readonly<INotification> = {
  isDelivered: false,
  isDeleted: false,
};
