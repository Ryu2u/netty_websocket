package com.ryu2u.obj;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 返回信息
 * @author Administrator
 * @Description:
 * @date 2023/5/10 10:27
 */
@Data
public class ResultMessage implements Serializable {
    private static final long serialVersionUID = -6676725356813852977L;

    /**
     * 返回类型:
     * err: 错误信息
     * success: 成功信息
     */
    private String type;
    /**
     * 返回的信息内容
     */
    private Object message;
    /**
     * 发送时间
     */
    private String backDate;

    public static String createErrMessageString(String message){
        ResultMessage err = new ResultMessage();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        err.setType("err");
        err.setBackDate(time);
        err.setMessage(message);
        return JSON.toJSONString(err);
    }
    public static String createSuccessMessageString(String message){
        ResultMessage success = new ResultMessage();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        success.setType("success");
        success.setBackDate(time);
        success.setMessage(message);
        return JSON.toJSONString(success);
    }

}
