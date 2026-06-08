package com.mukesh.FoodDelivery.repository;



import com.mukesh.FoodDelivery.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
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

    // [ADDED]: பேரு அல்லது சமையல் வகையை வச்சு தேட (Case-Insensitive)
    List<Restaurant> findByNameContainingIgnoreCaseOrCuisineTypeContainingIgnoreCaseAndIsActiveTrue(String name, String cuisine);
}
