package com.mukesh.FoodDelivery.controller;


import com.mukesh.FoodDelivery.model.Cart;
import com.mukesh.FoodDelivery.model.MenuItem;
import com.mukesh.FoodDelivery.model.Restaurant;
import com.mukesh.FoodDelivery.model.User;
import com.mukesh.FoodDelivery.service.CartService;
import com.mukesh.FoodDelivery.service.MenuService;
import com.mukesh.FoodDelivery.service.RestaurantService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class RestaurantDetailController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private CartService cartService;

    // 1. ஹோட்டலை கிளிக் செய்யும்போது மெனு கார்டை காட்ட (Restaurant Detail Page)
    @GetMapping("/restaurant/{id}")
    public String showRestaurantDetails(@PathVariable Long id, HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        try {
            Restaurant restaurant = restaurantService.getRestaurantById(id);
            List<MenuItem> menuItems = menuService.getAvailableMenuByRestaurant(id);

            model.addAttribute("restaurant", restaurant);
            model.addAttribute("menuItems", menuItems);
            return "restaurant-detail"; // restaurant-detail.html
        } catch (Exception e) {
            return "redirect:/home?error";
        }
    }

    // 2. கார்ட்டில் உணவைச் சேர்க்க (Add to Cart)
    @PostMapping("/cart/add")
    public String addItemToCart(@RequestParam Long menuItemId,
                                @RequestParam Integer quantity,
                                @RequestParam Long restaurantId,
                                HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        try {
            cartService.addItemToCart(loggedInUser.getId(), menuItemId, quantity);
        } catch (Exception e) {
            return "redirect:/restaurant/" + restaurantId + "?error";
        }
        return "redirect:/cart"; // ஆட் ஆனதும் கார்ட் பக்கத்துக்கு ரீடைரக்ட் பண்ணும்
    }

    // 3. கார்ட் பக்கத்தை லோடு செய்ய (View Cart)
    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        try {
            Cart cart = cartService.getCartByUser(loggedInUser.getId());
            model.addAttribute("cart", cart);

            // கார்ட்ல இருக்குற டோட்டல் அமௌன்ட் கால்குலேட் பண்ணி UI-க்கு அனுப்புறோம்
            double total = cart.getItems().stream()
                    .mapToDouble(item -> item.getMenuItem().getPrice() * item.getQuantity())
                    .sum();
            model.addAttribute("totalAmount", total);

            return "cart"; // cart.html
        } catch (Exception e) {
            return "redirect:/home";
        }
    }
    @PostMapping("/cart/remove")
    public String removeCartItem(@RequestParam Long cartItemId) {
        try {
            cartService.removeOrDecreaseItem(cartItemId);
        } catch (Exception e) {
            return "redirect:/cart?error";
        }
        return "redirect:/cart";
    }
}
