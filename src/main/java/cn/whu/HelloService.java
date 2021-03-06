package cn.whu;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Author WangYong
 * @Date 2020/06/29
 * @Time 17:22
 */
@RestController
@RequestMapping("/")
public class HelloService {

    @Value("${cn.whu.zk-demo.service-id}")
    private String serviceId;

    /*
     * access: http://127.0.0.1:8080/hello
     * output: say hello from 192.168.8.104
     */
    @GetMapping("hello")
    public Object sayHello() throws UnknownHostException {
        return "say hello from " + getMyIP() + ", and serviceId = " + serviceId;
    }

    private String getMyIP() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }
}
