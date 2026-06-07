package com.mukesh.FoodDelivery.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "restaurants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String cuisineType; // e.g., Biryani, South Indian

    private String address;

    private String imageUrl;

    private boolean isActive = true;

    // Many restaurants can belong to one owner (User)
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // One restaurant can have many menu items
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<MenuItem> menuItems;

    private Long ownerId;
}
