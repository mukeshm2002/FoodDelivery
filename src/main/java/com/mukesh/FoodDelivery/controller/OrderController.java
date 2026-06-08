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
            if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
                return "redirect:/cart?error=empty";
            }

            // கஸ்டமரோட சேவ் செய்யப்பட்ட அட்ரஸ் லிஸ்ட்டை எடுக்குறோம்
            List<DeliveryAddress> addresses = utilitySupportService.getCustomerAddresses(loggedInUser.getId());

            double total = cart.getItems().stream()
                    .mapToDouble(item -> (item.getMenuItem() != null ? item.getMenuItem().getPrice() : 0) * item.getQuantity())
                    .sum();

            model.addAttribute("cart", cart);
            model.addAttribute("totalAmount", total);
            model.addAttribute("addresses", addresses);
            model.addAttribute("newAddress", new DeliveryAddress());

            return "customer/checkout"; // customer/checkout.html
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/cart";
        }
    }

    // 2. செக்-அவுட் பக்கத்திலேயே புது அட்ரஸ் ஆட் பண்ணினால்
    @PostMapping("/checkout/address/add")
    public String addAddress(@ModelAttribute("newAddress") DeliveryAddress address, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        address.setCustomer(loggedInUser);
        utilitySupportService.saveAddress(address);
        return "redirect:/checkout";
    }

    // 3. ஆர்டரை கன்பர்ம் செய்து பிளேஸ் பண்ண (Confirm and Place Order)
    // [FIXED]: HTML-ல் இருந்து 'selectedAddr' பேராமீட்டரை @RequestParam மூலமாக வாங்குகிறோம்
    @PostMapping("/order/place")
    public String placeOrder(@RequestParam("paymentMode") String paymentMode,
                             @RequestParam("selectedAddr") Long addressId,
                             HttpSession session,
                             Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        try {
            Cart cart = cartService.getCartByUser(loggedInUser.getId());

            if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
                return "redirect:/checkout?error=empty_cart";
            }

            // [SAFETY NULL CHECK FIX]: உணவகம் null-ஆக இருந்தால் NullPointerException வராமல் தடுக்கிறோம்
            Long restaurantId = 1L; // Default ID
            if (cart.getItems().get(0).getMenuItem() != null &&
                    cart.getItems().get(0).getMenuItem().getRestaurant() != null) {
                restaurantId = cart.getItems().get(0).getMenuItem().getRestaurant().getId();
            }

            // [FIXED]: 3 பேராமீட்டர்களுடன் (userId, restaurantId, addressId) சர்வீஸ் மெத்தட் கச்சிதமாக அழைக்கப்படுகிறது
            Order order = orderService.placeOrder(loggedInUser.getId(), restaurantId, addressId);

            // 2. பேமென்ட் பிராசஸ் பண்றோம்
            utilitySupportService.processPayment(order, paymentMode);

            model.addAttribute("order", order);
            return "customer/order-success"; // customer/order-success.html

        } catch (Exception e) {
            e.printStackTrace();
            // எர்ரர் மெசேஜை URL-ல் அனுப்பி செக்அவுட் பக்கத்திற்கே ரீடைரெக்ட் செய்கிறோம்
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
        return "customer/order-history";
    }
}