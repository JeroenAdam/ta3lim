package com.ta3lim.app.service.mapper;

import com.ta3lim.app.domain.User;
import com.ta3lim.app.domain.UserExtended;
import com.ta3lim.app.service.dto.UserDTO;
import com.ta3lim.app.service.dto.UserExtendedDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserExtended} and its DTO {@link UserExtendedDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserExtendedMapper extends EntityMapper<UserExtendedDTO, UserExtended> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    UserExtendedDTO toDto(UserExtended s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
