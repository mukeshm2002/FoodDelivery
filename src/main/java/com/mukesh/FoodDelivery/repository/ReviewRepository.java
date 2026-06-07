package com.mukesh.FoodDelivery.repository;


import com.mukesh.FoodDelivery.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // ஒரு குறிப்பிட்ட ஹோட்டலுக்கு வந்த எல்லா ரிவியூக்களையும் லோடு செய்ய
    List<Review> findByRestaurantIdOrderByCreatedAtDesc(Long restaurantId);
}
