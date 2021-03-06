package com.szmirren.vxApi.cluster.impl;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import com.szmirren.vxApi.cluster.VxApiCluster;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * 用于集群时获取网关所需要配置信息
 * 
 * @author <a href="http://szmirren.com">Mirren</a>
 *
 */
public class VxApiClusterZookeeperImpl implements VxApiCluster {
	private JsonObject zkConf = new JsonObject();// 配置信息
	private Vertx vertx;

	/**
	 * 初始化一个Zookeeper的配置文件
	 * 
	 * @param zkConf
	 */
	public VxApiClusterZookeeperImpl(JsonObject zkConf, Vertx vertx) {
		this.zkConf = zkConf;
		this.vertx = vertx;
	}

	@Override
	public void getConfig(Handler<AsyncResult<JsonObject>> event) {
		vertx.<JsonObject>executeBlocking(get -> {
			JsonObject retry = zkConf.getJsonObject("retry", new JsonObject());
			String hosts = zkConf.getString("zookeeperHosts", "127.0.0.1");
			int baseSleepTimeMs = retry.getInteger("initialSleepTime", 1000);
			int maxRetries = retry.getInteger("maxTimes", 5);
			int maxSleepMs = retry.getInteger("intervalTimes", 10000);
			int sessionTimeoutMs = zkConf.getInteger("sessionTimeout", 20000);
			int connectionTimeoutMs = zkConf.getInteger("connectTimeout", 3000);
			ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries, maxSleepMs);
			String vxApiConfPath = zkConf.getString("vxApiConfPath", "/io.vertx/vx.api.gateway/conf");
			CuratorFramework client = CuratorFrameworkFactory.newClient(hosts, sessionTimeoutMs, connectionTimeoutMs, retryPolicy);
			try {
				client.start();
				byte[] data = client.getData().forPath(vxApiConfPath);
				String jsons = new String(data);
				get.complete(new JsonObject(jsons));
			} catch (Exception e) {
				get.fail(e);
			} finally {
				client.close();
			}
		}, event);
	}

	@Override
	public void putApplication(JsonObject app, Handler<AsyncResult<Integer>> event) {
		event.handle(Future.failedFuture("该方法会在后续版本实现"));
	}
	@Override
	public void getApplication(Handler<AsyncResult<JsonArray>> event) {
		event.handle(Future.failedFuture("该方法会在后续版本实现"));
	}
	@Override
	public void putAPI(String appName, JsonObject api, Handler<AsyncResult<JsonArray>> event) {
		event.handle(Future.failedFuture("该方法会在后续版本实现"));
	}
	@Override
	public void getAPIS(String appName, Handler<AsyncResult<JsonArray>> event) {
		event.handle(Future.failedFuture("该方法会在后续版本实现"));
	}

}
