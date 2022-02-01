package com.timo_noordzee.novi.backend.config;

import com.cosium.spring.data.jpa.entity.graph.repository.support.EntityGraphJpaRepositoryFactoryBean;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        repositoryFactoryBeanClass = EntityGraphJpaRepositoryFactoryBean.class,
        basePackages = "com.timo_noordzee.novi.backend.repository"
)
@RequiredArgsConstructor
public class JpaConfiguration {

    private final Environment env;

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(env.getProperty("JDBC_URL"));
        dataSource.setUsername(env.getProperty("JDBC_USER"));
        dataSource.setPassword(env.getProperty("JDBC_PASS"));

        return dataSource;
    }

}
