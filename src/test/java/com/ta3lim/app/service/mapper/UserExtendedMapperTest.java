package com.ta3lim.app.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserExtendedMapperTest {

    private UserExtendedMapper userExtendedMapper;

    @BeforeEach
    public void setUp() {
        userExtendedMapper = new UserExtendedMapperImpl();
    }
}
