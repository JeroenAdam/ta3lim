package com.ta3lim.app.service.mapper;

import com.ta3lim.app.domain.Resource;
import com.ta3lim.app.domain.User;
import com.ta3lim.app.domain.Votes;
import com.ta3lim.app.service.dto.ResourceDTO;
import com.ta3lim.app.service.dto.UserDTO;
import com.ta3lim.app.service.dto.VotesDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Votes} and its DTO {@link VotesDTO}.
 */
@Mapper(componentModel = "spring")
public interface VotesMapper extends EntityMapper<VotesDTO, Votes> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "resource", source = "resource", qualifiedByName = "resourceId")
    VotesDTO toDto(Votes s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("resourceId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ResourceDTO toDtoResourceId(Resource resource);
}
