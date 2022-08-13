package com.ta3lim.app.service.mapper;

import com.ta3lim.app.domain.Topic;
import com.ta3lim.app.service.dto.TopicDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Topic} and its DTO {@link TopicDTO}.
 */
@Mapper(componentModel = "spring")
public interface TopicMapper extends EntityMapper<TopicDTO, Topic> {}
