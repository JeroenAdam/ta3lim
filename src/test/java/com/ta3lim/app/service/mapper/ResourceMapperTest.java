package com.ta3lim.app.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResourceMapperTest {

    private ResourceMapper resourceMapper;

    @BeforeEach
    public void setUp() {
        resourceMapper = new ResourceMapperImpl();
    }
}
