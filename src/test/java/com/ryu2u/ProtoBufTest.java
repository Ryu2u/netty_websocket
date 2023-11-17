package com.ryu2u;

import com.ryu2u.obj.ResponseOuterClass;
import message.Message;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author Administrator
 * @Description:
 * @date 2023/11/16 11:24
 */
public class ProtoBufTest {

    @Test
    public void test(){
        ResponseOuterClass.Response.Builder builder = ResponseOuterClass.Response.newBuilder();
        builder.setData("Test");
        builder.setCode(200);

        ResponseOuterClass.Response response = builder.build();

        byte[] byteArray = response.toByteArray();

        System.out.println(Arrays.toString(byteArray));

        try {
            ResponseOuterClass.Response newRes = ResponseOuterClass.Response.parseFrom(byteArray);
            System.out.println(newRes.getData());
            System.out.println(newRes.getCode());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testMessage(){

    }


}
