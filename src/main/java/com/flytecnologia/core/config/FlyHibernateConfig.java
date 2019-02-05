package com.flytecnologia.core.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.flytecnologia.core.hibernate.multitenancy.FlyMultiTenantConnectionProviderImpl;
import com.flytecnologia.core.hibernate.multitenancy.FlyTenantIdentifierResolver;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class FlyHibernateConfig {
    private JpaProperties jpaProperties;

    public FlyHibernateConfig(JpaProperties jpaProperties) {
        this.jpaProperties = jpaProperties;
    }

    @Bean
    public Module datatypeHibernateModule() {
        //resolve problem of lazy inicialization :)
        final Hibernate5Module module = new Hibernate5Module();
        //get only id of lazy objects
        module.disable(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION);

        module.configure(Hibernate5Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS, true);
        return module;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    /**
     * @Bean public DataSource dataSource() {
     * HikariConfig config = new HikariConfig();
     * config.setDriverClassName(driver);
     * config.setJdbcUrl(url);
     * config.setUsername(username);
     * config.setPassword(password);
     * config.addDataSourceProperty("cachePrepStmts", "true");
     * config.addDataSourceProperty("prepStmtCacheSize", "250");
     * config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
     * config.addDataSourceProperty("useServerPrepStmts", "true");
     * <p>
     * return new HikariDataSource(config);
     * }
     */

    @Bean
    public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
        return new FlyTenantIdentifierResolver();
    }

    @Bean
    public MultiTenantConnectionProvider getFlyMultiTenantConnectionProviderImpl() {
        return new FlyMultiTenantConnectionProviderImpl();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
                                                                       MultiTenantConnectionProvider multiTenantConnectionProvider,
                                                                       CurrentTenantIdentifierResolver currentTenantIdentifierResolver) {
        final Properties properties = new Properties();
        properties.putAll(jpaProperties.getProperties());
        properties.put(Environment.MULTI_TENANT, MultiTenancyStrategy.SCHEMA);
        properties.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
        properties.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolver);

        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.flytecnologia", "br.com");
        em.setJpaVendorAdapter(jpaVendorAdapter());
        em.setJpaProperties(properties);

        return em;
    }

    @Bean
    public JpaProperties jpaProperties() {
        return new JpaProperties();
    }

    @Bean
    public MultiTenantConnectionProvider multiTenantConnectionProvider() {
        return new FlyMultiTenantConnectionProviderImpl();
    }

    @Bean
    public CurrentTenantIdentifierResolver currentTenantIdentifierResolver() {
        return new FlyTenantIdentifierResolver();
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);

        return transactionManager;
    }
}
