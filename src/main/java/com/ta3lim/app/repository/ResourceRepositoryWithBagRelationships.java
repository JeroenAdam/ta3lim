package com.ta3lim.app.repository;

import com.ta3lim.app.domain.Resource;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface ResourceRepositoryWithBagRelationships {
    Optional<Resource> fetchBagRelationships(Optional<Resource> resource);

    List<Resource> fetchBagRelationships(List<Resource> resources);

    Page<Resource> fetchBagRelationships(Page<Resource> resources);
}
