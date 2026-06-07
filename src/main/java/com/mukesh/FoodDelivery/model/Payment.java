package com.mukesh.FoodDelivery.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String paymentMode; // COD, UPI, CARD

    @Column(nullable = false)
    private String paymentStatus; // PENDING, SUCCESS, FAILED

    private String transactionId;

    private LocalDateTime paymentTime = LocalDateTime.now();

    // One order can have one payment record
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}
