package com.mukesh.FoodDelivery.repository;


import com.mukesh.FoodDelivery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Email வச்சு யூசரைத் தேட (Login மற்றும் Validation-க்கு)
    Optional<User> findByEmail(String email);

    // ஒரு குறிப்பிட்ட ரோல் உள்ளவர்களை மட்டும் எடுக்க (Optional)
    java.util.List<User> findByRole(String role);
}
