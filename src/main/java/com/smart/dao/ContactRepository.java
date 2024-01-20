package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;
import com.smart.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

	// Pagination
	@Query("FROM Contact AS c WHERE c.user.id = :userId")
	Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable pageable);
	// Pageable carries two pieces of information: CurrentPage, Contacts Per Page

	List<Contact> findByNameContainingAndUser(String nameKeyword, User user);
	// Predefined way of searching
}
