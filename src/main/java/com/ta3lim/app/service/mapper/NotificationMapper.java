package com.ta3lim.app.service.mapper;

import com.ta3lim.app.domain.Notification;
import com.ta3lim.app.domain.User;
import com.ta3lim.app.service.dto.NotificationDTO;
import com.ta3lim.app.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Notification} and its DTO {@link NotificationDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper extends EntityMapper<NotificationDTO, Notification> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    NotificationDTO toDto(Notification s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
