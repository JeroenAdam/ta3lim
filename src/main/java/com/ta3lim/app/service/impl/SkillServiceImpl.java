package com.ta3lim.app.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ta3lim.app.domain.Skill;
import com.ta3lim.app.repository.SkillRepository;
import com.ta3lim.app.repository.search.SkillSearchRepository;
import com.ta3lim.app.service.SkillService;
import com.ta3lim.app.service.dto.SkillDTO;
import com.ta3lim.app.service.mapper.SkillMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Skill}.
 */
@Service
@Transactional
public class SkillServiceImpl implements SkillService {

    private final Logger log = LoggerFactory.getLogger(SkillServiceImpl.class);

    private final SkillRepository skillRepository;

    private final SkillMapper skillMapper;

    private final SkillSearchRepository skillSearchRepository;

    public SkillServiceImpl(SkillRepository skillRepository, SkillMapper skillMapper, SkillSearchRepository skillSearchRepository) {
        this.skillRepository = skillRepository;
        this.skillMapper = skillMapper;
        this.skillSearchRepository = skillSearchRepository;
    }

    @Override
    public SkillDTO save(SkillDTO skillDTO) {
        log.debug("Request to save Skill : {}", skillDTO);
        Skill skill = skillMapper.toEntity(skillDTO);
        skill = skillRepository.save(skill);
        SkillDTO result = skillMapper.toDto(skill);
        skillSearchRepository.index(skill);
        return result;
    }

    @Override
    public SkillDTO update(SkillDTO skillDTO) {
        log.debug("Request to save Skill : {}", skillDTO);
        Skill skill = skillMapper.toEntity(skillDTO);
        skill = skillRepository.save(skill);
        SkillDTO result = skillMapper.toDto(skill);
        skillSearchRepository.index(skill);
        return result;
    }

    @Override
    public Optional<SkillDTO> partialUpdate(SkillDTO skillDTO) {
        log.debug("Request to partially update Skill : {}", skillDTO);

        return skillRepository
            .findById(skillDTO.getId())
            .map(existingSkill -> {
                skillMapper.partialUpdate(existingSkill, skillDTO);

                return existingSkill;
            })
            .map(skillRepository::save)
            .map(savedSkill -> {
                skillSearchRepository.save(savedSkill);

                return savedSkill;
            })
            .map(skillMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SkillDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Skills");
        return skillRepository.findAll(pageable).map(skillMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SkillDTO> findOne(Long id) {
        log.debug("Request to get Skill : {}", id);
        return skillRepository.findById(id).map(skillMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Skill : {}", id);
        skillRepository.deleteById(id);
        skillSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SkillDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Skills for query {}", query);
        return skillSearchRepository.search(query, pageable).map(skillMapper::toDto);
    }
}
