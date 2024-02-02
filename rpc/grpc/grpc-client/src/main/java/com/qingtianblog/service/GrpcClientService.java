package com.qingtianblog.service;

import com.qingtianblog.grpc.lib.HelloReply;
import com.qingtianblog.grpc.lib.HelloRequest;
import com.qingtianblog.grpc.lib.SimpleGrpc.SimpleBlockingStub;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class GrpcClientService  {

    @GrpcClient("grpc-server")
    private SimpleBlockingStub simpleStub;

    public String sendMessage(String name){
        try {
            HelloReply helloReply = simpleStub.sayHello(
                HelloRequest.newBuilder().setName(name).build());
            return helloReply.getMessage();
        }catch (StatusRuntimeException e) {
            return "fail with"+ e.getStatus().getCode().name();
        }

    }
}
