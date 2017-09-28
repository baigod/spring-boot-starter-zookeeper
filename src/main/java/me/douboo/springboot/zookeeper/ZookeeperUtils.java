package me.douboo.springboot.zookeeper;

import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import me.douboo.springboot.zookeeper.exception.LockBusyException;

@Component
public class ZookeeperUtils {

	private static final Logger logger = LoggerFactory.getLogger(ZookeeperUtils.class);

	@Autowired
	private CuratorFramework client;

	/**
	 * 申请锁，3秒申请不到则为锁存在,报异常
	 * 
	 * @param callback
	 * @return
	 * @throws Exception
	 */
	public <T> T lock(ZookeeperCallback<T> callback) throws Exception {
		return lock(callback, 3L, TimeUnit.SECONDS);
	}

	/**
	 * 申请锁，指定请求等待时间
	 * 
	 * @param callback
	 * @param time
	 *            时间，例如：3
	 * @param unit
	 *            时间单位，例如{@link TimeUnit.SECONDS}
	 * @return
	 * @throws Exception
	 */
	public <T> T lock(ZookeeperCallback<T> callback, long time, TimeUnit unit) throws Exception {
		InterProcessMutex lock = new InterProcessMutex(client, callback.getLockPath());

		// 请求锁，等待3秒
		if (lock.acquire(time, unit)) {
			try {
				return callback.callback();
			} finally {
				lock.release();
			}
		} else {
			throw new LockBusyException("系统繁忙，请稍后重试!", "-100102");
		}

	}

	private static String hostname = "UNKNOW HOSTNAME";

	@Value("${spring.application.name:unknow-application}")
	private String applicationName;

	static {
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取当前应用客户端节点数
	 * 
	 * @param zkClient
	 * @return
	 */
	public int getClientNum() {
		try {
			final String path = createClient();
			List<String> list = client.getChildren().watched().forPath(path);
			if (!CollectionUtils.isEmpty(list)) {
				logger.debug("获取节点总数:{}", list.size());
				return list.size();
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return 0;
	}

	/**
	 * 获取当前路径下的所有节点
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public List<String> listNodesByPath(String path) throws Exception {
		try {
			List<String> list = client.getChildren().watched().forPath(path);
			if (!CollectionUtils.isEmpty(list)) {
				logger.debug("{}下共有{}个节点", path, list.size());
				return list;
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
		return null;
	}

	/**
	 * 获取当前路径下的节点数
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public int countNodeByPath(String path) throws Exception {
		List<String> list = this.listNodesByPath(path);
		return CollectionUtils.isEmpty(list) ? 0 : list.size();
	}

	/**
	 * 创建节点
	 * 
	 * @param path
	 * @param node
	 * @return
	 * @throws Exception
	 */
	public String createNode(String path, String node) throws Exception {
		String pn = path + "/" + node;
		synchronized (pn.intern()) {
			Stat stat = client.checkExists().forPath(pn);
			if (stat == null) {
				String forPath = client.create()// 创建一个路径
						.creatingParentsIfNeeded()// 如果指定的节点的父节点不存在，递归创建父节点
						.withMode(CreateMode.EPHEMERAL)// 存储类型（临时的还是持久的）
						.withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)// 访问权限
						.forPath(pn);// 创建的路径
				logger.debug("创建临时子节点:{} .(如果根节点不存在，自动创建根节点)", forPath);
			}
		}
		return path;
	}

	// 创建当前应用客户端节点
	private String createClient() throws Exception {
		final String path = "/" + applicationName + "/client";
		final String node = hostname;
		return this.createNode(path, node);
	}

	/**
	 * 获取客户端索引
	 * 
	 * @author luheng
	 * @param zkClient
	 * @param hostName
	 * @return
	 */
	public int getClientIndex() {
		try {
			final String path = createClient();
			List<String> list = client.getChildren().watched().forPath(path);
			if (!CollectionUtils.isEmpty(list)) {
				int index = list.indexOf(hostname);
				logger.debug("{}正在执行定时任务;当前集群节点:{};数量:{};本机序号：{}", hostname, list, list.size(), index);
				return index;
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
		return -1;
	}

	/**
	 * 节点是否存在
	 * 
	 * @param path
	 * @param node
	 * @return
	 */
	public boolean exists(String path, String node) {
		try {
			List<String> list = client.getChildren().watched().forPath(path);
			return !CollectionUtils.isEmpty(list) && list.contains(node);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
		return false;
	}

}
