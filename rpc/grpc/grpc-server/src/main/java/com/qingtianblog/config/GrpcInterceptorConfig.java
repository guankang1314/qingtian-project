package com.qingtianblog.config;

import com.qingtianblog.interceptor.GrpcServerInterceptor;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class GrpcInterceptorConfig {

    @GrpcGlobalServerInterceptor
    public GrpcServerInterceptor grpcServerInterceptor() {
        return new GrpcServerInterceptor();
    }
}
