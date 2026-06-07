package com.mukesh.FoodDelivery.repository;


import com.mukesh.FoodDelivery.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    // ஒரு ஹோட்டலோட முழு மெனு கார்டையும் எடுக்க
    List<MenuItem> findByRestaurantId(Long restaurantId);

    // ஹோட்டல்ல இப்போதைக்கு அவைலபிளா இருக்குற சாப்பாட்டை மட்டும் காட்ட
    List<MenuItem> findByRestaurantIdAndIsAvailableTrue(Long restaurantId);
}
