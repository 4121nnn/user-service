package io.nuri.user_service.controller;

import io.nuri.user_service.dto.NewUserRequest;
import io.nuri.user_service.dto.OneTImeToken;
import io.nuri.user_service.dto.UserDto;
import io.nuri.user_service.security.CustomUserDetailsService;
import io.nuri.user_service.security.jwt.JwtService;
import io.nuri.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    @GetMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserBeEmail(@PathVariable String email){
        return userService.getUserByEmail(email);
    }


    @PostMapping("/auth")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody NewUserRequest userRequest){
        return userService.createUser(userRequest);
    }

    @PutMapping("/auth")
    @ResponseStatus(HttpStatus.OK)
    public String login(@RequestBody NewUserRequest userRequest){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userRequest.email()
                    , userRequest.password()));
        }catch (DisabledException | BadCredentialsException e){
            System.out.println("bad " + e.getMessage());
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(userRequest.email());
        return JwtService.generateToken(userDetails);
    }

    @PostMapping("/auth/oauth2/exchange")
    @ResponseStatus(HttpStatus.OK)
    public String exchangeOneTimeToken(@RequestBody OneTImeToken token){
        return userService.exchangeOneTimeToken(token.token());
    }

}
