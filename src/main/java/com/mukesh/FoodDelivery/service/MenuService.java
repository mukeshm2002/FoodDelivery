package com.mukesh.FoodDelivery.service;


import com.mukesh.FoodDelivery.model.MenuItem;
import com.mukesh.FoodDelivery.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MenuService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    // 1. ஒரு ஹோட்டலுக்கு புது உணவை ஆட் செய்ய (Add Menu Item)
    public MenuItem saveMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

    // 2. ஒரு ஹோட்டலோட முழு மெனு கார்டையும் எடுக்க (For Restaurant Detail Page)
    public List<MenuItem> getMenuByRestaurant(Long restaurantId) {
        return menuItemRepository.findByRestaurantId(restaurantId);
    }

    // 3. ஹோட்டல்ல தற்போது ஸ்டாக் இருக்குற (Available) சாப்பாட்டை மட்டும் காட்ட
    public List<MenuItem> getAvailableMenuByRestaurant(Long restaurantId) {
        return menuItemRepository.findByRestaurantIdAndIsAvailableTrue(restaurantId);
    }

    // 4. ஒரு குறிப்பிட்ட உணவோட விபரங்களை ID வச்சு எடுக்க
    public MenuItem getMenuItemById(Long id) throws Exception {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new Exception("உணவு வகை கண்டறியப்படவில்லை!"));
    }

    // 5. சாப்பாடு தற்காலிகமாக தீர்ந்துவிட்டால் (Toggle Availability - Out of Stock)
    public MenuItem toggleItemAvailability(Long id) throws Exception {
        MenuItem item = getMenuItemById(id);
        item.setAvailable(!item.isAvailable());
        return menuItemRepository.save(item);
    }

    // 6. மெனுவில் இருந்து ஒரு உணவை நீக்க (Delete Menu Item)
    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
    }

    // உங்க MenuService கிளாஸ்ல இதை சேர்த்துக்கோங்க:
    public List<MenuItem> getMenuItemsByRestaurant(Long restaurantId) {
        return menuItemRepository.findByRestaurantId(restaurantId);
    }

    // உங்க MenuService கிளாஸிற்குள் இந்த மெத்தடை சேர்க்கவும்:

    public List<MenuItem> getRecentMenuItems() {
        return menuItemRepository.findTop8ByOrderByIdDesc();
    }
}
