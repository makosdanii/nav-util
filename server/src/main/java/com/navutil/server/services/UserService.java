package com.navutil.server.services;


import com.navutil.server.config.utils.UserAuthorizationDetails;
import com.navutil.server.data.repositories.RoleRepository;
import com.navutil.server.data.repositories.UserRepository;
import com.navutil.server.data.entities.Role;
import com.navutil.server.data.entities.User;
import com.navutil.server.exceptions.AlreadyExistsException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class UserService extends ServiceORM<User> implements UserDetailsService {
    UserRepository userRepository;
    RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * method used by authentication manager to retrieve users stored in DB and also called when setting context manually
     *
     * @param email identifier
     * @return object containing the details
     * @throws UsernameNotFoundException when identified entity not found
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        List<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) throw new UsernameNotFoundException(email);

        List<GrantedAuthority> authority = List.of(
                new SimpleGrantedAuthority(user.get(0).getRoleByRoleId().getName().toLowerCase()));
        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        return new UserAuthorizationDetails(
                user.get(0).getId(),
                user.get(0).getEmail(),
                user.get(0).getPassword(),
                enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
                authority);
    }

    public User createUser(User user) throws AlreadyExistsException {
        checkConstraint(user, true);
        user.setId(UNASSIGNED);
        user.setPassword(passwordEncoder.encode(user.getPassword().trim()));
        return userRepository.save(user);
    }

    public User findUser(int id) {
        return userRepository.findById(id).orElse(new User());
    }

    public User findAdmin() {
        try {
            return userRepository.findByRoleByRoleId(roleRepository
                            .findByName("admin").get(0))
                    .get(0);
        } catch (IndexOutOfBoundsException e) {
            return new User();
        }
    }

    public Iterable<User> listAll() {
        return userRepository.findAll();
    }

    public User updateUser(int id, User user) throws AlreadyExistsException {
        User old = userRepository.findById(id).orElse(new User());
        if (old.getId() != UNASSIGNED) {
            checkConstraint(user, !old.getEmail().equals(user.getEmail()));

            old.setEmail(user.getEmail());
            old.setPassword(passwordEncoder.encode(user.getPassword().trim()));
            old.setName(user.getName());
            old.setRoleByRoleId(user.getRoleByRoleId());
            return userRepository.save(old);
        }
        return old;
    }

    public boolean deleteUser(int id) {
        if (!userRepository.existsById(id)) {
            return false;
        }

        userRepository.deleteById(id);
        return true;
    }

    @Override
    public void checkConstraint(User user, boolean notExistYet) throws AlreadyExistsException {
        if (notExistYet && !userRepository.findByEmail(user.getEmail()).isEmpty()) {
            throw new AlreadyExistsException();
        }

        Role role;
        //if there's no authenticated user then only user user can be created
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String anonymous
                && anonymous.equals("anonymousUser")) {
            try {
                role = roleRepository.findByName("user").get(0);
            } catch (IndexOutOfBoundsException e) {
                throw new ConstraintViolationException("Necessary user role is yet to be created", new HashSet<>());
            }
            user.setRoleByRoleId(role);
        } else if (SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().contains(new SimpleGrantedAuthority("admin"))) {
            role = roleRepository.findById(user.getRoleByRoleId().getId()).orElseThrow(() ->
                    new ConstraintViolationException("Invalid ID", new HashSet<>()));
            user.setRoleByRoleId(role);
        } else {
            role = roleRepository.findById(user.getRoleByRoleId().getId()).orElseThrow(() ->
                    new ConstraintViolationException("Invalid ID", new HashSet<>()));
            GrantedAuthority sufficientAuthentication = new SimpleGrantedAuthority(role.getName());

            if (!SecurityContextHolder.getContext().getAuthentication()
                    .getAuthorities().contains(sufficientAuthentication)) {
                throw new ConstraintViolationException("Forbidden role modification", new HashSet<>());
            } else {
                user.setRoleByRoleId(role);
            }
        }
    }

}
