package com.smart.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@NotBlank(message = "Name field is required.")
	@Size(min = 2, max = 25, message = "Name must be between 2 and 25 characters.")
	private String name;

	@Column(unique = true)
	@Email(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "Invalid email format.")
	private String email;

	@NotBlank(message = "Password must be provided.")
	@Size(min = 4, message = "Password must be at least 4 characters.")
	private String password;

	@Column(length = 15)
	private String role;

	private boolean enabled;
	private String imageUrl;

	@Column(length = 500)
	private String about;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
	private List<Contact> contacts;
}
