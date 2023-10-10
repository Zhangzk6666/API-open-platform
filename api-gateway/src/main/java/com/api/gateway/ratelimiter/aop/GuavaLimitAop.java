package com.api.gateway.ratelimiter.aop;

import com.alibaba.fastjson.JSONObject;
import com.api.gateway.ratelimiter.RateLimitHelper;
import com.api.gateway.ratelimiter.annotation.RateConfigAnno;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Objects;


@Slf4j
@Aspect
@Component
public class GuavaLimitAop {

    //如果该类和注解在同一包下
    //@Before("execution(@RateConfigAnno * *(..))")
    //如果该类和注解不在同一包下
    @Before("execution(@com.api.gateway.ratelimiter.annotation.RateConfigAnno * *(..))")
    public void limit(JoinPoint joinPoint) {
        //1、获取当前的调用方法
        Method currentMethod = getCurrentMethod(joinPoint);
        if (Objects.isNull(currentMethod)) {
            return;
        }
        //2、从方法注解定义上获取限流的类型
        String limitType = currentMethod.getAnnotation(RateConfigAnno.class).limitType();
        double limitCount = currentMethod.getAnnotation(RateConfigAnno.class).limitCount();
        //使用guava的令牌桶算法获取一个令牌，获取不到先等待
        RateLimiter rateLimiter = RateLimitHelper.getRateLimiter(limitType, limitCount);
        boolean b = rateLimiter.tryAcquire();
        if (b) {
            System.out.println("获取到令牌");
        } else {
            ServerWebExchange exchange = (ServerWebExchange) joinPoint.getArgs()[0];
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.FORBIDDEN);
            byte[] bytes = "用户身份验证错误!".getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            response.writeWith(Flux.just(buffer));
            response.setComplete();
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

    }

    private Method getCurrentMethod(JoinPoint joinPoint) {
        Method[] methods = joinPoint.getTarget().getClass().getMethods();
        Method target = null;
        for (Method method : methods) {
            if (method.getName().equals(joinPoint.getSignature().getName())) {
                target = method;
                break;
            }
        }
        //或者使用如下方式获取method对象
        //MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //Method method = signature.getMethod();
        return target;
    }

    public void output(HttpServletResponse response, String msg) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            outputStream.write(msg.getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            outputStream.flush();
            outputStream.close();
        }
    }
}
