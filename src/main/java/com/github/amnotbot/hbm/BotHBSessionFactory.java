package com.github.amnotbot.hbm;

import java.util.Properties;

import com.github.amnotbot.BotLogger;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class BotHBSessionFactory {

    private static SessionFactory sessionFactory = null;
    private static BotHBSessionFactory botHBSessionFactory = null;

    protected BotHBSessionFactory() {
        this.setUp();
    }

    protected void setUp() {

        Properties properties = new Properties();
        properties.setProperty("hibernate.connection.url", System.getenv("JDBC_DATABASE_URL"));
        properties.setProperty("hibernate.connection.username", System.getenv("JDBC_DATABASE_USERNAME"));
        properties.setProperty("hibernate.connection.password", System.getenv("JDBC_DATABASE_PASSWORD"));

        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml").addProperties(properties);;

        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                                                        .configure().applySettings(configuration.getProperties()).build();

        try {
            sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
	    } catch (final Exception e) {
		    // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            BotLogger.getDebugLogger().debug(e.getMessage());
		    StandardServiceRegistryBuilder.destroy( registry );
        }
    }

    public static SessionFactory getSessionFactory() {
        if (botHBSessionFactory == null) {
            botHBSessionFactory = new BotHBSessionFactory();
        }
        return sessionFactory;
    }
}
