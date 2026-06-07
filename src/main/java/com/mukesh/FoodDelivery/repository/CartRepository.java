package com.mukesh.FoodDelivery.repository;



import com.mukesh.FoodDelivery.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // லாகின் செஞ்சிருக்கிற யூசரோட ஆக்டிவ் கார்ட்டை மட்டும் எடுக்க
    Optional<Cart> findByUserId(Long userId);
}
