package com.ta3lim.app.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ta3lim.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserExtendedTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserExtended.class);
        UserExtended userExtended1 = new UserExtended();
        userExtended1.setId(1L);
        UserExtended userExtended2 = new UserExtended();
        userExtended2.setId(userExtended1.getId());
        assertThat(userExtended1).isEqualTo(userExtended2);
        userExtended2.setId(2L);
        assertThat(userExtended1).isNotEqualTo(userExtended2);
        userExtended1.setId(null);
        assertThat(userExtended1).isNotEqualTo(userExtended2);
    }
}
