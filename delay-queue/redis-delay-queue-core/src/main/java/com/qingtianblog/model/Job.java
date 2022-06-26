package com.qingtianblog.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author Guank
 * @version 1.0
 * @description: 延时任务消息结构
 * @date 2022/6/12 8:41
 */
@Data
public class Job implements Serializable {


    private static final long serialVersionUID = -7011983763974298797L;
    /**
     * Job的唯一标识。用来检索和删除指定的Job信息
     */
    @NotBlank
    private String jobId;


    /**
     * Job类型。可以理解成具体的业务名称
     */
    @NotBlank
    private String topic;

    /**
     * Job需要延迟的时间。单位：秒。（服务端会将其转换为绝对时间）
     */
    private Long delay;

    /**
     * Job的内容，供消费者做具体的业务处理，以json格式存储
     */
    @NotBlank
    private String body;

    /**
     * 失败重试次数
     */
    private int retry = 0;

    /**
     * 通知URL
     */
    @NotBlank
    private String url;
}
