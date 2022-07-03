package com.qingtianblog.service.impl;

import com.alibaba.fastjson.JSON;
import com.qingtianblog.constant.RedisQueueKey;
import com.qingtianblog.service.ConsumerService;
import com.qingtianblog.util.RestTemplateUtils;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

/**
 * @author Guank
 * @version 1.0
 * @description: 消费者实现类 对 Queue 中的消息进行消费
 * @date 2022/7/3 15:03
 */
@Service
@Slf4j
public class ConsumerServiceImpl implements ConsumerService {

    @Autowired
    private RestTemplateUtils restTemplateUtils;

    /**
     * 消费 Queue 中的消息
     * @param url 请求的 url
     * @param body 消息内容
     * @return 返回结果
     */
    @Override
    public Boolean consumeMessage(String url, String body) {
        log.info("consume Message url is : [{}], params is : [{}]",url, body);
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put(CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
            ResponseEntity<String> responseEntity = restTemplateUtils.post(url, headers, body, String.class);
            log.info("consume Message result is : [{}]", responseEntity.getBody());
            HttpStatus statusCode = responseEntity.getStatusCode();
            String response = responseEntity.getBody();
            return statusCode.is2xxSuccessful() && StringUtils.equals(response, RedisQueueKey.SUCCESS);
        }catch (Exception e) {
            log.error("consume Message error ", e);
        }
        return Boolean.FALSE;
    }
}
