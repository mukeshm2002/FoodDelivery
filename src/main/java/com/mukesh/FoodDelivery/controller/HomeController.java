package com.mukesh.FoodDelivery.controller;


import com.mukesh.FoodDelivery.model.Restaurant;
import com.mukesh.FoodDelivery.model.User;
import com.mukesh.FoodDelivery.service.RestaurantService;
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
    private RestaurantService restaurantRepository; // RestaurantService-ஐ கூப்பிடுறோம்

    @GetMapping("/home")
    public String showHomePage(HttpSession session,
                               @RequestParam(required = false) String search,
                               Model model) {

        // செஷன்ல யூசர் இருக்காங்களான்னு செக் பண்றோம் (பாதுகாப்பிற்கு)
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // லாகின் பண்ணலைனா திருப்பி அனுப்பிடும்
        }

        List<Restaurant> restaurantList;

        // சர்ச் பாக்ஸ்ல ஏதாச்சும் டைப் பண்ணி தேடினா பில்டர் பண்ணும், இல்லைனா எல்லா ஆக்டிவ் ஹோட்டலையும் காட்டும்
        if (search != null && !search.isEmpty()) {
            restaurantList = restaurantRepository.searchRestaurantsByName(search);
            model.addAttribute("searchValue", search);
        } else {
            restaurantList = restaurantRepository.getAllActiveRestaurants();
        }

        model.addAttribute("user", loggedInUser);
        model.addAttribute("restaurants", restaurantList);
        return "customer/home"; // home.html பக்கத்தை லோடு செய்யும்
    }
}
