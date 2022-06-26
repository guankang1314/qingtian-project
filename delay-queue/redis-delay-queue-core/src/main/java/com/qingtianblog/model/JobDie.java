package com.qingtianblog.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author Guank
 * @version 1.0
 * @description: 消息结构
 * @date 2022/6/12 8:47
 */
@Data
public class JobDie implements Serializable {

    private static final long serialVersionUID = 3124842176268778318L;
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
}
