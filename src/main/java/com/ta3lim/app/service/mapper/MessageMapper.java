package com.ta3lim.app.service.mapper;

import com.ta3lim.app.domain.Message;
import com.ta3lim.app.domain.User;
import com.ta3lim.app.service.dto.MessageDTO;
import com.ta3lim.app.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Message} and its DTO {@link MessageDTO}.
 */
@Mapper(componentModel = "spring")
public interface MessageMapper extends EntityMapper<MessageDTO, Message> {
    @Mapping(target = "receiver", source = "receiver", qualifiedByName = "userLogin")
    @Mapping(target = "sender", source = "sender", qualifiedByName = "userLogin")
    MessageDTO toDto(Message s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
