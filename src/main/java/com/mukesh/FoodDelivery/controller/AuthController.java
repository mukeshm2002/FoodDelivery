package com.mukesh.FoodDelivery.controller;

import com.mukesh.FoodDelivery.model.User;
import com.mukesh.FoodDelivery.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // 1. ரிஜிஸ்ட்ரேஷன் பக்கத்தை லோடு செய்ய
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "auth/register"; // register.html பக்கத்தை தேடும்
    }

    // 2. ரிஜிஸ்ட்ரேஷன் ஃபார்ம் சப்மிட் செய்யும்போது
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        try {
            userService.registerUser(user);
            return "redirect:/login?success"; // ரிஜிஸ்டர் ஆனதும் லாகின் பக்கத்துக்கு போகும்
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register"; // எர்ரர் வந்தா அதே பக்கத்துல காட்டும்
        }
    }

    // 3. லாகின் பக்கத்தை லோடு செய்ய
    // [UPDATED]: லாக்அவுட் அல்லது ரிஜிஸ்ட்ரேஷன் முடிந்து வரும்போது மெசேஜ் காட்ட பேராமீட்டர்களை வாங்குகிறோம்
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "logout", required = false) String logout,
                                @RequestParam(value = "success", required = false) String success,
                                Model model) {
        if (logout != null) {
            model.addAttribute("message", "நீங்கள் வெற்றிகரமாக லாக்-அவுட் செய்யப்பட்டுவிட்டீர்கள்!");
        }
        if (success != null) {
            model.addAttribute("message", "பதிவு வெற்றிகரமாக முடிந்தது! இப்போது லாகின் செய்யலாம்.");
        }
        return "auth/login"; // login.html பக்கத்தை தேடும்
    }

    // 4. லாகின் ஃபார்ம் சப்மிட் செய்யும்போது (Session Management-ஓட)
    @PostMapping("/login")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {
        try {
            User user = userService.loginUser(email, password);

            // லாகின் ஆன யூசரோட விபரங்களை செஷன்ல சேவ் பண்றோம்
            session.setAttribute("loggedInUser", user);

            // ரோல் பேஸ்டு ரீடைரக்ஷன் (Role Based Redirect)
            if ("RESTAURANT_OWNER".equals(user.getRole())) {
                return "redirect:/owner/dashboard";
            }
            return "redirect:/home"; // கஸ்டமரா இருந்தா ஹோம் பேஜ்

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/login";
        }
    }

    // 5. லாக் அவுட் செய்யும்போது
    // [FIXED]: 404 எர்ரரைத் தவிர்க்க Relative பாத் மாற்றப்பட்டு, சரியான Absolute பாத் (/login) கொடுக்கப்பட்டுள்ளது
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // செஷனை முழுமையாக காலி பண்றோம்
        return "redirect:/login?logout"; // நேராக முதன்மை லாகின் URL-க்கு ரீடைரெக்ட் செய்கிறது
    }
}