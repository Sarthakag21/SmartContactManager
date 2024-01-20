package com.smart.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}

	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About - Smart Contact Manager");
		return "about";
	}

	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Signup - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}

	// Handler for registering user
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result,
							   @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
							   HttpSession session) {
		try {
			if (!agreement) {
				throw new Exception("You have not accepted the terms and conditions");
			}

			if (result.hasErrors()) {
				model.addAttribute("user", user);
				return "signup";
			}

			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setAbout(user.getAbout().trim());
			user.setImageUrl("default.png");
			// Password encode
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			User savedUser = this.userRepository.save(user);
			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Successfully Registered!", "alert-success"));
			return "signup";
		} catch (Exception e) {
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong: " + e.getMessage(), "alert-danger"));
			return "signup";
		}
	}

	// Handler for custom Login
	@GetMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title", "Login - Smart Contact Manager");
		return "login";
	}
}