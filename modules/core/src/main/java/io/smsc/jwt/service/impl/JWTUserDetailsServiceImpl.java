package io.smsc.jwt.service.impl;

import io.smsc.jwt.service.JWTUserDetailsService;
import io.smsc.model.admin.Authority;
import io.smsc.model.admin.Group;
import io.smsc.model.admin.Role;
import io.smsc.model.admin.User;
import io.smsc.repository.admin.UserRepository;
import io.smsc.jwt.model.JWTUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of base {@link JWTUserDetailsService} which loads user-specific data.
 *
 * @author Nazar Lipkovskyy
 * @since 0.0.1-SNAPSHOT
 */
@Component
public class JWTUserDetailsServiceImpl implements JWTUserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public JWTUserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Implementation of basic {@link UserDetailsService#loadUserByUsername(String)} method
     * which takes {@link User} entity by username from database and creates {@link JWTUser} with
     * appropriate parameters.
     *
     * @param username string which describes user name
     * @return appropriate {@link JWTUser} object
     */
    @Override
    public JWTUser loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        } else {
            return createJwtUser(user);
        }
    }

    /**
     * Additional method which takes {@link User} entity by email from database and creates
     * {@link JWTUser} with appropriate parameters.
     *
     * @param email string which describes user's email
     * @return appropriate {@link JWTUser} object
     */
    @Override
    public JWTUser loadUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with email '%s'.", email));
        } else {
            return createJwtUser(user);
        }
    }

    /**
     * Method to create set with {@link SimpleGrantedAuthority} for logged user. Authorities
     * are created from user's {@link Authority} , {@link Role} and authorities from {@link Group}.
     *
     * @param user logged {@link User}
     * @return appropriate {@link JWTUser} object
     */
    public static JWTUser createJwtUser(User user) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (null != user.getGroups() && !user.getGroups().isEmpty()) {
            authorities.addAll(getAuthoritiesFromGroups(user.getGroups()));
        }
        if (null != user.getAuthorities() && !user.getAuthorities().isEmpty()) {
            for (Authority authority : user.getAuthorities()) {
                authorities.add(new SimpleGrantedAuthority(authority.getName()));
            }
        }
        if (null != user.getRoles() && !user.getRoles().isEmpty()) {
            for (Role role : user.getRoles()) {
                authorities.add(new SimpleGrantedAuthority(role.getName()));
            }
        }
        return new JWTUser(user, authorities);
    }

    /**
     * Method to create set with {@link SimpleGrantedAuthority} from all user's groups.
     *
     * @param groups Collection with groups
     * @return Set with authorities
     */
    private static Collection<? extends GrantedAuthority> getAuthoritiesFromGroups(Collection<Group> groups) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Group group : groups) {
            if (null != group.getAuthorities() && !group.getAuthorities().isEmpty()) {
                for (Authority authority : group.getAuthorities()) {
                    authorities.add(new SimpleGrantedAuthority(authority.getName()));
                }
            }
        }
        return authorities;
    }
}
