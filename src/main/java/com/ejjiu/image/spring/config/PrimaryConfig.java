package com.ejjiu.image.spring.config;

import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder.Builder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactoryPrimary", transactionManagerRef = "transactionManagerPrimary", basePackages = {
        "com.ejjiu.image.jpa"}) //设置Repository所在位置
public class PrimaryConfig {

    @Autowired
    private Environment environment;
    @Autowired(required = false)
    PhysicalNamingStrategyStandardImpl physicalNamingStrategy;

    private DataSource primaryDataSource;
    @PostConstruct
    public void init(){

        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create()
                .driverClassName(environment.getProperty("spring.datasource.driver-class-name"))
                .password("")
                .username("")
                .url(environment.getProperty("spring.datasource.url"));

        this.primaryDataSource =  dataSourceBuilder.build();;
    }
    private static final String entryPackage = "com.ejjiu.image.jpa.table";


    @Bean(name = "entityManagerPrimary")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {


        return entityManagerFactoryPrimary(builder).getObject().createEntityManager();
    }


    @Bean(name = "entityManagerFactoryPrimary")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryPrimary(EntityManagerFactoryBuilder builder) {
        Builder builder1 = builder.dataSource(primaryDataSource);
        Builder properties = builder1.properties(getVendorProperties());

        return properties.packages(entryPackage) //设置实体类所在位置
                .persistenceUnit("primaryPersistenceUnit").build();
    }


    @Primary
    @Bean(name = "sessionManagerPrimary")
    public LocalSessionFactoryBean sessionFactory() throws Exception {
        Properties properties = new Properties();

        // Hibernate Properties
        properties.put("hibernate.dialect",
                Objects.requireNonNull(environment.getProperty("spring.jpa.properties.hibernate.dialect")));
        properties.put("hibernate.show_sql", Objects.requireNonNull(environment.getProperty("spring.jpa.show-sql")));
        properties.put("hibernate.hbm2ddl.auto",
                Objects.requireNonNull(environment.getProperty("spring.jpa.hibernate.ddl-auto")));
        properties.put("hibernate.id.new_generator_mappings", false);
        properties.put("hibernate.search.default.directory_provider", "filesystem");
//        properties.put("hibernate.search.default.indexBase", environment.getProperty("spring.datasource.primary.indexBasePath"));
        properties.put("hibernate.search.default.indexwriter.infostream", true);

        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setPackagesToScan(entryPackage);
        sessionFactory.setDataSource(primaryDataSource);
        sessionFactory.setHibernateProperties(properties);
        sessionFactory.setImplicitNamingStrategy(ImplicitNamingStrategyJpaCompliantImpl.INSTANCE);
        sessionFactory.setPhysicalNamingStrategy(new SnakeCasePhysicalNamingStrategy());

        sessionFactory.afterPropertiesSet();

        return sessionFactory;
    }

    @Autowired(required = false)
    private JpaProperties jpaProperties;
    @Autowired
    private HibernateProperties hibernateProperties;


    private Map<String, Object> getVendorProperties() {
        Map<String, Object> map = hibernateProperties
                .determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings());
        return map;
    }

    @Primary
    @Bean(name = "transactionManagerPrimary")
    public PlatformTransactionManager transactionManagerPrimary(EntityManagerFactoryBuilder builder) {

        return new JpaTransactionManager(entityManagerFactoryPrimary(builder).getObject());
    }

}
