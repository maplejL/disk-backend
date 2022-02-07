package com.cslg.disk.example.test.controller;


import com.cslg.disk.example.log.SysLogAnno;
import com.cslg.disk.example.redis.RedisService;
import com.cslg.disk.example.socket.WebSocket;
import com.cslg.disk.example.test.service.TestService;
import com.qcloud.cos.model.ObjectListing;
import io.swagger.annotations.Api;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/test")
@Api(value = "Test", tags = "测试相关接口")
@Slf4j
public class TestController {
    @Autowired
    private TestService testService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private WebSocket webSocket;

    private String res;

    @SysLogAnno("测试")
    @GetMapping("/get")
    public String doGet(@RequestParam(value = "s")String s) {
        return longestNiceSubstring(s);
    }

    @GetMapping("/testRedis")
    public void testRedis() {
//        redisService.setValue("test", "测试");
        Object test = redisService.getValue("test");
        log.info(test.toString());
    }


    @RequestMapping("/sendAllWebSocket")
    public String test() {
        webSocket.sendAllMessage("清晨起来打开窗，心情美美哒~");
        return null;
    }

    @RequestMapping("/sendOneWebSocket")
    public String sendOneWebSocket() {
        webSocket.sendOneMessage("DPS007", "只要你乖给你买条gai！");
        return "websocket单人发送";
    }

    @GetMapping("/link")
    public void Link(HttpServletRequest request, HttpServletResponse response) {
        try {
            browse("https://www.baidu.com/");
//            response.sendRedirect("https://www.baidu.com/");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void browse(String url) throws Exception {
        //获取操作系统的名字
        String osName = System.getProperty("os.name", "");

        if (osName.startsWith("Mac OS")) {
            //苹果的打开方式
            Class fileMgr = Class.forName("com.apple.eio.FileManager");

            Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[]{String.class});

            openURL.invoke(null, new Object[]{url});
        } else if (osName.startsWith("Windows")) {
            //windows的打开方式。
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
        } else {
            // Unix or Linux的打开方式
            String[] browsers = {"firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"};
            String browser = null;
            for (int count = 0; count < browsers.length && browser == null; count++)
                //执行代码，在brower有值后跳出，
                //这里是如果进程创建成功了，==0是表示正常结束。
                if (Runtime.getRuntime().exec(new String[]{"which", browsers[count]}).waitFor() == 0)
                    browser = browsers[count];
            if (browser == null)
                throw new Exception("Could not find web browser");
            else
                //这个值在上面已经成功的得到了一个进程。
                Runtime.getRuntime().exec(new String[]{browser, url});

        }
    }

    public String longestNiceSubstring(String s) {
        char[] chars = s.toCharArray();
        Map<Character, Integer> map = new HashMap<>();
        int max = 0;
        int start = 0;
        int end = s.length();
        for (int i = 0; i < chars.length; i++) {
            if (!map.containsKey(chars[i])) {
                map.put(chars[i], 1);
            } else {
                Integer integer = map.get(chars[i]);
                integer++;
                map.put(chars[i], integer);
            }
        }
        List<Character> keys = map.keySet().stream().collect(Collectors.toList());
        Set<Character> notNice = new HashSet<>();
        for (Character key : keys) {
            if (key >= 65 && key <= 90) {
                char c = (char) (key + 32);
                if (!keys.contains(c)) {
                    notNice.add(key);
                }
            } else {
                char c = (char) (key - 32);
                if (!keys.contains(c)) {
                    notNice.add(key);
                }
            }
        }
        if (notNice.size() == 0) {
            res = s;
            return res;
        } else {
            List<Integer> pos = new ArrayList<>();
            for (Character character : notNice) {
                pos.add(s.indexOf(character));
            }
            pos = pos.stream().sorted().collect(Collectors.toList());
            for (int i = 0; i <= pos.size(); i++) {
                if (i == 0) {
                    start = 0;
                    end = pos.get(i);
                    max = end - start;
                } else if (i < pos.size()) {
                    if (pos.get(i) - pos.get(i - 1) - 1 > max) {
                        start = pos.get(i - 1);
                        end = pos.get(i);
                        max = pos.get(i) - pos.get(i - 1);
                    }
                } else {
                    if (s.length() - 1 - pos.get(i - 1) > max) {
                        start = pos.get(i - 1) + 1;
                        end = s.length();
                        max = end - start;
                    }
                }
            }
            longestNiceSubstring(s.substring(start, end));
            return res;
        }
    }
}
