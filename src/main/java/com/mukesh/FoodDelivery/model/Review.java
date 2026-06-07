package com.mukesh.FoodDelivery.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer rating; // 1 to 5 stars

    private String comment;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // ரிவியூ எழுதிய கஸ்டமர்
    private User customer;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false) // எந்த ஹோட்டலுக்கு ரிவியூ
    private Restaurant restaurant;
}
