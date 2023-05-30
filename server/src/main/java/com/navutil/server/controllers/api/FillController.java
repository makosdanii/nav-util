package com.navutil.server.controllers.api;

import com.navutil.server.controllers.Controller;
import com.navutil.server.data.entities.*;
import com.navutil.server.exceptions.AlreadyExistsException;
import com.navutil.server.services.*;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Accepts GET request, with authorization Header. Its only purpose is to fill up DB with test data.
 * So it should only be called once after DB initialization.
 */
@RestController
@CrossOrigin
@RequestMapping("/fill-data")
@PreAuthorize("hasAuthority('admin')")
public class FillController extends Controller {
    private final UserService userService;

    @Autowired
    public FillController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/basic")
    public User basic() throws AlreadyExistsException {
        User user = new User("john.smith@example.com", "John Smith", "verysecret", new Role(1, "admin"));
        userService.createUser(user);
        return user;
    }
}
