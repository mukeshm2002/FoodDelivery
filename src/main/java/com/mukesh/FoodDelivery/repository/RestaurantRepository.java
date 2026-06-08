package com.mukesh.FoodDelivery.repository;

import com.mukesh.FoodDelivery.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // [ADDED]: குயரி இம்போர்ட்
import org.springframework.data.repository.query.Param; // [ADDED]: பேராம் இம்போர்ட்
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    // தற்போது ஆன்லைன்ல ஆக்டிவா (Open) இருக்குற ஹோட்டல்களை மட்டும் எடுக்க
    List<Restaurant> findByIsActiveTrue();

    // ஹோட்டல் பேரை வச்சு தேட (Search Feature-க்கு)
    List<Restaurant> findByNameContainingIgnoreCase(String name);

    // ஒரு குறிப்பிட்ட ஓனருக்கு சொந்தமான ஹோட்டல்களை மட்டும் எடுக்க
    List<Restaurant> findByOwnerId(Long ownerId);

    // [UPDATED FIX]: JPQL குயரி மூலமாக பேரு அல்லது சமையல் வகையை கச்சிதமாக தேடுகிறோம் (Active-ஆ உள்ளவை மட்டும்)
    @Query("SELECT r FROM Restaurant r WHERE (LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(r.cuisineType) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND r.isActive = true")
    List<Restaurant> searchRestaurants(@Param("keyword") String keyword);
}