package com.bank.vyom.dao;

import org.springframework.data.repository.CrudRepository;

import com.bank.vyom.model.security.Role;



public interface RoleDao extends CrudRepository<Role, Integer> {
    Role findByName(String name);
}
