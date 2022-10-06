package com.example.security.controller;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.security.dto.RoleToUser;
import com.example.security.entity.AppUser;
import com.example.security.entity.Role;
import com.example.security.service.ServiceUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
///@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class UserRessource {

    private ServiceUser serviceUser;

    public UserRessource(ServiceUser serviceUser) {
        this.serviceUser = serviceUser;
    }

    @RequestMapping(value = "/getallusers",method = RequestMethod.GET)
    @PostAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<List<AppUser>> getAlluser(){
        return  ResponseEntity.ok().body(serviceUser.getAllUser());
    }

    @RequestMapping(value = "addUser",method = RequestMethod.POST)
    @PostAuthorize("hasAuthority('SUPER-ADMIN')")
    public ResponseEntity<AppUser> saveUser(@RequestBody AppUser appUser){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("addUser").toUriString());
        return ResponseEntity.created(uri).body(serviceUser.addUser(appUser));
    }

    @RequestMapping(value = "addRole",method = RequestMethod.POST)
    public ResponseEntity<Role> saveRole(@RequestBody Role role){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("addRole").toUriString());
        return ResponseEntity.created(uri).body(serviceUser.addRole(role));
    }

    @RequestMapping(value = "save/role/touser",method = RequestMethod.GET)
    public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUser roleToUser){
        serviceUser.addRoleToUser(roleToUser.getUsername(),roleToUser.getRolename());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/refresh/token",method = RequestMethod.GET)
    public void Refreshtoken(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String authorizationHeader=request.getHeader(AUTHORIZATION);
        if (authorizationHeader !=null && authorizationHeader.startsWith("Bearer ")){
            try {
                String refresh_token=authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm=Algorithm.HMAC256("secret".getBytes());
                JWTVerifier jwtVerifier= JWT.require(algorithm).build();
                DecodedJWT decodedJWT=jwtVerifier.verify(refresh_token);
                String username=decodedJWT.getSubject();
                AppUser appUser=serviceUser.getUser(username);

                String access_token= JWT.create()
                        .withSubject(appUser.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis()+ 10*60*1000) )
                        .withIssuer(request.getRequestURI().toString())
                        .withClaim("roles",appUser.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                        .sign(algorithm);

                Map<String, String> tokens=new HashMap<>();
                tokens.put("access_token",access_token);
                tokens.put("refresh_token",refresh_token);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream() , tokens);

            }catch (Exception exception){
                response.setHeader("error",exception.getMessage());
                response.setStatus(FORBIDDEN.value());
                //response.sendError(FORBIDDEN.value());
                Map<String, String> error=new HashMap<>();
                error.put("error",exception.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream() , error);
            }

        }else {
            throw  new RuntimeException("the token is not valid");
        }
    }




}
