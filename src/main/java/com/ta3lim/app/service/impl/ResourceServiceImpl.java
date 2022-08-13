package com.ta3lim.app.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ta3lim.app.domain.Resource;
import com.ta3lim.app.repository.ResourceRepository;
import com.ta3lim.app.repository.search.ResourceSearchRepository;
import com.ta3lim.app.service.ResourceService;
import com.ta3lim.app.service.dto.ResourceDTO;
import com.ta3lim.app.service.mapper.ResourceMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Resource}.
 */
@Service
@Transactional
public class ResourceServiceImpl implements ResourceService {

    private final Logger log = LoggerFactory.getLogger(ResourceServiceImpl.class);

    private final ResourceRepository resourceRepository;

    private final ResourceMapper resourceMapper;

    private final ResourceSearchRepository resourceSearchRepository;

    public ResourceServiceImpl(
        ResourceRepository resourceRepository,
        ResourceMapper resourceMapper,
        ResourceSearchRepository resourceSearchRepository
    ) {
        this.resourceRepository = resourceRepository;
        this.resourceMapper = resourceMapper;
        this.resourceSearchRepository = resourceSearchRepository;
    }

    @Override
    public ResourceDTO save(ResourceDTO resourceDTO) {
        log.debug("Request to save Resource : {}", resourceDTO);
        Resource resource = resourceMapper.toEntity(resourceDTO);
        resource = resourceRepository.save(resource);
        ResourceDTO result = resourceMapper.toDto(resource);
        resourceSearchRepository.index(resource);
        return result;
    }

    @Override
    public ResourceDTO update(ResourceDTO resourceDTO) {
        log.debug("Request to save Resource : {}", resourceDTO);
        Resource resource = resourceMapper.toEntity(resourceDTO);
        resource = resourceRepository.save(resource);
        ResourceDTO result = resourceMapper.toDto(resource);
        resourceSearchRepository.index(resource);
        return result;
    }

    @Override
    public Optional<ResourceDTO> partialUpdate(ResourceDTO resourceDTO) {
        log.debug("Request to partially update Resource : {}", resourceDTO);

        return resourceRepository
            .findById(resourceDTO.getId())
            .map(existingResource -> {
                resourceMapper.partialUpdate(existingResource, resourceDTO);

                return existingResource;
            })
            .map(resourceRepository::save)
            .map(savedResource -> {
                resourceSearchRepository.save(savedResource);

                return savedResource;
            })
            .map(resourceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResourceDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Resources");
        return resourceRepository.findAll(pageable).map(resourceMapper::toDto);
    }

    public Page<ResourceDTO> findAllWithEagerRelationships(Pageable pageable) {
        return resourceRepository.findAllWithEagerRelationships(pageable).map(resourceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ResourceDTO> findOne(Long id) {
        log.debug("Request to get Resource : {}", id);
        return resourceRepository.findOneWithEagerRelationships(id).map(resourceMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Resource : {}", id);
        resourceRepository.deleteById(id);
        resourceSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResourceDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Resources for query {}", query);
        return resourceSearchRepository.search(query, pageable).map(resourceMapper::toDto);
    }
}
