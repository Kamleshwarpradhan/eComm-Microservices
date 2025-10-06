package com.app.ecomm.user.controller;

import com.app.ecomm.user.Services.UserService;
import com.app.ecomm.user.dto.UserRequest;
import com.app.ecomm.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor // Used to make all the final variable initialize using constructor
@RequestMapping("/api/users")
public class UserController {

    // Other ways are we can use @Autowired or we can create manually the constructor
    private final UserService userService;
    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers(){
        List<UserResponse> userList = userService.fetchAllUsers();

        if(userList.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(userList,HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String id){
//        User user = userService.fetchUser(id);
//
//        if(user==null)
//        {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//
//        return new ResponseEntity<>(user,HttpStatus.OK);
//
        logger.info("Request recieved for user: {}",id);
        return userService.fetchUser(id)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable String id,@RequestBody UserRequest userRequest){
        if(userService.updateUser(id,userRequest)){
            return new ResponseEntity<>("User updated successfully",HttpStatus.OK);
        }
        return new ResponseEntity<>("User not found",HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody UserRequest user){
        userService.adduser(user);
        return ResponseEntity.ok("user added successfully !!!");
    }
}
