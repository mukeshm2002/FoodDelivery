package com.mukesh.FoodDelivery.service;


import com.mukesh.FoodDelivery.model.Restaurant;
import com.mukesh.FoodDelivery.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    // 1. புதிய உணவகத்தை ஆட் செய்ய (Add / Save Restaurant)
    public Restaurant saveRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    // 2. தற்போது ஆன்லைன்ல ஆக்டிவா (Open) இருக்குற எல்லா ஹோட்டல்களையும் எடுக்க (For Home Page)
    public List<Restaurant> getAllActiveRestaurants() {
        return restaurantRepository.findByIsActiveTrue();
    }

    // 3. ஹோட்டல் பேரை வச்சு தேட (Search Feature)
    public List<Restaurant> searchRestaurantsByName(String name) {
        return restaurantRepository.findByNameContainingIgnoreCase(name);
    }

    // 4. ஒரு குறிப்பிட்ட ஓனருக்கு சொந்தமான ஹோட்டல்களை மட்டும் எடுக்க (For Owner Dashboard)
    public List<Restaurant> getRestaurantsByOwner(Long ownerId) {
        return restaurantRepository.findByOwnerId(ownerId);
    }

    // 5. ஹோட்டலோட ID வச்சு விபரங்களை எடுக்க (Menu Page லோடு பண்ண தேவைப்படும்)
    public Restaurant getRestaurantById(Long id) throws Exception {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new Exception("உணவகம் கண்டறியப்படவில்லை!"));
    }

    // 6. ஹோட்டலை தற்காலிகமாக மூட அல்லது திறக்க (Toggle Active Status)
    public Restaurant toggleRestaurantStatus(Long id) throws Exception {
        Restaurant restaurant = getRestaurantById(id);
        restaurant.setActive(!restaurant.isActive()); // True-ஆ இருந்தா False மாறும், False-ஆ இருந்தா True மாறும்
        return restaurantRepository.save(restaurant);
    }

    // உங்க RestaurantService கிளாஸிற்குள் இந்த மெத்தடை சேர்க்கவும்:

    public List<Restaurant> searchRestaurants(String keyword) {
        // keyword-ஐ ரெண்டு பேராமீட்டருக்கும் பாஸ் பண்றோம் (பேர்லயும் தேடும், குசின்லயும் தேடும்)
        return restaurantRepository.findByNameContainingIgnoreCaseOrCuisineTypeContainingIgnoreCaseAndIsActiveTrue(keyword, keyword);
    }


}
