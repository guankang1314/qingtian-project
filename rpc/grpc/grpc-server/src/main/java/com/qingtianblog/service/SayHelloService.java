package com.qingtianblog.service;


import com.qingtianblog.grpc.lib.HelloReply;
import com.qingtianblog.grpc.lib.HelloReply.Builder;
import com.qingtianblog.grpc.lib.HelloRequest;
import com.qingtianblog.grpc.lib.SimpleGrpc.SimpleImplBase;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class SayHelloService extends SimpleImplBase {

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder().setMessage("helloWorld" + request.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
