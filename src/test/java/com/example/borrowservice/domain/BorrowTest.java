package com.example.borrowservice.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.borrowservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BorrowTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Borrow.class);
        Borrow borrow1 = new Borrow();
        borrow1.setId(1L);
        Borrow borrow2 = new Borrow();
        borrow2.setId(borrow1.getId());
        assertThat(borrow1).isEqualTo(borrow2);
        borrow2.setId(2L);
        assertThat(borrow1).isNotEqualTo(borrow2);
        borrow1.setId(null);
        assertThat(borrow1).isNotEqualTo(borrow2);
    }
}
