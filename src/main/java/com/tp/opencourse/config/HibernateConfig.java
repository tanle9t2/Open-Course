package com.tp.opencourse.config;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;

import java.util.Properties;

import static org.hibernate.cfg.JdbcSettings.DIALECT;
import static org.hibernate.cfg.JdbcSettings.SHOW_SQL;

@Configuration
@RequiredArgsConstructor
public class HibernateConfig {
    private final Dotenv dotenv;

    @Bean
    public LocalSessionFactoryBean getSessionFactory() {
        LocalSessionFactoryBean sessionFactory
                = new LocalSessionFactoryBean();
        sessionFactory.setPackagesToScan(new String[]{
                "com.tp.opencourse.entity"
        });
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource
                = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(
                dotenv.get("hibernate.connection.url"));
        dataSource.setUsername(
                dotenv.get("hibernate.connection.username"));
        dataSource.setPassword(
                dotenv.get("hibernate.connection.password"));

        return dataSource;
    }

    private Properties hibernateProperties() {
        Properties props = new Properties();
        props.put(DIALECT,dotenv.get("hibernate.dialect"));
        props.put(SHOW_SQL, dotenv.get("hibernate.showSql"));
//        props.put("hibernate.transaction.coordinator_class", "jdbc");
        return props;
    }

    @Bean
    public HibernateTransactionManager transactionManager() {
        HibernateTransactionManager transactionManager
                = new HibernateTransactionManager();
        transactionManager.setSessionFactory(
                getSessionFactory().getObject());
        return transactionManager;
    }

}
