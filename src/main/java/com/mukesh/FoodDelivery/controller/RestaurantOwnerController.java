package com.mukesh.FoodDelivery.controller;

import com.mukesh.FoodDelivery.model.MenuItem;
import com.mukesh.FoodDelivery.model.Restaurant;
import com.mukesh.FoodDelivery.model.User;
import com.mukesh.FoodDelivery.service.ImageUploadService;
import com.mukesh.FoodDelivery.service.MenuService;
import com.mukesh.FoodDelivery.service.RestaurantService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/owner") // மெயின் பேஸ் ஆர் எல் (Base URL)
public class RestaurantOwnerController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ImageUploadService imageUploadService;

    @Autowired
    private MenuService menuService;

    // 1. ஓனர் டேஷ்போர்டு (Owner Dashboard)
    @GetMapping("/dashboard")
    public String showOwnerDashboard(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null || !"RESTAURANT_OWNER".equals(loggedInUser.getRole())) {
            return "redirect:/login";
        }

        List<Restaurant> myRestaurants = restaurantService.getRestaurantsByOwner(loggedInUser.getId());

        // ஃபிரண்ட்-எண்ட் சிங்கிள் ரெஸ்டாரன்ட்டா வாங்குறதுனால முதல் ரெஸ்டாரன்ட்டை மட்டும் மாடல்ல அட்டாச் பண்றோம்
        if (!myRestaurants.isEmpty()) {
            Restaurant restaurant = myRestaurants.get(0);
            model.addAttribute("restaurant", restaurant);
            model.addAttribute("menuItems", menuService.getMenuItemsByRestaurant(restaurant.getId()));
        } else {
            model.addAttribute("restaurant", null);
        }

        model.addAttribute("user", loggedInUser);
        return "owner/owner-dashboard"; // ஃபோல்டர் ஸ்ட்রক্ষர் படி 'owner/' சேர்த்துள்ளேன்
    }

    // 2. புது ஹோட்டல் ஆட் பண்ற பக்கத்தை காட்ட
    @GetMapping("/restaurant/add")
    public String showAddRestaurantPage(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null || !"RESTAURANT_OWNER".equals(loggedInUser.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("restaurant", new Restaurant());
        return "owner/add-restaurant";
    }

    // 3. புது ஹோட்டல் ஃபார்ம் சப்மிட் ஆகும்போது (Cloudinary + Owner Mapping Updated)
    @PostMapping("/restaurant/add")
    public String saveRestaurant(@RequestParam("name") String name,
                                 @RequestParam("cuisineType") String cuisineType,
                                 @RequestParam("address") String address,
                                 @RequestParam(value = "imageFile", required = false) MultipartFile file, // 👈 [FIXED]: required = false மாத்தியாச்சு
                                 HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        try {
            Restaurant restaurant = new Restaurant();
            restaurant.setName(name);
            restaurant.setCuisineType(cuisineType);
            restaurant.setAddress(address);
            restaurant.setActive(true);
            restaurant.setOwner(loggedInUser);

            // 👈 [FIXED]: ஃபைல் காலியாக இல்லாமல் இருந்தால் மட்டும் Cloudinary-க்கு அனுப்பும் செக்கிங்
            if (file != null && !file.isEmpty()) {
                String imageUrl = imageUploadService.uploadImage(file);
                if (imageUrl != null) {
                    restaurant.setImageUrl(imageUrl);
                }
            } else {
                // இமேஜ் வரலனா ஒரு டீஃபால்ட் இமேஜ் ஆர் எல் செட் பண்ணிக்கலாம் (Optional)
                restaurant.setImageUrl("https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg");
            }

            restaurantService.saveRestaurant(restaurant);

        } catch (IOException e) {
            return "redirect:/owner/restaurant/add?error";
        }
        return "redirect:/owner/dashboard";
    }

    // 4. ஒரு ஹோட்டலுக்கு மெனு ஆட் பண்ற பக்கத்தை காட்ட
    @GetMapping("/restaurant/{id}/menu/add")
    public String showAddMenuPage(@PathVariable Long id, HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null || !"RESTAURANT_OWNER".equals(loggedInUser.getRole())) {
            return "redirect:/login";
        }

        MenuItem menuItem = new MenuItem();
        model.addAttribute("menuItem", menuItem);
        model.addAttribute("restaurantId", id);
        return "owner/add-menu";
    }

    // 5. புது மெனு ஐட்டம் ஃபார்ம் சப்மிட் ஆகும்போது (Cloudinary Food Image Integration added!)
    @PostMapping("/restaurant/{restaurantId}/menu/add")
    public String addMenuItem(@PathVariable Long restaurantId,
                              @ModelAttribute("menuItem") MenuItem menuItem,
                              @RequestParam("imageFile") MultipartFile file) {
        try {
            Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
            menuItem.setRestaurant(restaurant);

            // சாப்பாடு இமேஜையும் Cloudinary-க்கு அனுப்புகிறோம்
            String imageUrl = imageUploadService.uploadImage(file);
            if (imageUrl != null) {
                menuItem.setImageUrl(imageUrl); // உங்க MenuItem மாடல்ல imageUrl ஃபீல்ட் இருக்கணும்
            }

            menuService.saveMenuItem(menuItem);
        } catch (Exception e) {
            return "redirect:/owner/dashboard?error";
        }
        return "redirect:/owner/dashboard";
    }
}