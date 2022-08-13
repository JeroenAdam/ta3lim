package com.ta3lim.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ta3lim.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VotesDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(VotesDTO.class);
        VotesDTO votesDTO1 = new VotesDTO();
        votesDTO1.setId(1L);
        VotesDTO votesDTO2 = new VotesDTO();
        assertThat(votesDTO1).isNotEqualTo(votesDTO2);
        votesDTO2.setId(votesDTO1.getId());
        assertThat(votesDTO1).isEqualTo(votesDTO2);
        votesDTO2.setId(2L);
        assertThat(votesDTO1).isNotEqualTo(votesDTO2);
        votesDTO1.setId(null);
        assertThat(votesDTO1).isNotEqualTo(votesDTO2);
    }
}
