package com.mukesh.FoodDelivery.controller;

import com.mukesh.FoodDelivery.model.Cart;
import com.mukesh.FoodDelivery.model.DeliveryAddress;
import com.mukesh.FoodDelivery.model.Order;
import com.mukesh.FoodDelivery.model.User;
import com.mukesh.FoodDelivery.service.CartService;
import com.mukesh.FoodDelivery.service.OrderService;
import com.mukesh.FoodDelivery.service.UtilitySupportService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UtilitySupportService utilitySupportService;

    // 1. செக்-அவுட் பக்கத்தை காட்ட (Show Checkout Page)
    @GetMapping("/checkout")
    public String showCheckoutPage(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        try {
            Cart cart = cartService.getCartByUser(loggedInUser.getId());
            if (cart.getItems() == null || cart.getItems().isEmpty()) {
                return "redirect:customer/cart?error=empty";
            }

            // கஸ்டமரோட சேவ் செய்யப்பட்ட அட்ரஸ் லிஸ்ட்டை எடுக்குறோம்
            List<DeliveryAddress> addresses = utilitySupportService.getCustomerAddresses(loggedInUser.getId());

            double total = cart.getItems().stream()
                    .mapToDouble(item -> item.getMenuItem().getPrice() * item.getQuantity())
                    .sum();

            model.addAttribute("cart", cart);
            model.addAttribute("totalAmount", total);
            model.addAttribute("addresses", addresses);
            model.addAttribute("newAddress", new DeliveryAddress()); // புது அட்ரஸ் ஆட் பண்ண ஃபார்ம் ஆப்ஜெக்ட்

            return "customer/checkout"; // checkout.html
        } catch (Exception e) {
            return "redirect:customer/cart";
        }
    }

    // 2. செக்-அவுட் பக்கத்திலேயே புது அட்ரஸ் ஆட் பண்ணினால்
    @PostMapping("/checkout/address/add")
    public String addAddress(@ModelAttribute("newAddress") DeliveryAddress address, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        address.setCustomer(loggedInUser);
        utilitySupportService.saveAddress(address);
        return "redirect:/checkout"; // அட்ரஸ் சேவ் ஆனதும் அதே பக்கத்துக்கு ரீப்ஃப்ரெஷ் ஆகும்
    }

    // 3. ஆர்டரை கன்பர்ம் செய்து பிளேஸ் பண்ண (Confirm and Place Order)
    @PostMapping("/order/place")
    public String placeOrder(@RequestParam String paymentMode,
                             HttpSession session,
                             Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        try {
            Cart cart = cartService.getCartByUser(loggedInUser.getId());

            // [UPDATED] கார்ட் திடீர்னு காலியா இருந்தா கிராஷ் ஆகாம தடுக்க கண்டிஷன்
            if (cart.getItems() == null || cart.getItems().isEmpty()) {
                return "redirect:/checkout?error=empty_cart";
            }

            // கார்ட்ல இருக்குற முதல் ஐட்டத்தோட ரெஸ்டாரன்ட் ID-யை எடுக்குறோம்
            Long restaurantId = cart.getItems().get(0).getMenuItem().getRestaurant().getId();

            // 1. ஆர்டர் பிளேஸ் பண்றோம் (Order & OrderItems டேபிளுக்கு டேட்டா மாறும், கார்ட் காலியாகும்)
            Order order = orderService.placeOrder(loggedInUser.getId(), restaurantId);

            // 2. பேமென்ட் பிராசஸ் பண்றோம் (COD-ஆ இருந்தா Pending, மத்தது Success-னு மாறும்)
            utilitySupportService.processPayment(order, paymentMode);

            model.addAttribute("order", order);
            return "customer/order-success"; // order-success.html (ஆர்டர் கன்பர்ம் ஆன பேஜ்)

        } catch (Exception e) {
            return "redirect:/checkout?error=" + e.getMessage();
        }
    }

    // 4. கஸ்டமரோட பழைய ஆர்டர் விபரங்கள் (Order History Page)
    @GetMapping("/orders")
    public String showOrderHistory(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        List<Order> orderList = orderService.getCustomerOrderHistory(loggedInUser.getId());
        model.addAttribute("orders", orderList);
        return "customer/order-history"; // order-history.html
    }
}