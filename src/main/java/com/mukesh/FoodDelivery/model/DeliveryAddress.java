package com.mukesh.FoodDelivery.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "delivery_addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String addressLine; // கதவு எண், தெரு விபரம்

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String pincode;

    @Column(nullable = false)
    private String addressType; // HOME, WORK, OTHER (எ.கா: பிரண்ட் வீடு)

    // ஒரு கஸ்டமருக்கு பல அட்ரஸ் இருக்கலாம்
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User customer;
}
