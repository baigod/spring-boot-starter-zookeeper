package com.szzc.spring.boot.starter.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(ZookeeperProperties.class)
@Configuration
@ComponentScan(basePackageClasses = { ZookeeperUtils.class })
public class ZookeeperConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(ZookeeperConfiguration.class);
	@Autowired
	private ZookeeperProperties zookeeperProperties;

	@Bean(initMethod = "start", destroyMethod = "close")
	public CuratorFramework curatorFramework() {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 30);
		CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperProperties.getServers(), retryPolicy);
		logger.debug("zookeeper client started . server address is : {}", zookeeperProperties.getServers());
		return client;
	}

}
