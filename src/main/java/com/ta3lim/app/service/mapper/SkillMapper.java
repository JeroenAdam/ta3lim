package com.ta3lim.app.service.mapper;

import com.ta3lim.app.domain.Skill;
import com.ta3lim.app.service.dto.SkillDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Skill} and its DTO {@link SkillDTO}.
 */
@Mapper(componentModel = "spring")
public interface SkillMapper extends EntityMapper<SkillDTO, Skill> {}
