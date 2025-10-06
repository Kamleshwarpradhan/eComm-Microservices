package com.app.ecomm.user.Services;


import com.app.ecomm.user.Model.Address;
import com.app.ecomm.user.Model.User;
import com.app.ecomm.user.Repository.UserRepository;
import com.app.ecomm.user.dto.AddressDTO;
import com.app.ecomm.user.dto.UserRequest;
import com.app.ecomm.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
//    private List<User> userList = new ArrayList<>();
    private final UserRepository userRepository;
    private final KeyCloakAdminService keyCloakAdminService;
//    private Long id = 0L;

    public List<UserResponse> fetchAllUsers(){
//        return userRepository.findAll();

        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }


    public void adduser(UserRequest userRequest){
//        id += 1;
//        user.setId(id);
//        userList.add(user);
          String token = keyCloakAdminService.getAdminAccessToken();
          String KeyCloakId =  keyCloakAdminService.createUser(token,userRequest);
          User user = new User();
          updateuserFromuserRequest(user,userRequest);
          user.setKeycloakId(KeyCloakId);
          keyCloakAdminService.assignRealmRoleToUser(userRequest.getUsername(),"USER",KeyCloakId);
          userRepository.save(user);
    }



    public Optional<UserResponse> fetchUser(String id) {

//        return userList.stream()
//                .filter(user -> user.getId().equals(id))
//                .findFirst();
        return userRepository.findById(String.valueOf(id))
                .map(this::mapToUserResponse);
    }

    public boolean updateUser(String id,UserRequest userRequest){
//        return userList.stream()
//                .filter(user->user.getId().equals(id))
//                .findFirst()
//                .map(existinguser -> {
//                    if(updatedUser.getFirstName()!=null && !updatedUser.getFirstName().isBlank()) existinguser.setFirstName(updatedUser.getFirstName());
//                    if(updatedUser.getLastName()!=null && !updatedUser.getLastName().isBlank())   existinguser.setLastName(updatedUser.getLastName());
//                    return true;
//                })
//                .orElse(false);
          return userRepository.findById(String.valueOf(id))
                  .map(existinguser -> {
                        updateuserFromuserRequest(existinguser,userRequest);
                        userRepository.save(existinguser);
                        return true;
                  })
                  .orElse(false);
    }


    private void updateuserFromuserRequest(User user, UserRequest userRequest) {
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());

        if(userRequest.getAddress() != null){
            Address address = new Address();
            address.setCity(userRequest.getAddress().getCity());
            address.setCountry(userRequest.getAddress().getCountry());
            address.setStreet(userRequest.getAddress().getStreet());
            address.setState(userRequest.getAddress().getState());
            address.setZipCode(userRequest.getAddress().getZipCode());

            user.setAddress(address);
        }

    }

    private UserResponse mapToUserResponse(User user){
        UserResponse response = new UserResponse();

        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        response.setId(user.getId());

        if(user.getAddress() != null){
            AddressDTO addressresponse = new AddressDTO();

            addressresponse.setCity(user.getAddress().getCity());
            addressresponse.setCountry(user.getAddress().getCountry());
            addressresponse.setStreet(user.getAddress().getStreet());
            addressresponse.setZipCode(user.getAddress().getZipCode());
            addressresponse.setState(user.getAddress().getState());
            response.setAddress(addressresponse);
        }
        return response;
    }
}
