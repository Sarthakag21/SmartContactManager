package com.smart.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contact {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int cId;

	@NotBlank(message = "Name must not be empty.")
	@Column(nullable = false)
	private String name;

	@NotBlank(message = "Second name must not be empty.")
	@Column(nullable = false)
	private String secondName;

	@NotBlank(message = "Work must not be empty.")
	@Column(nullable = false)
	private String work;

	@NotBlank(message = "Email must not be empty.")
	@Column(nullable = false)
	private String email;

	@NotBlank(message = "Phone number must not be empty.")
	@Column(nullable = false, length = 10)
	private String phone;

	private String image;

	@NotBlank(message = "Description must not be empty.")
	@Column(length = 5000, nullable = false)
	private String description;

	@ManyToOne
	@JoinColumn(name = "id")
	private User user;
}
