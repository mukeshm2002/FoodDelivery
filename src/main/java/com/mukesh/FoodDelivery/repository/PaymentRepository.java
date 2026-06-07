package com.mukesh.FoodDelivery.repository;


import com.mukesh.FoodDelivery.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Order ID வச்சு அதோட பேமென்ட் விபரங்களை எடுக்க
    Optional<Payment> findByOrderId(Long orderId);

    // Transaction ID வச்சு பேமென்டை செக் பண்ண
    Optional<Payment> findByTransactionId(String transactionId);
}
