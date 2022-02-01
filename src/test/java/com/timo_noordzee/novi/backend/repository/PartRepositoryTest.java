package com.timo_noordzee.novi.backend.repository;

import com.timo_noordzee.novi.backend.config.JpaConfiguration;
import com.timo_noordzee.novi.backend.data.PartEntity;
import com.timo_noordzee.novi.backend.util.PartTestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfiguration.class)
public class PartRepositoryTest {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private PartRepository partRepository;

    private final PartTestUtils partTestUtils = new PartTestUtils();

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(partRepository).isNotNull();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void decrementStockWorks() {
        final int stock = partTestUtils.faker.number().numberBetween(10, 20);
        final int amount = partTestUtils.faker.number().numberBetween(1, 10);
        final PartEntity partEntity = partTestUtils.generateMockEntity(stock);
        partRepository.save(partEntity);

        partRepository.decrementStock(partEntity.getId(), amount);
        final PartEntity updatedPartEntity = partRepository.findById(partEntity.getId()).orElse(null);

        assertThat(updatedPartEntity).isNotNull();
        assertThat(updatedPartEntity.getStock()).isEqualTo(stock - amount);
    }

}