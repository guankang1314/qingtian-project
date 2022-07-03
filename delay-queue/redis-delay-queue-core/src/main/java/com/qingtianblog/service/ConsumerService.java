package com.qingtianblog.service;

/**
 * @author Guank
 * @version 1.0
 * @description: 消费者类 对在队列中的任务进行消费
 * @date 2022/7/3 15:00
 */
public interface ConsumerService {

    /**
     * 消费 Queue 中的消息
     * @param url 请求的 url
     * @param body 消息内容
     * @return 返回结果
     */
    Boolean consumeMessage(String url, String body);
}
