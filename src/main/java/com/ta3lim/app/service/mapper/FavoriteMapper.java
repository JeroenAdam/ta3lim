package com.ta3lim.app.service.mapper;

import com.ta3lim.app.domain.Favorite;
import com.ta3lim.app.domain.Resource;
import com.ta3lim.app.domain.User;
import com.ta3lim.app.service.dto.FavoriteDTO;
import com.ta3lim.app.service.dto.ResourceDTO;
import com.ta3lim.app.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Favorite} and its DTO {@link FavoriteDTO}.
 */
@Mapper(componentModel = "spring")
public interface FavoriteMapper extends EntityMapper<FavoriteDTO, Favorite> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "resource", source = "resource", qualifiedByName = "resourceId")
    FavoriteDTO toDto(Favorite s);

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
