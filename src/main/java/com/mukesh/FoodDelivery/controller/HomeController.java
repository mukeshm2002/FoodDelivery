package com.mukesh.FoodDelivery.controller;

import com.mukesh.FoodDelivery.model.MenuItem;
import com.mukesh.FoodDelivery.model.Restaurant;
import com.mukesh.FoodDelivery.model.User;
import com.mukesh.FoodDelivery.service.RestaurantService;
import com.mukesh.FoodDelivery.service.MenuService; // [ADDED]: மெனு சர்வீஸ் இம்போர்ட்
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private RestaurantService restaurantService; // RestaurantService-ஐ கூப்பிடுறோம்

    @Autowired
    private MenuService menuService; // [ADDED]: MenuService-ஐ இன்ஜெக்ட் செய்கிறோம்

    @GetMapping("/home")
    public String showHomePage(HttpSession session, Model model, @RequestParam(value = "search", required = false) String search) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        List<Restaurant> restaurants;
        if (search != null && !search.trim().isEmpty()) {
            restaurants = restaurantService.searchRestaurants(search);
            model.addAttribute("searchValue", search);
        } else {
            restaurants = restaurantService.getAllActiveRestaurants();
        }

        // [TRY-CATCH SAFE MODE]: எர்ரர் வந்தாலும் அப்ளிகேஷன் உடையாது
        List<MenuItem> recentFoods = null;
        try {
            recentFoods = menuService.getRecentMenuItems();
            System.out.println("✅ Recent Foods Loaded: " + (recentFoods != null ? recentFoods.size() : 0));
        } catch (Exception e) {
            System.out.println("❌ ERROR WHILE FETCHING RECENT FOODS: " + e.getMessage());
            e.printStackTrace(); // கன்சோலில் அக்யூரெட் லைன் நம்பர் காட்டும்
        }

        model.addAttribute("restaurants", restaurants);
        model.addAttribute("recentFoods", recentFoods);
        model.addAttribute("user", loggedInUser);

        return "customer/home";
    }
}