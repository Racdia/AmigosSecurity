package com.example.security.service;

import com.example.security.entity.AppUser;
import com.example.security.entity.Role;
import com.example.security.repository.RoleRepository;
import com.example.security.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Transactional
@Slf4j
public class ServiceUserImpl implements ServiceUser , UserDetailsService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    private PasswordEncoder passwordEncoder;


    public ServiceUserImpl(UserRepository userRepository, RoleRepository roleRepository,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder=passwordEncoder;
    }

    @Override
    public AppUser addUser(AppUser appUser) {
        log.info("save user in database");
        String pw=appUser.getPassword();
        appUser.setPassword(passwordEncoder.encode(pw));
        return userRepository.save(appUser);
    }

    @Override
    public Role addRole(Role role) {
        log.info(("save role in database"));
        return roleRepository.save(role);
    }

    @Override
    public List<AppUser> getAllUser() {
        log.info("get all user in databse");
        return userRepository.findAll();
    }

    @Override
    public List<Role> getAllRole() {
        log.info("get all role in databse");
        return roleRepository.findAll();
    }

    @Override
    public void addRoleToUser(String username, String name) {
        log.info("add role to user");
        AppUser user=userRepository.findByUsername(username);
        Role role=roleRepository.findByName(name);

        user.getRoles().add(role);
        userRepository.save(user);

    }

    @Override
    public AppUser getUser(String username) {
        log.info("get user by username");
        return userRepository.findByUsername(username);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser=userRepository.findByUsername(username);
        if (appUser==null){
            throw new IllegalStateException("user not found");
        }else {
            log.info("the username is {}", username);
        }

        Collection<GrantedAuthority> authorities=new ArrayList<>();

        appUser.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });
        return  new User(appUser.getUsername(),appUser.getPassword(),authorities);
    }

}
