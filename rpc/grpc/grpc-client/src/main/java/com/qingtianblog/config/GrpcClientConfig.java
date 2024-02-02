package com.qingtianblog.config;

import com.qingtianblog.interceptor.GrpcClientInterceptor;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Order(Ordered.LOWEST_PRECEDENCE)
@Configuration
public class GrpcClientConfig {

    @GrpcGlobalClientInterceptor
    public GrpcClientInterceptor grpcClientInterceptor() {
        return new GrpcClientInterceptor();
    }
}
