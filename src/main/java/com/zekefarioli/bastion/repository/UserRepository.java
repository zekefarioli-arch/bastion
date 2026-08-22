package com.zekefarioli.bastion.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zekefarioli.bastion.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
