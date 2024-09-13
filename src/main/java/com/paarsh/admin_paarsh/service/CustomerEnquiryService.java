// CustomerEnquiryService.java
package com.paarsh.admin_paarsh.service;

import com.paarsh.admin_paarsh.model.CustomerEnquiry;
import com.paarsh.admin_paarsh.model.User;
import com.paarsh.admin_paarsh.repository.CustomerEnquiryRepository;
import com.paarsh.admin_paarsh.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerEnquiryService {

    private final CustomerEnquiryRepository customerEnquiryRepository;
    private final UserRepository userRepository;

    @Autowired
    public CustomerEnquiryService(CustomerEnquiryRepository customerEnquiryRepository, UserRepository userRepository) {
        this.customerEnquiryRepository = customerEnquiryRepository;
        this.userRepository = userRepository;
    }

    // Save a new customer enquiry
    public CustomerEnquiry saveEnquiry(CustomerEnquiry enquiry) {
        enquiry.setCreatedAt(new java.util.Date());
        return customerEnquiryRepository.save(enquiry);
    }

    // Get all customer enquiries
    public List<CustomerEnquiry> getAllEnquiries() {
        return customerEnquiryRepository.findAll();
    }

    // Get enquiries by user ID
    public Object getEnquiriesByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(customerEnquiryRepository::findByUser).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    // Find a specific enquiry by ID
    public Optional<CustomerEnquiry> getEnquiryById(Long id) {
        return customerEnquiryRepository.findById(id);
    }

    // Delete an enquiry by ID
    public void deleteEnquiry(Long id) {
        customerEnquiryRepository.deleteById(id);
    }
}
