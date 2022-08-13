package com.ta3lim.app.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.ta3lim.app.domain.Favorite;
import com.ta3lim.app.repository.FavoriteRepository;
import com.ta3lim.app.repository.search.FavoriteSearchRepository;
import com.ta3lim.app.service.FavoriteService;
import com.ta3lim.app.service.dto.FavoriteDTO;
import com.ta3lim.app.service.mapper.FavoriteMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Favorite}.
 */
@Service
@Transactional
public class FavoriteServiceImpl implements FavoriteService {

    private final Logger log = LoggerFactory.getLogger(FavoriteServiceImpl.class);

    private final FavoriteRepository favoriteRepository;

    private final FavoriteMapper favoriteMapper;

    private final FavoriteSearchRepository favoriteSearchRepository;

    public FavoriteServiceImpl(
        FavoriteRepository favoriteRepository,
        FavoriteMapper favoriteMapper,
        FavoriteSearchRepository favoriteSearchRepository
    ) {
        this.favoriteRepository = favoriteRepository;
        this.favoriteMapper = favoriteMapper;
        this.favoriteSearchRepository = favoriteSearchRepository;
    }

    @Override
    public FavoriteDTO save(FavoriteDTO favoriteDTO) {
        log.debug("Request to save Favorite : {}", favoriteDTO);
        Favorite favorite = favoriteMapper.toEntity(favoriteDTO);
        favorite = favoriteRepository.save(favorite);
        FavoriteDTO result = favoriteMapper.toDto(favorite);
        favoriteSearchRepository.index(favorite);
        return result;
    }

    @Override
    public FavoriteDTO update(FavoriteDTO favoriteDTO) {
        log.debug("Request to save Favorite : {}", favoriteDTO);
        Favorite favorite = favoriteMapper.toEntity(favoriteDTO);
        favorite = favoriteRepository.save(favorite);
        FavoriteDTO result = favoriteMapper.toDto(favorite);
        favoriteSearchRepository.index(favorite);
        return result;
    }

    @Override
    public Optional<FavoriteDTO> partialUpdate(FavoriteDTO favoriteDTO) {
        log.debug("Request to partially update Favorite : {}", favoriteDTO);

        return favoriteRepository
            .findById(favoriteDTO.getId())
            .map(existingFavorite -> {
                favoriteMapper.partialUpdate(existingFavorite, favoriteDTO);

                return existingFavorite;
            })
            .map(favoriteRepository::save)
            .map(savedFavorite -> {
                favoriteSearchRepository.save(savedFavorite);

                return savedFavorite;
            })
            .map(favoriteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FavoriteDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Favorites");
        return favoriteRepository.findAll(pageable).map(favoriteMapper::toDto);
    }

    public Page<FavoriteDTO> findAllWithEagerRelationships(Pageable pageable) {
        return favoriteRepository.findAllWithEagerRelationships(pageable).map(favoriteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FavoriteDTO> findOne(Long id) {
        log.debug("Request to get Favorite : {}", id);
        return favoriteRepository.findOneWithEagerRelationships(id).map(favoriteMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Favorite : {}", id);
        favoriteRepository.deleteById(id);
        favoriteSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FavoriteDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Favorites for query {}", query);
        return favoriteSearchRepository.search(query, pageable).map(favoriteMapper::toDto);
    }
}
