package com.mukesh.FoodDelivery.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String phone;

    @Column(nullable = false)
    private String role; // CUSTOMER, RESTAURANT_OWNER, DELIVERY_PARTNER

    private String address;

    // One owner can have multiple restaurants
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Restaurant> restaurants;

    // A user can have multiple saved delivery addresses
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<DeliveryAddress> deliveryAddresses;
}
