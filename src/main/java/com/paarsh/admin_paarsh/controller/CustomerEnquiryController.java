// CustomerEnquiryController.java
package com.paarsh.admin_paarsh.controller;

import com.paarsh.admin_paarsh.model.CustomerEnquiry;
import com.paarsh.admin_paarsh.service.CustomerEnquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/enquiries")
public class CustomerEnquiryController {

    private final CustomerEnquiryService customerEnquiryService;

    @Autowired
    public CustomerEnquiryController(CustomerEnquiryService customerEnquiryService) {
        this.customerEnquiryService = customerEnquiryService;
    }

    // Create a new enquiry
    @PostMapping
    public ResponseEntity<CustomerEnquiry> createEnquiry(@RequestBody CustomerEnquiry enquiry) {
        CustomerEnquiry savedEnquiry = customerEnquiryService.saveEnquiry(enquiry);
        return new ResponseEntity<>(savedEnquiry, HttpStatus.CREATED);
    }

    // Get all enquiries
    @GetMapping
    public ResponseEntity<List<CustomerEnquiry>> getAllEnquiries() {
        List<CustomerEnquiry> enquiries = customerEnquiryService.getAllEnquiries();
        return new ResponseEntity<>(enquiries, HttpStatus.OK);
    }

    // Get enquiries by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<Object> getEnquiriesByUserId(@PathVariable Long userId) {
        Object enquiries = customerEnquiryService.getEnquiriesByUserId(userId);
        return new ResponseEntity<>(enquiries, HttpStatus.OK);
    }

    // Get an enquiry by ID
    @GetMapping("/{id}")
    public ResponseEntity<CustomerEnquiry> getEnquiryById(@PathVariable Long id) {
        Optional<CustomerEnquiry> enquiry = customerEnquiryService.getEnquiryById(id);
        return enquiry.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Delete an enquiry by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnquiry(@PathVariable Long id) {
        customerEnquiryService.deleteEnquiry(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
