package com.ta3lim.app.service.mapper;

import com.ta3lim.app.domain.Subject;
import com.ta3lim.app.service.dto.SubjectDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Subject} and its DTO {@link SubjectDTO}.
 */
@Mapper(componentModel = "spring")
public interface SubjectMapper extends EntityMapper<SubjectDTO, Subject> {}
