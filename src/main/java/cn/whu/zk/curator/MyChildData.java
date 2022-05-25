package cn.whu.zk.curator;

import org.apache.curator.framework.recipes.cache.ChildData;

public class MyChildData {

    private final String path;
    private final byte[] data;

    public MyChildData(ChildData childData) {
        this.path = childData.getPath();
        this.data = childData.getData();
    }

    @Override
    public String toString() {
        return "ChildData{" +
                "path='" + path + '\'' +
                ", data=" + new String(data) +
                '}';
    }
}
