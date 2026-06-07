package com.mukesh.FoodDelivery.repository;


import com.mukesh.FoodDelivery.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // ஒரு குறிப்பிட்ட கார்ட்டுக்குள் இருக்கும் எல்லா ஐட்டம்களையும் எடுக்க
    List<CartItem> findByCartId(Long cartId);

    // கார்ட்டை காலி செய்ய (Clear Cart - ஆர்டர் பிளேஸ் ஆனதும் தேவைப்படும்)
    void deleteByCartId(Long cartId);
}
