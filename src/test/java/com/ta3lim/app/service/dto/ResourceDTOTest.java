package com.ta3lim.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ta3lim.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ResourceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ResourceDTO.class);
        ResourceDTO resourceDTO1 = new ResourceDTO();
        resourceDTO1.setId(1L);
        ResourceDTO resourceDTO2 = new ResourceDTO();
        assertThat(resourceDTO1).isNotEqualTo(resourceDTO2);
        resourceDTO2.setId(resourceDTO1.getId());
        assertThat(resourceDTO1).isEqualTo(resourceDTO2);
        resourceDTO2.setId(2L);
        assertThat(resourceDTO1).isNotEqualTo(resourceDTO2);
        resourceDTO1.setId(null);
        assertThat(resourceDTO1).isNotEqualTo(resourceDTO2);
    }
}
