package com.app.ecomm.order.Services;


import com.app.ecomm.order.Model.Order;
import com.app.ecomm.order.Model.OrderItem;
import com.app.ecomm.order.Model.OrderStatus;
import com.app.ecomm.order.Repository.OrderRepository;
import com.app.ecomm.order.dto.CartResponse;
import com.app.ecomm.order.dto.OrderCreatedEvent;
import com.app.ecomm.order.dto.OrderItemDTO;
import com.app.ecomm.order.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartService cartService;
    private final OrderRepository orderRepository;
//    private final RabbitTemplate rabbitTemplate;
//
//    @Value("${rabbitmq.exchange.name}")
//    private String exchangeName;
//
//    @Value("${rabbitmq.routing.key}")
//    private String rountingKey;

    private final StreamBridge streamBridge;

    public Optional<OrderResponse> createOrder(String userId) {
        // Validate for cart Items
        List<CartResponse> cartItems = cartService.fetchCartItems(userId);

        if(cartItems.isEmpty()){
            return Optional.empty();
        }

        // Validate for user
//        Optional<User> userOptional = userRepository.findById(Long.valueOf(userId));
//
//        if(userOptional.isEmpty()){
//            return Optional.empty();
//        }
//
//        User user = userOptional.get();

        // Calculate total price
        BigDecimal totalPrice = cartItems.stream()
                .map(CartResponse::getPrice)
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        // Create Order
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalprice(totalPrice);

        List<OrderItem> orderItems = cartItems.stream()
                .map(item -> new OrderItem(
                        null,
                        item.getProductId(),
                        item.getQuantity(),
                        item.getPrice(),
                        order)).toList();

        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);
        // Clear the cart if order placed successfully

        cartService.clearCart(userId);

        // Publish order created event

        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getUserId(),
                savedOrder.getStatus(),
                mapToOrderItemDTOs(savedOrder.getItems()),
                savedOrder.getTotalprice(),
                savedOrder.getCreatedAt()
        );
//        rabbitTemplate.convertAndSend(exchangeName,rountingKey,event);

        streamBridge.send("createOrder-out-0",event);

        return Optional.of(mapOrderToOrderResponse(order));
    }

    private List<OrderItemDTO> mapToOrderItemDTOs(List<OrderItem> items){
        return items.stream()
                .map(item->new OrderItemDTO(
                        item.getId(),
                        item.getProductId(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getPrice().multiply(new BigDecimal(item.getQuantity()))
                )).collect(Collectors.toList());
    }

    private OrderResponse mapOrderToOrderResponse(Order order) {
        OrderResponse orderResponse = new OrderResponse();

        orderResponse.setId(order.getId());
        orderResponse.setItems(order.getItems().stream().map(orderItem -> new OrderItemDTO(
                        orderItem.getId(),
                        orderItem.getProductId(),
                    orderItem.getQuantity(),
                    orderItem.getPrice(),
                orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()))
                )).toList()
        );

        orderResponse.setOrderStatus(order.getStatus());
        orderResponse.setTotalprice(order.getTotalprice());
        orderResponse.setCreatedAt(order.getCreatedAt());

        return orderResponse;
    }
}
