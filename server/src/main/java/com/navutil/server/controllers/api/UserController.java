package com.navutil.server.controllers.api;

import com.navutil.server.controllers.Controller;
import com.navutil.server.data.validation.NonValidatedOnPersistTime;
import com.navutil.server.exceptions.AlreadyExistsException;
import com.navutil.server.services.ServiceORM;
import com.navutil.server.services.UserService;
import com.navutil.server.data.entities.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@CrossOrigin
@RequestMapping(path = "/user")
public class UserController extends Controller {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAuthority('admin') || principal.getId() == #id")
    @GetMapping("/{id}")
    public ResponseEntity<User> findUser(@PathVariable @Positive @P("id") int id) {
        User user = userService.findUser(id);
        return new ResponseEntity<>(user, user.getId() == ServiceORM.UNASSIGNED ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping
    public Iterable<User> listUser() {
        return userService.listAll();
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody @Validated(NonValidatedOnPersistTime.class) User user) throws AlreadyExistsException {
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('admin') || principal.getId() == #id")
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable @Positive @P("id") int id, @RequestBody @Valid User user) throws AlreadyExistsException {
        user = userService.updateUser(id, user);
        return new ResponseEntity<>(user, user.getId() == ServiceORM.UNASSIGNED ? HttpStatus.NOT_FOUND : HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('admin') || principal.getId() == #id")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable @Positive @P("id") int id) {
        return new ResponseEntity<>("", userService.deleteUser(id) ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }
}
