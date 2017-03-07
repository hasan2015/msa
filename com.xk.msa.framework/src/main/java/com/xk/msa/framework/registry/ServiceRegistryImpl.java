/**
 * 
 */
package com.xk.msa.framework.registry;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author yanhaixun 2017年1月8日
 */
@Component
public class ServiceRegistryImpl implements ServiceRegistry, Watcher {
	private static Logger logger = LoggerFactory.getLogger(ServiceRegistryImpl.class);
	private static CountDownLatch latch = new CountDownLatch(1);
	private ZooKeeper zk;
//	private static final String CONNECTION_STRING = "127.0.0.1:2181";
	private static final String REGISTRY_PATH = "/registry";
	private static final int SESSION_TIMEOUT = 5000;

	public ServiceRegistryImpl() {

	}

	public ServiceRegistryImpl(String zkServers) {
		try {
			zk = new ZooKeeper(zkServers, SESSION_TIMEOUT, this);
			latch.await();
			logger.info("connected to zookeeper");
		} catch (Exception e) {
			logger.error("create zookeeper client failure", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * demo.msa.framework.registry.ServiceRegistry#Register(java.lang.String,
	 * java.lang.String)
	 */
	public void register(String serviceName, String serviceAddress) {
		try {
			// 创建根节点
			String registryPath = REGISTRY_PATH;
			if (zk.exists(registryPath, false) == null) {
				zk.create(registryPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				logger.info("create registry node:{}", registryPath);

			}
			// 创建服务节点
			String servicePath = registryPath + "/" + serviceName;
			if (zk.exists(servicePath, false) == null) {
				zk.create(servicePath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				logger.info("create service node:{}", servicePath);
			}
			// 创建地址节点
			String addressPath = servicePath + "/address-";
			String addressNode = zk.create(addressPath, serviceAddress.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
					CreateMode.EPHEMERAL_SEQUENTIAL);
			logger.info("create address node:{}=>{}", addressNode, serviceAddress);

		} catch (Exception e) {
			logger.error("create node failure", e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.zookeeper.Watcher#process(org.apache.zookeeper.WatchedEvent)
	 */
	public void process(WatchedEvent event) {
		if(event.getState()==Event.KeeperState.SyncConnected){
			latch.countDown();
		}

	}

}
