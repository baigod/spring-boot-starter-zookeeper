package me.douboo.springboot.zookeeper;

public interface ZookeeperCallback<T> {

	public T callback() throws Exception;

	public String getLockPath();
}
