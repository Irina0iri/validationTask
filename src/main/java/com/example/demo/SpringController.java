package com.example.demo;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

    @RestController
    public class SpringController {
        private static final Map<String, User> userRegistered = new HashMap<>();
        private static final Map<String, User> alreadyLogged = new HashMap<>();

        @PostMapping("/register")
        public User handleRegister(@Valid @RequestBody User user) {
            if(userRegistered.containsKey(user.getEmail())){
                throw new ValidationException("Email already exists!");
            } else {
                userRegistered.put(user.getEmail(), user);
            }
            return user;
        }

        @PostMapping("/login")
        public ResponseEntity<User> handleLogin(@RequestBody User user0) {
            if(!userRegistered.containsKey(user0.getEmail())){
                throw new ValidationException("This user does not exists.");
            } else if(!userRegistered.get(user0.getEmail()).getPassword().equals(user0.getPassword())){
                throw new ValidationException("Wrong Password");
            }
            if(alreadyLogged.containsKey(user0.getEmail())){
                return new ResponseEntity<>(user0,HttpStatus.ACCEPTED);
            }
            alreadyLogged.put(user0.getEmail(), userRegistered.get(user0.getEmail()));
            return new ResponseEntity<>(user0,HttpStatus.OK);

        }
        @GetMapping("/logout/{email}")
        public ResponseEntity<Map<String, User> > logout (@PathVariable String email ){
            if(alreadyLogged.containsKey(email)){
                alreadyLogged.remove(email);
                return new ResponseEntity<>(alreadyLogged,HttpStatus.OK);
            }else {
                return new ResponseEntity<>(alreadyLogged,HttpStatus.BAD_REQUEST);
            }
        }

        @ResponseStatus(HttpStatus.BAD_REQUEST)
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
            Map<String, String> errors = new HashMap<>();

            ex.getBindingResult().getAllErrors().forEach((error) -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });

            return errors;

        }
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        @ExceptionHandler(ValidationException.class)
        public Map<String, String> handleValidationEx(ValidationException ex) {
            Map<String, String> errors = new HashMap<>();
            errors.put("Validation exception: ", ex.getMessage());
            return errors;
        }

    }
