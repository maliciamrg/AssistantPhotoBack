package com.malicia.mrg.assistant.photo;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

@SpringBootApplication
public class AssistantPhotoApplication {
    public static final String HTTP_DEFAULT_PORT = "8080";
    private static final Logger logger = LoggerFactory.getLogger(AssistantPhotoApplication.class);

    public static void main(String[] args) throws UnknownHostException {

        final Environment env = SpringApplication.run(AssistantPhotoApplication.class, args).getEnvironment();

        logApplicationStartup(env);

        logger.trace("---==[ trace  ]==---");
        logger.debug("---==[ debug ]==---");
        logger.info("---==[  info   ]==---");
        logger.warn("---==[  warn   ]==---");
        logger.error("---==[ error  ]==---");
        logger.info("Start.................");
    }

    private static void logApplicationStartup(Environment env) throws UnknownHostException {
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        final String serverPort = Optional.ofNullable(env.getProperty("server.port")).orElse(HTTP_DEFAULT_PORT);
        String contextPath = env.getProperty("server.servlet.context-path");
        if (StringUtils.isEmpty(contextPath)) {
            contextPath = "/";
        }
        final String hostAddress = InetAddress.getLocalHost().getHostAddress();
        final String ipOutsideDocker = env.getProperty("application.ipWan");
        String appName = env.getProperty("spring.application.name");
        String appVersion = env.getProperty("application.version");
        logger.info("""
                ---------------------------------------------------------------
                Application '{}' ({})' is running!
                ---------------------------------------------------------------
                Profile(s):  {}
                ---------------------------------------------------------------
                Access URLs:
                  Local:        {}://localhost:{}{}
                  Swagger:      {}://localhost:{}{}swagger-ui/index.html
                ---------------------------------------------------------------
                External:
                  Local:        {}://{}:{}{}
                  Swagger:      {}://{}:{}{}swagger-ui/index.html
                ---------------------------------------------------------------
                IpWan:
                  Local:        {}://{}:{}{}
                  Swagger:      {}://{}:{}{}swagger-ui/index.html
                """,
                appName, appVersion,
                env.getActiveProfiles(),
                protocol, serverPort, contextPath,
                protocol, serverPort, contextPath,
                protocol, hostAddress, serverPort, contextPath,
                protocol, hostAddress, serverPort, contextPath,
                protocol, ipOutsideDocker, serverPort, contextPath,
                protocol, ipOutsideDocker, serverPort, contextPath
                );
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propsConfig
                = new PropertySourcesPlaceholderConfigurer();
        propsConfig.setLocation(new ClassPathResource("git.properties"));
        propsConfig.setIgnoreResourceNotFound(true);
        propsConfig.setIgnoreUnresolvablePlaceholders(true);
        return propsConfig;
    }
}
