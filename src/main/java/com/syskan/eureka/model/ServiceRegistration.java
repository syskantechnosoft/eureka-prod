package com.syskan.eureka.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "service_registrations")
public class ServiceRegistration {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String serviceId;
    
    private String serviceName;
    
    private String instanceId;
    
    private String hostName;
    
    private String ipAddress;
    
    private Integer port;
    
    private Boolean secure;
    
    private String healthCheckUrl;
    
    private String homePageUrl;
    
    private String statusPageUrl;
    
    private LocalDateTime registrationTime;
    
    private LocalDateTime lastUpdateTime;
    
    private String status;
    
    private Long upTime;
    
    private String metadata;
}
