package com.syskan.eureka.config;

import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeyConfiguration;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeyProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"chaos-monkey"})
@Import(ChaosMonkeyConfiguration.class)
@EnableConfigurationProperties({ChaosMonkeyProperties.class, AssaultProperties.class, WatcherProperties.class})
public class ChaosMonkeyConfig {

    @Bean
    public ChaosMonkeyProperties chaosMonkeyProperties() {
        ChaosMonkeyProperties properties = new ChaosMonkeyProperties();
        properties.setEnabled(true);
        return properties;
    }

    @Bean
    public AssaultProperties assaultProperties() {
        AssaultProperties properties = new AssaultProperties();
        properties.setLevel(3);
        properties.setLatencyActive(true);
        properties.setLatencyRangeStart(1000);
        properties.setLatencyRangeEnd(3000);
        properties.setExceptionsActive(false);
        properties.setKillApplicationActive(false);
        properties.setMemoryActive(false);
        return properties;
    }

    @Bean
    public WatcherProperties watcherProperties() {
        WatcherProperties properties = new WatcherProperties();
        properties.setController(true);
        properties.setRestController(true);
        properties.setService(true);
        properties.setRepository(false);
        properties.setComponent(false);
        return properties;
    }
}
