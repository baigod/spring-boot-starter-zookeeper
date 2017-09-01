package com.szzc.spring.boot.starter.zookeeper;

public interface ZookeeperCallback<T> {

	public T callback() throws Exception;

	public String getLockPath();
}
