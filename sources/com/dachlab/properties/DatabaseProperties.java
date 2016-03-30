package com.dachlab.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Database configuration bean. Get the parameters from
 * application-xxx.properties
 * 
 * @author dcharles
 */
@Component
@ConfigurationProperties(prefix = "database")
public class DatabaseProperties {
	private String host;
	private int port;
	private String keyspace;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getKeyspace() {
		return keyspace;
	}

	public void setKeyspace(String keyspace) {
		this.keyspace = keyspace;
	}

}
