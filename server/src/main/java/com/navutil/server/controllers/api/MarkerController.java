package com.navutil.server.controllers.api;

import com.navutil.server.config.utils.UserAuthorizationDetails;
import com.navutil.server.controllers.Controller;
import com.navutil.server.data.entities.Marker;
import com.navutil.server.data.entities.User;
import com.navutil.server.data.repositories.MarkerRepository;
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
@RequestMapping(path = "/marker")
public class MarkerController extends Controller {
    private final UserService userService;
    private final MarkerRepository markerRepository;

    @Autowired
    public MarkerController(UserService userService, MarkerRepository markerRepository) {
        this.userService = userService;
        this.markerRepository = markerRepository;
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping
    public Iterable<Marker> listMarkers() {
        return markerRepository.findAll();
    }

    @PreAuthorize("hasAuthority('admin') || principal.getId() == #userId")
    @PostMapping("/{userId}")
    public ResponseEntity<Marker> addMarker(@PathVariable @Positive @P("userId") int userId,
                                            @RequestBody @Validated(NonValidatedOnPersistTime.class) Marker marker) {
        marker.setId(0);
        marker.setUserByUserId(userService.findUser(userId));
        if (marker.getUserByUserId().getId() == 0)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(markerRepository.save(marker), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('admin', 'user')")
    @PutMapping("/{id}")
    public ResponseEntity<Marker> updateMarker(@PathVariable @Positive @P("id") int id,
                                               @RequestBody @Validated(NonValidatedOnPersistTime.class) Marker marker) {
        Marker old = markerRepository.findById(id).orElse(new Marker());
        if (old.getId() == 0)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        User user = userService.findUser(old.getUserByUserId().getId());
        UserAuthorizationDetails authenticated =
                (UserAuthorizationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() != authenticated.getId())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        marker.setUserByUserId(user);

        return new ResponseEntity<>(markerRepository.save(marker), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('admin', 'user')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMarker(@PathVariable @Positive @P("id") int id) {
        Marker old = markerRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        User user = userService.findUser(old.getUserByUserId().getId());
        UserAuthorizationDetails authenticated =
                (UserAuthorizationDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() != authenticated.getId())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        markerRepository.deleteById(old.getId());
        return new ResponseEntity<>("Deleted marker", HttpStatus.OK);
    }
}
