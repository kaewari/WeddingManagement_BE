/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qltc.controller;

import com.qltc.components.JwtService;
import com.qltc.pojo.Permission;
import com.qltc.pojo.User;
import com.qltc.repository.UserPermissionRepository;
import com.qltc.service.UserService;
import java.security.Principal;
import java.util.List;
import org.cloudinary.json.JSONException;
import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author sonho
 */
@RestController
@RequestMapping("/api")
public class ApiUserController {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder passEncoder;
    @Autowired
    private JSONObject message;
    @Autowired
    private UserPermissionRepository userRepository;

    @PostMapping(path = "/login/", produces = {MediaType.APPLICATION_JSON_VALUE})
    @CrossOrigin
    public ResponseEntity<Object> login(@RequestBody User user) {
        try {
            if (!this.userService.authUser(user.getName(), user.getPassword())) {
                return new ResponseEntity<>("Wrong name or password", HttpStatus.BAD_REQUEST);
            } else {
                String token = this.jwtService.generateTokenLogin(user.getName());
                JSONObject resToken = new JSONObject();
                resToken.put("access_token", token);
                return new ResponseEntity<>(resToken.toString(), HttpStatus.OK);
            }
        } catch (JSONException e) {
            message.put("Msg", "Cannot login");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/test/{id}")
    @CrossOrigin
    public ResponseEntity<List<Permission>> test(@PathVariable int id, Principal principal) {
        return new ResponseEntity<>(this.userRepository.getPermissionsByUserId(id), HttpStatus.OK);
    }

    @PostMapping(path = "/user/",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @CrossOrigin
    public ResponseEntity<Object> addUser(@ModelAttribute User u, @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            if (this.userService.findUserInfo("name", u.getName())) {
                message.put("Msg", "This name existed");
                return new ResponseEntity<>(message.toString(), HttpStatus.CONFLICT);
            }
            if (this.userService.findUserInfo("email", u.getEmail())) {
                message.put("Msg", "This email existed");
                return new ResponseEntity<>(message.toString(), HttpStatus.CONFLICT);
            }
            if (this.userService.findUserInfo("phone", u.getPhone())) {
                message.put("Msg", "This phone number existed");
                return new ResponseEntity<>(message.toString(), HttpStatus.CONFLICT);
            }
            if (this.userService.findUserInfo("identityNumber", u.getIdentityNumber())) {
                message.put("Msg", "This identity number existed");
                return new ResponseEntity<>(message.toString(), HttpStatus.CONFLICT);
            }

            User isUser = this.userService.addUser(u, file);
            if (isUser != null) {
//                message.put("Msg", "Create user successfully");
                return new ResponseEntity<>(isUser, HttpStatus.CREATED);
            } else {
                message.put("Msg", "Create user failure");
                return new ResponseEntity<>(message.toString(), HttpStatus.CONFLICT);

            }
        } catch (JSONException e) {
            message.put("Msg", "Cannot create user");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/users/update/{id}/",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @CrossOrigin
    public ResponseEntity<Object> updateUser(@PathVariable int id, @ModelAttribute User u, @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            User user = this.userService.getUserById(id);
            if (user != null) {
                if (file != null) {
                    user.setFile(file);
                }
                if (u.getAddress() != null) {
                    user.setAddress(u.getAddress());
                }
                if (u.getPhone() != null) {
                    user.setPhone(u.getPhone());
                }
                if (u.getEmail() != null) {
                    user.setEmail(u.getEmail());
                }
                if (u.getName() != null) {
                    user.setName(u.getName());
                }
                if (u.getPassword() != null) {
                    user.setPassword(this.passEncoder.encode(u.getPassword()));
                }
                if (u.getIdentityNumber() != null) {
                    user.setIdentityNumber(u.getIdentityNumber());
                }
                if (this.userService.updateUser(user)) {
//                    message.put("Msg", "Update user successfully");
                    return new ResponseEntity<>(this.userService.getUserById(id), HttpStatus.OK);
                } else {
                    message.put("Msg", "Update user Failure");
                    return new ResponseEntity<>(message.toString(), HttpStatus.CONFLICT);
                }

            } else {
                message.put("Msg", "Cannot find this user");
                return new ResponseEntity<>(message.toString(), HttpStatus.NOT_FOUND);
            }
        } catch (JSONException e) {
            message.put("Msg", "Cannot update this user");
            return new ResponseEntity<>(message.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/current-user/", produces = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin
    public ResponseEntity<Object> getCurrentUser(Principal pricipal) {
        try {
            User user = this.userService.getUserByName(pricipal.getName());
            if (user != null) {
                return new ResponseEntity<>(user, HttpStatus.OK);
            }
            message.put("Msg", "This user does not exist");
            return new ResponseEntity<>(message.toString(), HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            message.put("Msg", "Cannot get current user");
            return new ResponseEntity<>(message.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/users/")
    @CrossOrigin
    public ResponseEntity<Object> getUsers() {
        try {
            List<User> users = this.userService.getUsers();
            if (users != null) {
                return new ResponseEntity<>(users, HttpStatus.OK);
            }
            message.put("Msg", "Don't have any users");
            return new ResponseEntity<>(message.toString(), HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            message.put("Msg", "Cannot get users");
            return new ResponseEntity<>(message.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/users/name/{name}/", produces = {MediaType.APPLICATION_JSON_VALUE})
    @CrossOrigin
    public ResponseEntity<Object> getUserByName(@PathVariable(value = "name") String name) {
        try {
            User user = this.userService.getUserByName(name);
            if (user != null) {
                return new ResponseEntity<>(user, HttpStatus.OK);
            }
            message.put("Msg", "This user does not exist");
            return new ResponseEntity<>(message.toString(), HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            message.put("Msg", "Cannot get this user");
            return new ResponseEntity<>(message.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/users/id/{id}/", produces = {MediaType.APPLICATION_JSON_VALUE})
    @CrossOrigin
    public ResponseEntity<Object> getUserById(@PathVariable(value = "id") int id) {
        try {
            User user = this.userService.getUserById(id);
            if (user != null) {
                return new ResponseEntity<>(user, HttpStatus.OK);
            }
            message.put("Msg", "This user doesn not exist");
            return new ResponseEntity<>(message.toString(), HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            message.put("Msg", "Cannot get this user");
            return new ResponseEntity<>(message.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(path = "/users/delete/{id}/", produces = {MediaType.APPLICATION_JSON_VALUE})
    @CrossOrigin
    public ResponseEntity<String> deleteUserById(@PathVariable(value = "id") int id) {
        try {
            if (this.userService.deleteUserById(id)) {
                return new ResponseEntity<>("Delete Successfully", HttpStatus.OK);
            }
            message.put("Msg", "This user does not exist");
            return new ResponseEntity<>(message.toString(), HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>("Delete Failure", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}