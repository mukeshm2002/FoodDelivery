package com.mukesh.FoodDelivery.controller;

import com.mukesh.FoodDelivery.model.MenuItem;
import com.mukesh.FoodDelivery.model.Restaurant;
import com.mukesh.FoodDelivery.model.User;
import com.mukesh.FoodDelivery.service.ImageUploadService;
import com.mukesh.FoodDelivery.service.MenuService;
import com.mukesh.FoodDelivery.service.RestaurantService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/owner")
public class RestaurantOwnerController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ImageUploadService imageUploadService;

    @Autowired
    private MenuService menuService;

    @GetMapping("/dashboard")
    public String showOwnerDashboard(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null || !"RESTAURANT_OWNER".equals(loggedInUser.getRole())) {
            return "redirect:/login";
        }

        List<Restaurant> myRestaurants = restaurantService.getRestaurantsByOwner(loggedInUser.getId());

        if (!myRestaurants.isEmpty()) {
            Restaurant restaurant = myRestaurants.get(0);
            model.addAttribute("restaurant", restaurant);
            model.addAttribute("menuItems", menuService.getMenuItemsByRestaurant(restaurant.getId()));
        } else {
            model.addAttribute("restaurant", null);
        }

        model.addAttribute("user", loggedInUser);
        return "owner/owner-dashboard";
    }

    @GetMapping("/restaurant/add")
    public String showAddRestaurantPage(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null || !"RESTAURANT_OWNER".equals(loggedInUser.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("restaurant", new Restaurant());
        return "owner/add-restaurant";
    }

    // [UPDATED FIX]: @RequestParam-க்கு பதிலாக நேரடியாக Request-ல் இருந்து வேல்யூஸ் எடுக்கிறோம்.
    // இதனால் 400 Bad Request வர வாய்ப்பே இல்லை!
    @PostMapping("/restaurant/add")
    public String saveRestaurant(HttpServletRequest request, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        try {
            String name = request.getParameter("name");
            String cuisineType = request.getParameter("cuisineType");
            String address = request.getParameter("address");

            Restaurant restaurant = new Restaurant();
            restaurant.setName(name);
            restaurant.setCuisineType(cuisineType);
            restaurant.setAddress(address);
            restaurant.setActive(true);
            restaurant.setOwner(loggedInUser);

            // ஃபைல் அப்லோடை கையாள்கிறோம்
            if (request instanceof MultipartHttpServletRequest) {
                MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
                MultipartFile file = multipartRequest.getFile("imageFile");

                if (file != null && !file.isEmpty()) {
                    String imageUrl = imageUploadService.uploadImage(file);
                    if (imageUrl != null) {
                        restaurant.setImageUrl(imageUrl);
                    }
                } else {
                    restaurant.setImageUrl("https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg");
                }
            }

            restaurantService.saveRestaurant(restaurant);

        } catch (IOException e) {
            return "redirect:/owner/restaurant/add?error";
        }
        return "redirect:/owner/dashboard";
    }

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

    @PostMapping("/restaurant/{restaurantId}/menu/add")
    public String addMenuItem(@PathVariable Long restaurantId,
                              @ModelAttribute("menuItem") MenuItem menuItem,
                              @RequestParam("imageFile") MultipartFile file) {
        try {
            Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
            menuItem.setRestaurant(restaurant);

            String imageUrl = imageUploadService.uploadImage(file);
            if (imageUrl != null) {
                menuItem.setImageUrl(imageUrl);
            }

            menuService.saveMenuItem(menuItem);
        } catch (Exception e) {
            return "redirect:/owner/dashboard?error";
        }
        return "redirect:/owner/dashboard";
    }
}