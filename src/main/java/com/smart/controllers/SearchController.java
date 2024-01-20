package com.smart.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

@RestController
public class SearchController {

	@Autowired
	private ContactRepository contactRepository;

	@Autowired
	private UserRepository userRepository;

	@GetMapping(value = "/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal) {
		try {
			String name = principal.getName();
			User user = this.userRepository.getUserByEmail(name);
			List<Contact> contacts = this.contactRepository.findByNameContainingAndUser(query, user);

			// Create a custom response DTO if needed
			// ContactResponseDTO responseDTO = createResponseDTO(contacts);

			return ResponseEntity.ok(contacts);
		} catch (Exception e) {
			// Handle exceptions and return an error response
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while processing the search request.");
		}
	}

	// Example method to create a custom response DTO if needed
	// private ContactResponseDTO createResponseDTO(List<Contact> contacts) {
	//     // Implement logic to create a custom DTO
	//     // return new ContactResponseDTO(...);
	// }

}
