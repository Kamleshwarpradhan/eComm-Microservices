package com.eccomerce.gateway;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

@Configuration
public class GatwayConfig {

    @Bean
    public RedisRateLimiter redisRateLimiter(){
        return new RedisRateLimiter(10,20,1);
    }

    @Bean
    public KeyResolver hostNameKeyResolver(){
        return exchange -> Mono.just(
                exchange.getRequest().getRemoteAddress().getHostName()
        );
    }

    @Bean
    public RouteLocator customLocator(RouteLocatorBuilder builder){
        return builder.routes()
                .route("product-microservice",r -> r
                            .path("/api/products/**")
                            .filters(f->f.retry(retryConfig -> retryConfig
                                            .setRetries(5)
                                            .setMethods(HttpMethod.GET)
                                    )
                                    .circuitBreaker(config ->
                                    config.setName("ecomBreaker")
                                            .setFallbackUri("forward:/fallback/products"))
                            )
//                            .filters(f->f.rewritePath("/products(?<segment>/?.*)","/api/products${segment}"))
                            .uri("lb://PRODUCT-MICROSERVICE"))
                .route("user-microservice",r -> r
                        .path("/api/users/**")
//                        .filters(f->f.rewritePath("/users(?<segment>/?.*)","/api/users{segment}"))
                        .uri("lb://USER-MICROSERVICE"))
                .route("order-microservice",r -> r
                        .path("/api/orders/**","/api/cart/**")
//                        .filters(f->f.rewritePath("/(?<segment>.*)","/api/${segment}"))
                        .uri("lb://ORDER-MICROSERVICE"))
                .route("eureka-server",r -> r
                        .path("/eureka/main")
                        .filters(f -> f.rewritePath("/eureka/main", "/"))
                        .uri("http://localhost:8761"))
                .route("eureka-server-static",r -> r
                        .path("/eureka/**")
                        .uri("http://localhost:8761"))
                .build();
    }
}
