package com.example.security.service;


import com.example.security.entity.AppUser;
import com.example.security.entity.Role;

import java.util.List;

public interface ServiceUser {

    AppUser addUser(AppUser appUser);

    Role addRole(Role role);

    List<AppUser> getAllUser();

    List<Role> getAllRole();

    void addRoleToUser(String username,String rolename);

    AppUser getUser(String username);


}
