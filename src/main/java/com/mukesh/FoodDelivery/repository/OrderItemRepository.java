package com.mukesh.FoodDelivery.repository;


import com.mukesh.FoodDelivery.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // ஒரு குறிப்பிட்ட ஆர்டரில் உள்ள அனைத்து சாப்பாட்டு ஐட்டம்களையும் எடுக்க
    List<OrderItem> findByOrderId(Long orderId);
}
