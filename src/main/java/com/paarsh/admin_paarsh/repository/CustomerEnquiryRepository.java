package com.paarsh.admin_paarsh.repository;

import com.paarsh.admin_paarsh.model.CustomerEnquiry;
import com.paarsh.admin_paarsh.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerEnquiryRepository extends JpaRepository<CustomerEnquiry,Long> {
    Object findByUser(User user);
}
