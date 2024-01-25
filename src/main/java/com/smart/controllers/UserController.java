package com.smart.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	private static final int CONTACTS_PER_PAGE = 4;

	@Autowired
	private BCryptPasswordEncoder passw;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	//method for adding common data to the response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		//principal will give current logged in user name
		String userName = principal.getName();
		System.out.println(userName);
		
		//get user from db
		User user = this.userRepository.getUserByEmail(userName);
		System.out.println(user);
		model.addAttribute("user", user);
	}
	
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {	
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}
	
	//open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		
		return "normal/add_contact_form";
	}
	
	//handler for processing the contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,
								 @RequestParam("profileImage") MultipartFile multiPartFile,
								 Principal principal, HttpSession session) {
		try {
			String name = principal.getName();
			User user = this.userRepository.getUserByEmail(name);
			
			//check if file is empty
			if(multiPartFile.isEmpty()) {
				//if file is empty
				System.out.println("file is Empty");
				contact.setImage("Contact.png");
			}
			else 
			{
				handleImageUpload(contact, multiPartFile);

			}
			
			contact.setUser(user);
			
			user.getContacts().add(contact); //User store the contact 
			
			this.userRepository.save(user);
			System.out.println("Data :"+contact);
			System.out.println("Contact Image :"+contact.getImage().toString());
			System.out.println("Successfully added to database");
			
			//message success
			session.setAttribute("message", new Message("Your contact is added !! Add more ", "alert-success"));
		}catch(Exception e) {
			System.out.println("ERROR "+e.getMessage());
			e.printStackTrace();
			//Error Message
			session.setAttribute("message", new Message("Something went wrong !! Try again ", "alert-danger"));
		}
		return "normal/add_contact_form";
	}

	private void handleImageUpload(Contact contact, MultipartFile multiPartFile) throws IOException, IOException {
		String[] split = contact.getEmail().split("@");
		contact.setImage(split[0] + "_" + multiPartFile.getOriginalFilename());

		File fileObject = new ClassPathResource("static/img").getFile();
		Path path = Paths.get(fileObject.getAbsolutePath() + File.separator + contact.getImage());
		Files.copy(multiPartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		System.out.println("Image is uploaded");
	}
	
	//show contacts handler
	//per page = 5 contacts
	//current page = 0 [page] 
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
		model.addAttribute("title", "View Contacts");

		String name = principal.getName();
		User user = this.userRepository.getUserByEmail(name);

		Pageable pageable = PageRequest.of(page, CONTACTS_PER_PAGE);
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);

		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());

		return "normal/show_contacts";
	}


	//showing a Specific Contact details
	@RequestMapping(value = "/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cid, Model model, Principal principal) {
		model.addAttribute("title", "Contact Details");
		System.out.println("Cid : "+cid);
		Optional<Contact> optional = this.contactRepository.findById(cid);
		Contact contact = optional.get();
		//Contact contact = this.contactRepository.findById(cid).get();
		
		String name = principal.getName();
		User user = this.userRepository.getUserByEmail(name);
		if(user.getId()== contact.getUser().getId()) {
			model.addAttribute("contact", contact);
		}

		return "normal/contact_detail";
	}
	
	//delete Contact handler
	@RequestMapping(value = "/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cid, Model model, Principal principal, HttpSession session) {
		Optional<Contact> contactOptional = this.contactRepository.findById(cid);
		Contact contact = contactOptional.get();
		
		String name = principal.getName();
		User user = this.userRepository.getUserByEmail(name);
		if(user.getId()==contact.getUser().getId()) {
			//assignment remove img
			//contact.getImage()
			user.getContacts().remove(contact);
			//contact.setUser(null);
			//this.contactRepository.delete(contact);
			this.contactRepository.deleteById(cid);
			session.setAttribute("message", new Message("Contact deleted Successfully...", "alert-success"));
//			return "redirect:/user/show-contacts/0";
		}
		return "redirect:/user/show-contacts/0";
	}
	// Open update form handler
	@PostMapping("/update-contact/{cId}")
	public String updateForm(@PathVariable("cId") Integer id, Model model) {
		model.addAttribute("title", "Update Contact");
		Optional<Contact> optional = this.contactRepository.findById(id);
		Contact contact = optional.get();
		model.addAttribute("contact", contact);
		return "normal/update_form";
	}
	
	//update Contact handler
	@RequestMapping(value = "/process-update" , method = RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact contact, Model model, HttpSession session, 
			@RequestParam("profileImage") MultipartFile multiPartFile, Principal principal) {
		try {
			//old Contact details
				Contact oldContactDetail = this.contactRepository.findById(contact.getCId()).get();
			if(!multiPartFile.isEmpty()) {
				String[] split = contact.getEmail().split("@");
				//file is not empty so update the file
				//delete old photo
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file = new File(deleteFile, oldContactDetail.getImage());
				file.delete();
				
				//update new photo
				File fileObject = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(fileObject.getAbsolutePath()+File.separator+split[0]+"_"+multiPartFile.getOriginalFilename());
				Files.copy(multiPartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(split[0]+"_"+multiPartFile.getOriginalFilename());
			
			}
			else
			{
				//file is empty
				contact.setImage(oldContactDetail.getImage());
			}
			User user = this.userRepository.getUserByEmail(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
			session.setAttribute("message",new Message("Your Contact is updated.......","alert-success"));
			System.out.println("Id :"+contact.getCId());
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/user/"+contact.getCId()+"/contact";
	}
	
	//profile page handler
	@GetMapping("/profile")
	public String yourProfilePage(Model model) {
		model.addAttribute("title", "Profile Page");
		return "normal/profile";
	}

	@RequestMapping("/settings")
	public String setting(Model model) {
		model.addAttribute("title", "Settings");
		return "normal/settings";
	}

	@PostMapping("/change-password")
	public String changePassword(Principal principal,@RequestParam("old") String oldp, @RequestParam("new") String newp, HttpSession session) {
		String name = principal.getName();
		User userbyUserName = this.userRepository.getUserByEmail(name);
		if(this.passw.matches(oldp, userbyUserName.getPassword())) {
			userbyUserName.setPassword(this.passw.encode(newp));
			this.userRepository.save(userbyUserName);
			session.setAttribute("message",new Message("Updated successfully","alert-success"));
		}
		else {
			session.setAttribute("message",new Message("Password not Matched","alert-danger"));
			return "redirect:/user/settings";
		}
		return "redirect:/user/index";
	}
}