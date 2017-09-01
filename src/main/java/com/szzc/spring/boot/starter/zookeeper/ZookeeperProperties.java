package com.szzc.spring.boot.starter.zookeeper;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zookeeper")
public class ZookeeperProperties {

	private String servers="127.0.0.1:2181";

	public String getServers() {
		return servers;
	}

	public void setServers(String servers) {
		this.servers = servers;
	}
}
