package cn.whu.zk.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;


/**
 * CuratorCacheListenerBuilder 这个类的注释，解释了各种Listener
 */

public class Main {
    public static void main(String[] args) {

        String PATH = "/test/nodes"; // Path must not end with / character
        try (CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", new ExponentialBackoffRetry(1000, 3))) {
            client.start();
            client.blockUntilConnected();
            Stat stat = client.checkExists().forPath(PATH);
            if (stat != null) {
                /**
                 * stat=41,44,1635994653811,1635994653874,1,2,0,0,5,2,43
                 */
                System.out.println("stat=" + stat);
                client.delete().deletingChildrenIfNeeded().forPath(PATH);
            }

            CuratorCache cache = CuratorCache.build(client, PATH/*, CuratorCache.Options.SINGLE_NODE_CACHE*/);


            cache.listenable().addListener(CuratorCacheListener.builder()
                    /**
                     * Node created: [ChildData{path='/test/nodes', data=sum=0}]
                     * Node created: [ChildData{path='/test/nodes/node_1', data=node=1,offset=100}]
                     * Node created: [ChildData{path='/test/nodes/node_2', data=node=2,offset=102}]
                     * Node changed. Old: [ChildData{path='/test/nodes', data=sum=0}] New: [ChildData{path='/test/nodes', data=sum=2}]
                     * Node changed. Old: [ChildData{path='/test/nodes/node_2', data=node=2,offset=102}] New: [ChildData{path='/test/nodes/node_2', data=node=2,offset=202}]
                     */
//                    .forCreates(node -> System.out.println(String.format("Node created: [%s]", new MyChildData(node))))
//                    .forChanges((oldNode, node) -> System.out.println(String.format("Node changed. Old: [%s] New: [%s]", new MyChildData(oldNode), new MyChildData(node))))
//                    .forDeletes(oldNode -> System.out.println(String.format("Node deleted. Old value: [%s]", new MyChildData(oldNode))))
//                    .forInitialized(() -> System.out.println("Cache initialized"))
                    /**
                     * >>> forAll >>>
                     * type:NODE_CREATED
                     * >>> forAll >>>
                     * type:NODE_CREATED
                     * >>> forAll >>>
                     * type:NODE_CREATED
                     * >>> forAll >>>
                     * type:NODE_CHANGED
                     * oldData:ChildData{path='/test/nodes', data=sum=0}
                     * data:ChildData{path='/test/nodes', data=sum=2}
                     * <<< forAll <<<
                     * >>> forAll >>>
                     * type:NODE_CHANGED
                     * oldData:ChildData{path='/test/nodes/node_2', data=node=2,offset=102}
                     * data:ChildData{path='/test/nodes/node_2', data=node=2,offset=202}
                     * <<< forAll <<<
                     */
//                    .forAll(new CuratorCacheListener() {
//                        @Override
//                        public void event(Type type, ChildData oldData, ChildData data) {
//                            System.out.println(">>> forAll >>>");
//                            System.out.println("type:" + type);
//                            System.out.println("oldData:" + new MyChildData(oldData));
//                            System.out.println("data:" + new MyChildData(data));
//                            System.out.println("<<< forAll <<<");
//                        }
//                    })
                    .build());

            // 监听 /test/nodes 下面的child
            cache.listenable().addListener(CuratorCacheListener.builder().forPathChildrenCache(PATH, client, new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                    System.out.println(">>> PathChildrenCacheListener >>>");
                    System.out.println("type:" + event.getType());
                    System.out.println("path:" + event.getData().getPath());
                    System.out.println("data:" + new String(event.getData().getData()));
                    System.out.println("now child:" + client.getChildren().forPath(PATH));
                    System.out.println("<<< PathChildrenCacheListener <<<");
                }
            }).build());


//            cache.listenable().addListener(CuratorCacheListener.builder().forTreeCache(client, new TreeCacheListener() {
//                @Override
//                public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
//                    System.out.println(">>> TreeCacheListener >>>");
//                    System.out.println("type:" + event.getType());
//                    System.out.println("old data:" + new MyChildData(event.getOldData()));
//                    System.out.println("new data:" + new MyChildData(event.getData()));
//                    System.out.println("<<< TreeCacheListener <<<");
//
//                }
//            }).build());


            // 监听 /test/nodes 这个 node本身
//            cache.listenable().addListener(CuratorCacheListener.builder().forNodeCache(new NodeCacheListener() {
//                @Override
//                public void nodeChanged() throws Exception {
//                    System.out.println(">>> NodeCacheListener >>>");
//                    System.out.println("now data:" + new String(client.getData().forPath(PATH)));
//                    System.out.println("<<< NodeCacheListener <<<");
//                }
//            }).build());


            cache.start();


//            client.getCuratorListenable().addListener(new CuratorListener() {
//                @Override
//                public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
//
//                }
//            });

            Thread.sleep(10);
            client.create().creatingParentsIfNeeded().forPath(PATH, "sum=0".getBytes());
            Thread.sleep(10);
            client.create().orSetData().creatingParentsIfNeeded().forPath(PATH + "/node_1", "node=1,offset=100".getBytes());
            Thread.sleep(10);
            client.create().orSetData().creatingParentsIfNeeded().forPath(PATH + "/node_2", "node=2,offset=102".getBytes());
            Thread.sleep(10);
            client.setData().forPath(PATH, "sum=2".getBytes());
            Thread.sleep(10);
            client.create().orSetData().creatingParentsIfNeeded().forPath(PATH + "/node_2", "node=2,offset=202".getBytes());
            Thread.sleep(10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
