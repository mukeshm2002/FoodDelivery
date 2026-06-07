package com.mukesh.FoodDelivery.service;


import com.mukesh.FoodDelivery.model.DeliveryAddress;
import com.mukesh.FoodDelivery.model.Order;
import com.mukesh.FoodDelivery.model.Payment;
import com.mukesh.FoodDelivery.model.Review;
import com.mukesh.FoodDelivery.repository.DeliveryAddressRepository;
import com.mukesh.FoodDelivery.repository.PaymentRepository;
import com.mukesh.FoodDelivery.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class UtilitySupportService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private DeliveryAddressRepository addressRepository;

    // --- PAYMENT LAOGIC ---
    public Payment processPayment(Order order, String paymentMode) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMode(paymentMode); // COD, UPI, CARD

        if (paymentMode.equals("COD")) {
            payment.setPaymentStatus("PENDING");
        } else {
            payment.setPaymentStatus("SUCCESS");
            payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        return paymentRepository.save(payment);
    }

    // --- DELIVERY ADDRESS LOGIC ---
    public DeliveryAddress saveAddress(DeliveryAddress address) {
        return addressRepository.save(address);
    }

    public List<DeliveryAddress> getCustomerAddresses(Long customerId) {
        return addressRepository.findByCustomerId(customerId);
    }

    // --- REVIEWS LOGIC ---
    public Review addReview(Review review) {
        return reviewRepository.save(review);
    }

    public List<Review> getRestaurantReviews(Long restaurantId) {
        return reviewRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId);
    }
}
