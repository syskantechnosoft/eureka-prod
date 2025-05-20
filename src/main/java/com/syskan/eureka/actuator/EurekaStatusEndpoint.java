package com.syskan.eureka.actuator;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

import com.netflix.eureka.EurekaServerContext;
import com.netflix.eureka.EurekaServerContextHolder;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Endpoint(id = "eureka-status")
@RequiredArgsConstructor
@Slf4j
public class EurekaStatusEndpoint {

	@ReadOperation
	public Map<String, Object> getEurekaStatus() {
		Map<String, Object> status = new HashMap<>();

		try {
			EurekaServerContext serverContext = EurekaServerContextHolder.getInstance().getServerContext();
			PeerAwareInstanceRegistry registry = serverContext.getRegistry();

//			status.put("environment", serverContext.getEnvironment());
//			status.put("isBelowRenewThreshold", registry.isBelowRenewThreshold() ? "true" : "false");
//			status.put("generalStats", registry.getStatusInfo().getGeneralStats());
//			status.put("instanceStats", registry.getStatusInfo().getInstanceInfo());
//			status.put("numberOfRenewsPerMinThreshold", registry.getNumberOfRenewsPerMinThreshold());
			status.put("numberOfRenewsInLastMin", registry.getNumOfRenewsInLastMin());
			status.put("selfPreservationMode", registry.isSelfPreservationModeEnabled() ? "enabled" : "disabled");
//			status.put("uptime", registry.getUptime());
		} catch (Exception e) {
			log.error("Error retrieving Eureka status", e);
			status.put("error", e.getMessage());
		}

		return status;
	}

//	 @GetMapping("/actuator/eureka-status")
//	    public Map<String, Object> getEurekaStatus() {
//	        EurekaServerContext serverContext = EurekaServerContextHolder.getInstance().getServerContext();
//	        PeerAwareInstanceRegistry registry = serverContext.getRegistry();
//
//	        Map<String, Object> status = new LinkedHashMap<>();
//	        status.put("environment", serverContext.getEnvironment());
//	        status.put("isBelowRenewThreshold", registry.isBelowRenewThreshold());
//	        status.put("generalStats", registry.getStatusInfo().getGeneralStats());
//	        status.put("instanceStats", registry.getStatusInfo().getInstanceInfo());
//	        status.put("numberOfRenewsPerMinThreshold", registry.getNumberOfRenewsPerMinThreshold());
//	        status.put("numberOfRenewsInLastMin", registry.getNumOfRenewsInLastMin());
//	        status.put("selfPreservationMode", registry.isSelfPreservationModeEnabled());
//	        status.put("uptime", registry.getUptime());
//
//	        return status;
//	    }
	@ReadOperation
	public Map<String, Object> getEurekaApplications(@Selector String application) {
		Map<String, Object> result = new HashMap<>();

		try {
			EurekaServerContext serverContext = EurekaServerContextHolder.getInstance().getServerContext();
			PeerAwareInstanceRegistry registry = serverContext.getRegistry();

			var app = registry.getApplication(application.toUpperCase());
			if (app != null) {
				result.put("name", app.getName());
				result.put("instancesCount", app.getInstances().size());
				result.put("instances", app.getInstances());
			} else {
				result.put("error", "Application not found");
			}
		} catch (Exception e) {
			log.error("Error retrieving application details", e);
			result.put("error", e.getMessage());
		}

		return result;
	}

	@WriteOperation
	public Map<String, String> toggleSelfPreservation() {
		Map<String, String> result = new HashMap<>();

		try {
			EurekaServerContext serverContext = EurekaServerContextHolder.getInstance().getServerContext();
			PeerAwareInstanceRegistry registry = serverContext.getRegistry();

			boolean currentMode = registry.isSelfPreservationModeEnabled();
//			registry.toggleSelfPreservation(!currentMode);

			result.put("status", "success");
			result.put("selfPreservationMode", registry.isSelfPreservationModeEnabled() ? "enabled" : "disabled");
		} catch (Exception e) {
			log.error("Error toggling self preservation mode", e);
			result.put("status", "error");
			result.put("message", e.getMessage());
		}

		return result;
	}
}
