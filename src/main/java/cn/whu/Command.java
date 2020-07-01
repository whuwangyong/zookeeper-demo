package cn.whu;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author WangYong
 * @Date 2020/06/30
 * @Time 16:40
 */
@Component
public class Command implements InitializingBean {
    @Autowired
    private CuratorFramework client;
    @Value("${cn.whu.zk-demo.service-id}")
    private String serviceId;


    @Override
    public void afterPropertiesSet() throws Exception {
        init();
        registerSelf();
    }

    private void init() throws Exception {
        client.start();
        if (client.checkExists().forPath("/service/hello") == null) {
            client.create().creatingParentContainersIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath("/service/hello");
        }

    }

    private void registerSelf() throws Exception {
        client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .forPath("/service/hello/", serviceId.getBytes());
    }
}
