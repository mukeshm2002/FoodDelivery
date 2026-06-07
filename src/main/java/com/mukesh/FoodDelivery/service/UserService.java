package com.mukesh.FoodDelivery.service;


import com.mukesh.FoodDelivery.model.User;
import com.mukesh.FoodDelivery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 1. புது யூசர் ரிஜிஸ்ட்ரேஷன் (Register User)
    public User registerUser(User user) throws Exception {
        // ஏற்கனவே இந்த Email-ல அக்கவுண்ட் இருக்கான்னு செக் பண்றோம்
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new Exception("இந்த ஈமெயில் முகவரி ஏற்கனவே பயன்படுத்தப்பட்டுள்ளது!");
        }
        // குறிப்பு: ரியல் டைம்ல இங்க Spring Security BCryptPasswordEncoder வச்சு பாஸ்வேர்டை என்க்ரிப்ட் பண்ணனும்.
        return userRepository.save(user);
    }

    // 2. லாகின் வேலிடேஷன் (Login User)
    public User loginUser(String email, String password) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("பயனர் கணக்கு எதுவும் கண்டறியப்படவில்லை!"));

        // பாஸ்வேர்ட் மேட்ச் ஆகுதான்னு செக் பண்றோம்
        if (!user.getPassword().equals(password)) {
            throw new Exception("தவறான கடவுச்சொல் (Incorrect Password)!");
        }
        return user;
    }

    // 3. User ID வச்சு ப்ரொபைல் விபரங்களை எடுக்க
    public User getUserById(Long id) throws Exception {
        return userRepository.findById(id)
                .orElseThrow(() -> new Exception("பயனர் விபரங்கள் கிடைக்கவில்லை!"));
    }
}
