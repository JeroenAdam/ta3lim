package com.ta3lim.app.service.mapper;

import com.ta3lim.app.domain.Resource;
import com.ta3lim.app.domain.Skill;
import com.ta3lim.app.domain.Subject;
import com.ta3lim.app.domain.Topic;
import com.ta3lim.app.domain.User;
import com.ta3lim.app.service.dto.ResourceDTO;
import com.ta3lim.app.service.dto.SkillDTO;
import com.ta3lim.app.service.dto.SubjectDTO;
import com.ta3lim.app.service.dto.TopicDTO;
import com.ta3lim.app.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Resource} and its DTO {@link ResourceDTO}.
 */
@Mapper(componentModel = "spring")
public interface ResourceMapper extends EntityMapper<ResourceDTO, Resource> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "subject", source = "subject", qualifiedByName = "subjectLabel")
    @Mapping(target = "topics", source = "topics", qualifiedByName = "topicLabelSet")
    @Mapping(target = "skills", source = "skills", qualifiedByName = "skillLabelSet")
    ResourceDTO toDto(Resource s);

    @Mapping(target = "removeTopics", ignore = true)
    @Mapping(target = "removeSkills", ignore = true)
    Resource toEntity(ResourceDTO resourceDTO);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("subjectLabel")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "label", source = "label")
    SubjectDTO toDtoSubjectLabel(Subject subject);

    @Named("topicLabel")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "label", source = "label")
    TopicDTO toDtoTopicLabel(Topic topic);

    @Named("topicLabelSet")
    default Set<TopicDTO> toDtoTopicLabelSet(Set<Topic> topic) {
        return topic.stream().map(this::toDtoTopicLabel).collect(Collectors.toSet());
    }

    @Named("skillLabel")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "label", source = "label")
    SkillDTO toDtoSkillLabel(Skill skill);

    @Named("skillLabelSet")
    default Set<SkillDTO> toDtoSkillLabelSet(Set<Skill> skill) {
        return skill.stream().map(this::toDtoSkillLabel).collect(Collectors.toSet());
    }
}
