package com.syskan.eureka.config;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class MonitoringConfig {

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
//        return registry -> ((Object) registry.config()
//                .commonTags("application", "eureka-server"))
//                .bindToGlobalRegistry(true);
    	 return registry -> registry.config().commonTags("application", "eureka-server");
    }

    @Bean
    public List<MeterBinder> extraMetrics() {
        return Arrays.asList(
                new JvmMemoryMetrics(),
                new JvmGcMetrics(),
                new JvmThreadMetrics(),
                new ClassLoaderMetrics(),
                new ProcessorMetrics(),
                new FileDescriptorMetrics(),
                new UptimeMetrics()
        );
    }

    @Bean
    public Counter eurekaRegistrationsCounter(MeterRegistry registry) {
        return Counter.builder("eureka.registrations")
                .description("Number of service registrations")
                .register(registry);
    }

    @Bean
    public Counter eurekaRenewalsCounter(MeterRegistry registry) {
        return Counter.builder("eureka.renewals")
                .description("Number of service renewals")
                .register(registry);
    }

    @Bean
    public Counter eurekaCancellationsCounter(MeterRegistry registry) {
        return Counter.builder("eureka.cancellations")
                .description("Number of service cancellations")
                .register(registry);
    }

    @Bean
    public Gauge eurekaNumOfRenewsGauge(MeterRegistry registry) {
        return Gauge.builder("eureka.renews.last.minute", () -> {
            try {
                return com.netflix.eureka.EurekaServerContextHolder.getInstance()
                        .getServerContext().getRegistry().getNumOfRenewsInLastMin();
            } catch (Exception e) {
                log.error("Failed to get number of renewals", e);
                return 0;
            }
        })
                .description("Number of renewals in the last minute")
                .register(registry);
    }

    @Bean
    public Timer requestLatencyTimer(MeterRegistry registry) {
        return Timer.builder("http.server.requests.latency")
                .description("Request latency")
                .publishPercentileHistogram()
                .serviceLevelObjectives(
                        Duration.ofMillis(50),
                        Duration.ofMillis(100),
                        Duration.ofMillis(200),
                        Duration.ofMillis(500)
                )
                .register(registry);
    }

    @Bean
    public DistributionSummary responseSize(MeterRegistry registry) {
        return DistributionSummary.builder("http.server.response.size")
                .description("Response size in bytes")
                .baseUnit("bytes")
                .publishPercentileHistogram()
                .register(registry);
    }
}
