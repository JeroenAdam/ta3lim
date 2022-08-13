package com.ta3lim.app.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ta3lim.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VotesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Votes.class);
        Votes votes1 = new Votes();
        votes1.setId(1L);
        Votes votes2 = new Votes();
        votes2.setId(votes1.getId());
        assertThat(votes1).isEqualTo(votes2);
        votes2.setId(2L);
        assertThat(votes1).isNotEqualTo(votes2);
        votes1.setId(null);
        assertThat(votes1).isNotEqualTo(votes2);
    }
}
