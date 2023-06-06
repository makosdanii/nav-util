package com.navutil.server.controllers.api;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navutil.server.config.utils.UserAuthorizationDetails;
import com.navutil.server.controllers.Controller;
import com.navutil.server.data.entities.Route;
import com.navutil.server.data.entities.User;
import com.navutil.server.data.repositories.RouteRepository;
import com.navutil.server.data.validation.NonValidatedOnPersistTime;
import com.navutil.server.services.UserService;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Validated
@RestController
@CrossOrigin
@RequestMapping(path = "/route")
public class RouteController extends Controller {
    private final UserService userService;
    private final RouteRepository routeRepository;

    private final ObjectMapper mapper = new ObjectMapper()
            .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);

    @Autowired
    public RouteController(UserService userService, RouteRepository routeRepository) {
        this.userService = userService;
        this.routeRepository = routeRepository;
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping
    public Iterable<Route> listRoutes() {
        return routeRepository.findAll();
    }

    @PreAuthorize("hasAuthority('admin') || principal.getId() == #userId")
    @PostMapping("/{userId}")
    public ResponseEntity<Route> addRoute(@PathVariable @Positive @P("userId") int userId,
                                          @RequestBody @Validated(NonValidatedOnPersistTime.class) Route route) {
        route.setId(0);
        route.setUserByUserId(userService.findUser(userId));
        if (route.getUserByUserId().getId() == 0)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        try {
            mapper.readTree(route.getRoute());
        } catch (JacksonException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Route is not a valid JSON");
        }

        return new ResponseEntity<>(routeRepository.save(route), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('admin', 'user')")
    @PutMapping("/{id}")
    public ResponseEntity<Route> updateRoute(@PathVariable @Positive @P("id") int id,
                                             @RequestBody @Validated(NonValidatedOnPersistTime.class) Route route) {
        Route old = routeRepository.findById(id).orElse(new Route());
        if (old.getId() == 0)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        try {
            mapper.readTree(route.getRoute());
        } catch (JacksonException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Route is not a valid JSON");
        }

        User user = userService.findUser(old.getUserByUserId().getId());
        UserAuthorizationDetails authenticated =
                (UserAuthorizationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() != authenticated.getId())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);


        route.setUserByUserId(user);

        return new ResponseEntity<>(routeRepository.save(route), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('admin', 'user')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoute(@PathVariable @Positive @P("id") int id) {
        Route old = routeRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        User user = userService.findUser(old.getUserByUserId().getId());
        UserAuthorizationDetails authenticated =
                (UserAuthorizationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() != authenticated.getId())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        routeRepository.deleteById(old.getId());
        return new ResponseEntity<>("Deleted route", HttpStatus.OK);
    }
}
