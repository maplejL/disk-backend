package com.cslg.disk.example.socket;

import com.alibaba.fastjson.JSON;
import com.cslg.disk.example.chat.dao.TempChatDao;
import com.cslg.disk.example.chat.entity.TempChat;
import com.cslg.disk.example.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint(value = "/websocket/{id}")
//此注解相当于设置访问URL
public class WebSocket {

    @Autowired
    private RedisService redisService;

    @Autowired
    private TempChatDao tempChatDao;

    private Session session;

    private static CopyOnWriteArraySet<WebSocket> webSockets = new CopyOnWriteArraySet<>();
    private static Map<String, Session> sessionPool = new HashMap<String, Session>();

    public static WebSocket webSocket;

    @PostConstruct
    public void init() {
        webSocket = this;
        webSocket.tempChatDao = this.tempChatDao;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam(value = "id") String id) {
        if (sessionPool.keySet().contains(id)) {
            return;
        }
        this.session = session;
        webSockets.add(this);
        sessionPool.put(id, session);
        List<TempChat> tempChats = webSocket.tempChatDao.findTempChatsById(Integer.valueOf(id));
        if (tempChats.size()>0) {
            sendOneObject(id, "您有未读聊天!");
            Map<String, List<TempChat>> tempChatMap = new HashMap<>();
            tempChatMap.put("tempChat", tempChats);
            sendOneObject(id, tempChatMap);
        }
        System.out.println("【websocket消息】有新的连接，总数为:" + webSockets.size());
    }

    @OnClose
    public void onClose(@PathParam(value = "id") String id) {
        webSockets.remove(this);
        sessionPool.remove(id);
        System.out.println("【websocket消息】连接断开，总数为:" + webSockets.size());
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("【websocket消息】收到客户端消息:" + message);
    }

    // 此为广播消息
    public void sendAllMessage(String message) {
        for (WebSocket webSocket : webSockets) {
            System.out.println("【websocket消息】广播消息:" + message);
            try {
                webSocket.session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 此为单点消息
    public Integer sendOneMessage(String id, String message) {
        Session session = sessionPool.get(id);
        if (session != null) {
            try {
                session.getAsyncRemote().sendText(message);
                return 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    // 推送对象，转化json字符串
    public void sendOneObject(String id, Object o) {
        Session session = sessionPool.get(id);
        if (session != null) {
            try {
                String s = JSON.toJSONString(o);
                synchronized (session) {
                    session.getBasicRemote().sendText(s);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Integer isOnLine(String id) {
        Session session = sessionPool.get(id);
        if (session != null) {
            return 1;
        }
        return 0;
    }

    public void logout(String id) {
        this.sessionPool.remove(id);
    }
}