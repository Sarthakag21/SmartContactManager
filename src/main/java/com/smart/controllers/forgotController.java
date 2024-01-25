package com.smart.controllers;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.service.emailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Random;

@Controller
public class forgotController {

    @Autowired
    private emailService emailService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    Random rand=new Random();

    @GetMapping("/forget")
    public String openEmailForm(Model model) {
        model.addAttribute("title", "Forgot Password");
        return "forget_email_form";
    }

    @PostMapping("/sendOtp")
    public String sendOtp(@RequestParam("email") String email, HttpSession session, Model model) {
        model.addAttribute("title", "Verify OTP");
        int otp=rand.nextInt(899999)+100000;
        System.out.println(email);
        System.out.println(otp);
//        System.out.println(otp);
        String msg= "<div style = 'border:1px solid #e2e2e2; padding:20px'>"
                +"<h1>"
                +"OTP is "
                +"<b>"+otp
                +"</b>"
                +"</h1>"
                +"</div>";
        String sub="OTP FROM Smart Contact Manager";
        String to=email;
//        String from="sarthakag2002@gmail.com";
        boolean flag= emailService.sendEmail(msg, sub,to);
        if(flag) {
            session.setAttribute("myotp",otp);
            session.setAttribute("email", email);
            return "verify_otp";
        }
        else {
            session.setAttribute("message", "Check your mail");
            return "forget_email_form";
        }
    }

    @PostMapping("/verify-otp")
    public String verify(@RequestParam("otp") int otp,HttpSession session, Model model) {
        model.addAttribute("title", "Change Password");
        int myotp = (int) session.getAttribute("myotp");
        String mail=(String) session.getAttribute("email");
        if(myotp==otp) {
            User user=this.userRepository.getUserByEmail(mail);
            if(user==null) {
                session.setAttribute("message",new Message("You are not a registered user", "danger"));
                return "forget_email_form";
            }
            return "password_change_form";
        }
        else {
            session.setAttribute("message",new Message("You have entered wrong otp", "danger"));
            return "verify_otp";
        }
    }

    @GetMapping("/change-password")
    public String chngepass(@RequestParam("password") String password,HttpSession session)
    {
        User user=this.userRepository.getUserByEmail((String)session.getAttribute("email"));
        user.setPassword(this.bCryptPasswordEncoder.encode(password));
        this.userRepository.save(user);
        session.setAttribute("message",new Message("Password change successfully", "success"));
        return "redirect:/signin?change=password changed successfully";
    }
}