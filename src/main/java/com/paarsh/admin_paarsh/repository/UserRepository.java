package com.paarsh.admin_paarsh.repository;

import com.paarsh.admin_paarsh.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
