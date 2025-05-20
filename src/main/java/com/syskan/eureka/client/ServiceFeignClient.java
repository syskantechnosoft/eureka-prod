package com.syskan.eureka.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Example Feign client for interfacing with microservices registered in Eureka
 * This would be used by services that need to communicate with each other.
 */
@FeignClient(name = "example-service")
public interface ServiceFeignClient {

	@GetMapping("/api/resources")
	List<ResourceDto> getAllResources();

	@GetMapping("/api/resources/{id}")
	ResourceDto getResourceById(@PathVariable("id") Long id);
}

/**
 * Example DTO class for the Feign client
 */
class ResourceDto {
	private Long id;
	private String name;
	private String description;

	// Getters and setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
