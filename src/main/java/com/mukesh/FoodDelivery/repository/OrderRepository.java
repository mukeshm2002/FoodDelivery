package com.mukesh.FoodDelivery.repository;



import com.mukesh.FoodDelivery.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // ஒரு கஸ்டமரோட முழு ஆர்டர் ஹிஸ்டரியை (சமீபத்திய ஆர்டர் முதலில் வர மாதிரி) எடுக்க
    List<Order> findByCustomerIdOrderByOrderTimeDesc(Long customerId);

    // ஒரு ஹோட்டலுக்கு வந்த ஆர்டர்களை ஓனர் பார்க்க
    List<Order> findByRestaurantIdOrderByOrderTimeDesc(Long restaurantId);

    // டெலிவரி பார்ட்னருக்கு அசைன் செய்யப்பட்ட ஆர்டர்களை பில்டர் செய்ய
    List<Order> findByDeliveryPartnerIdAndOrderStatus(Long deliveryPartnerId, String orderStatus);
}
