package com.llmoe.print.pojo;

import lombok.Data;

/**
 * 接收消息实体
 *
 * @author llmoe
 * @date 2020/6/6 10:45
 */
@Data
public class PushMessage {

    private String username;

    private String message;
}
