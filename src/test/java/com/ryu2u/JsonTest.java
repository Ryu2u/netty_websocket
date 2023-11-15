package com.ryu2u;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ryu2u.obj.SingleChatMessage;
import com.ryu2u.obj.LoginMessage;
import com.ryu2u.obj.Message;
import org.junit.Test;

/**
 * @author Administrator
 * @Description:
 * @date 2023/5/9 17:30
 */
public class JsonTest {

    @Test
    public void testJson() {
        String json = "{\"id\":1,\"name\":\"Jack\"}";
//        User user = JSON.parseObject(json, User.class);
        SingleChatMessage singleChatMessage = JSON.parseObject(json, SingleChatMessage.class);
        System.out.println(singleChatMessage);
        System.out.println(singleChatMessage == null);
    }

    @Test
    public void testMessage(){
        String json = "{\n" +
                "    \"type\": 1 ,\n" +
                "    \"data\": {\n" +
                "        \"senderId\": \"12345\",\n" +
                "        \"receiverId\": \"67890\",\n" +
                "        \"content\": \"Hello, how are you?\"\n" +
                "    }\n" +
                "}";
        Message message = JSON.parseObject(json, Message.class);
        System.out.println(message.getType());
        JSONObject data = (JSONObject) message.getData();
        System.out.println(data);
        SingleChatMessage to = data.to(SingleChatMessage.class);
        System.out.println(to);

    }

    @Test
    public void testLoginMsg(){
        String json = "{\n" +
                "    \"type\": 2 ,\n" +
                "    }\n" +
                "}";
        Message message = JSON.parseObject(json, Message.class);
        System.out.println(message);
        JSONObject data = (JSONObject) message.getData();
        LoginMessage to = data.to(LoginMessage.class);
        System.out.println(to);

    }

    @Test
    public void testInstance(){
        Object obj = new Object();
        Message msg = new Message();
        System.out.println(obj instanceof Message);
        System.out.println(msg instanceof Object);
        System.out.println(msg instanceof Message);
    }

}
