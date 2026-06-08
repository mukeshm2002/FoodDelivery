package com.mukesh.FoodDelivery.service;

import com.mukesh.FoodDelivery.model.*;
import com.mukesh.FoodDelivery.repository.OrderItemRepository;
import com.mukesh.FoodDelivery.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartService cartService;

    // 1. கார்ட்டில் உள்ள பொருட்களை ஆர்டராக மாற்றுதல் (Place Order)
    // [FIXED]: கம்பைலர் எர்ரர் வராமல் தடுக்க 'Long addressId' பேராமீட்டர் மட்டும் வைக்கப்பட்டு, உள்ளே எர்ரர் அடித்த செட்டர் நீக்கப்பட்டுள்ளது
    public Order placeOrder(Long userId, Long restaurantId, Long addressId) throws Exception {
        Cart cart = cartService.getCartByUser(userId);
        if (cart.getItems().isEmpty()) {
            throw new Exception("உங்க கார்ட் காலியாக உள்ளது!");
        }

        // புது ஆர்டர் ரெக்கார்ட் கிரியேட் பண்றோம்
        Order order = new Order();
        order.setCustomer(cart.getUser());

        // முதல் ஐட்டத்தோட ரெஸ்டாரன்ட்டை மெயினான ஆர்டருக்கு செட் பண்றோம்
        Restaurant restaurant = cart.getItems().get(0).getMenuItem().getRestaurant();
        order.setRestaurant(restaurant);
        order.setOrderStatus("PLACED");
        order.setOrderTime(LocalDateTime.now());

        // டோட்டல் பில் கால்குலேட் பண்றோம்
        double totalAmount = 0;
        for (CartItem cartItem : cart.getItems()) {
            totalAmount += cartItem.getMenuItem().getPrice() * cartItem.getQuantity();
        }
        order.setTotalAmount(totalAmount);

        // ஆர்டரை முதலில் சேவ் பண்றோம் (Order ID ஜெனரேட் ஆகணும்)
        Order savedOrder = orderRepository.save(order);

        // கார்ட் ஐட்டம்களை அப்படியே OrderItem டேபிளுக்கு மாத்துறோம்
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setMenuItem(cartItem.getMenuItem());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getMenuItem().getPrice()); // Snapshot Price
            orderItemRepository.save(orderItem);
        }

        // ஆர்டர் பிளேஸ் ஆனதும் கார்ட்டை காலி பண்ணிடுறோம்
        cartService.clearCart(cart.getId());

        return savedOrder;
    }

    // 2. ஆர்டர் நிலையை மாற்றுதல் (Update Order Status)
    public Order updateOrderStatus(Long orderId, String status) throws Exception {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception("ஆர்டர் விபரங்கள் கிடைக்கவில்லை!"));
        order.setOrderStatus(status);
        return orderRepository.save(order);
    }

    // 3. டெலிவரி பாயை அசைன் செய்தல் (Assign Delivery Partner)
    public Order assignDeliveryPartner(Long orderId, User deliveryPartner) throws Exception {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception("ஆர்டர் விபரங்கள் கிடைக்கவில்லை!"));
        order.setDeliveryPartner(deliveryPartner);
        return orderRepository.save(order);
    }

    // 4. ஒரு கஸ்டமரோட முழு ஆர்டர் ஹிஸ்டரி (Order History)
    public List<Order> getCustomerOrderHistory(Long customerId) {
        return orderRepository.findByCustomerIdOrderByOrderTimeDesc(customerId);
    }
}