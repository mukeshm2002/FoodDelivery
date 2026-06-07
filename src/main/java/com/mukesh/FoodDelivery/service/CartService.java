package com.mukesh.FoodDelivery.service;


import com.mukesh.FoodDelivery.model.Cart;
import com.mukesh.FoodDelivery.model.CartItem;
import com.mukesh.FoodDelivery.model.MenuItem;
import com.mukesh.FoodDelivery.model.User;
import com.mukesh.FoodDelivery.repository.CartItemRepository;
import com.mukesh.FoodDelivery.repository.CartRepository;
import com.mukesh.FoodDelivery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Optional;

@Service
@Transactional // Database Rollback மேனேஜ்மென்ட்க்காக
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private MenuService menuService;

    @Autowired
    private UserRepository userRepository;

    // 1. யூசரோட கார்ட்டை எடுக்க (Get or Create Cart)
    public Cart getCartByUser(Long userId) throws Exception {
        Optional<Cart> cart = cartRepository.findByUserId(userId);

        if (cart.isPresent()) {
            return cart.get();
        } else {
            // கஸ்டமர்க்கு கார்ட் இல்லைனா புதுசா ஒன்னு கிரியேட் பண்றோம்
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new Exception("பயனர் கண்டறியப்படவில்லை!"));
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setItems(new ArrayList<>());
            return cartRepository.save(newCart);
        }
    }

    // 2. கார்ட்டில் உணவைச் சேர்க்க (Add Item to Cart)
    public CartItem addItemToCart(Long userId, Long menuItemId, Integer quantity) throws Exception {
        Cart cart = getCartByUser(userId);
        MenuItem menuItem = menuService.getMenuItemById(menuItemId);

        // ஏற்கனவே இந்த சாப்பாடு கார்ட்ல இருக்கான்னு செக் பண்றோம்
        Optional<CartItem> existingCartItem = cart.getItems().stream()
                .filter(item -> item.getMenuItem().getId().equals(menuItemId))
                .findFirst();

        if (existingCartItem.isPresent()) {
            // ஏற்கனவே இருந்தா குவாண்டிட்டியை மட்டும் கூட்டுறோம்
            CartItem item = existingCartItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            return cartItemRepository.save(item);
        } else {
            // புது ஐட்டம்னா கார்ட்ல ஆட் பண்றோம்
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setMenuItem(menuItem);
            newItem.setQuantity(quantity);
            return cartItemRepository.save(newItem);
        }
    }

    // 3. கார்ட் ஐட்டத்தின் அளவைக் குறைக்க அல்லது நீக்க (Remove / Decrease Item)
    public void removeOrDecreaseItem(Long cartItemId) throws Exception {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new Exception("கார்ட் ஐட்டம் கிடைக்கவில்லை!"));

        if (cartItem.getQuantity() > 1) {
            // ஒன்றுக்கு மேல் இருந்தால் அளவை 1 குறைக்கிறோம்
            cartItem.setQuantity(cartItem.getQuantity() - 1);
            cartItemRepository.save(cartItem);
        } else {
            // 1 தான் இருந்தா கார்ட்ல இருந்தே டெலிட் பண்றோம்
            cartItemRepository.delete(cartItem);
        }
    }

    // 4. ஆர்டர் பிளேஸ் ஆனதும் கார்ட்டை முழுமையாக காலி செய்ய (Clear Cart)
    public void clearCart(Long cartId) {
        cartItemRepository.deleteByCartId(cartId);
    }
}
