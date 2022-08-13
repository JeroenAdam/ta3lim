package com.ta3lim.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ta3lim.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserExtendedDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserExtendedDTO.class);
        UserExtendedDTO userExtendedDTO1 = new UserExtendedDTO();
        userExtendedDTO1.setId(1L);
        UserExtendedDTO userExtendedDTO2 = new UserExtendedDTO();
        assertThat(userExtendedDTO1).isNotEqualTo(userExtendedDTO2);
        userExtendedDTO2.setId(userExtendedDTO1.getId());
        assertThat(userExtendedDTO1).isEqualTo(userExtendedDTO2);
        userExtendedDTO2.setId(2L);
        assertThat(userExtendedDTO1).isNotEqualTo(userExtendedDTO2);
        userExtendedDTO1.setId(null);
        assertThat(userExtendedDTO1).isNotEqualTo(userExtendedDTO2);
    }
}
