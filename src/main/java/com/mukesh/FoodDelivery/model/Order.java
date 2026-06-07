package com.mukesh.FoodDelivery.model;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double totalAmount;

    @Column(nullable = false)
    private LocalDateTime orderTime = LocalDateTime.now();

    @Column(nullable = false)
    private String orderStatus; // PLACED, PREPARING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED

    // Who placed the order
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    // From which restaurant
    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    // Which delivery partner is assigned (Initially can be null)
    @ManyToOne
    @JoinColumn(name = "delivery_partner_id")
    private User deliveryPartner;
}
