package com.good.physicalexercisesystem.security;

import com.good.physicalexercisesystem.entity.User;
import com.good.physicalexercisesystem.service.UserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 获取认证上下文中的用户类型
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() != null) {
            String userType = (String) authentication.getDetails();
            System.out.println("Checking user type: expected=" + userType + ", actual=" + user.getUserType());
            if (!user.getUserType().equals(userType)) {
                throw new BadCredentialsException("用户类型不匹配");
            }
        }

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            user.getEnabled() == 1,
            true,
            true,
            true,
            Collections.emptyList()
        );
    }
}
