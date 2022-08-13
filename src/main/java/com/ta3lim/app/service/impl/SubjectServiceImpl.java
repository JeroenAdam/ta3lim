package com.ta3lim.app.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ta3lim.app.domain.Subject;
import com.ta3lim.app.repository.SubjectRepository;
import com.ta3lim.app.repository.search.SubjectSearchRepository;
import com.ta3lim.app.service.SubjectService;
import com.ta3lim.app.service.dto.SubjectDTO;
import com.ta3lim.app.service.mapper.SubjectMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Subject}.
 */
@Service
@Transactional
public class SubjectServiceImpl implements SubjectService {

    private final Logger log = LoggerFactory.getLogger(SubjectServiceImpl.class);

    private final SubjectRepository subjectRepository;

    private final SubjectMapper subjectMapper;

    private final SubjectSearchRepository subjectSearchRepository;

    public SubjectServiceImpl(
        SubjectRepository subjectRepository,
        SubjectMapper subjectMapper,
        SubjectSearchRepository subjectSearchRepository
    ) {
        this.subjectRepository = subjectRepository;
        this.subjectMapper = subjectMapper;
        this.subjectSearchRepository = subjectSearchRepository;
    }

    @Override
    public SubjectDTO save(SubjectDTO subjectDTO) {
        log.debug("Request to save Subject : {}", subjectDTO);
        Subject subject = subjectMapper.toEntity(subjectDTO);
        subject = subjectRepository.save(subject);
        SubjectDTO result = subjectMapper.toDto(subject);
        subjectSearchRepository.index(subject);
        return result;
    }

    @Override
    public SubjectDTO update(SubjectDTO subjectDTO) {
        log.debug("Request to save Subject : {}", subjectDTO);
        Subject subject = subjectMapper.toEntity(subjectDTO);
        subject = subjectRepository.save(subject);
        SubjectDTO result = subjectMapper.toDto(subject);
        subjectSearchRepository.index(subject);
        return result;
    }

    @Override
    public Optional<SubjectDTO> partialUpdate(SubjectDTO subjectDTO) {
        log.debug("Request to partially update Subject : {}", subjectDTO);

        return subjectRepository
            .findById(subjectDTO.getId())
            .map(existingSubject -> {
                subjectMapper.partialUpdate(existingSubject, subjectDTO);

                return existingSubject;
            })
            .map(subjectRepository::save)
            .map(savedSubject -> {
                subjectSearchRepository.save(savedSubject);

                return savedSubject;
            })
            .map(subjectMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubjectDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Subjects");
        return subjectRepository.findAll(pageable).map(subjectMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SubjectDTO> findOne(Long id) {
        log.debug("Request to get Subject : {}", id);
        return subjectRepository.findById(id).map(subjectMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Subject : {}", id);
        subjectRepository.deleteById(id);
        subjectSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubjectDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Subjects for query {}", query);
        return subjectSearchRepository.search(query, pageable).map(subjectMapper::toDto);
    }
}
