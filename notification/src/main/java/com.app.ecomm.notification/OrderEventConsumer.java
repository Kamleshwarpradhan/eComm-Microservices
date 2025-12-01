package com.app.ecomm.notification;

import com.app.ecomm.notification.payload.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import java.util.function.Consumer;

@Service
@Slf4j
public class OrderEventConsumer {
//    @RabbitListener(queues = "${rabbitmq.queue.name}")
//    public void handleOrderEvent(OrderCreatedEvent orderEvent){
//        System.out.println("Recieved order event is: "+ orderEvent);
//
//        long orderId = orderEvent.getOrderId();
//        OrderStatus orderStatus = orderEvent.getStatus();
//
//        System.out.println("Order ID: "+orderId);
//        System.out.println("Order Status: "+orderStatus);
//    }

    @Bean
    public Consumer<OrderCreatedEvent> orderCreated(){
        return event -> {
            log.info("Received order created event for order: {}",event.getOrderId());
            log.info("Received order created event for userId: {}",event.getUserId());
        };
    }

}
