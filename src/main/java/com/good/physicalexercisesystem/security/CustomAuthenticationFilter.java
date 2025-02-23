package com.good.physicalexercisesystem.security;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.good.physicalexercisesystem.common.ApiResponse;
import com.good.physicalexercisesystem.dto.LoginDTO;
import com.good.physicalexercisesystem.entity.User;
import com.good.physicalexercisesystem.service.UserService;
import com.good.physicalexercisesystem.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.BadCredentialsException;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager,
                                   UserService userService,
                                   JwtUtils jwtUtils,
                                   ObjectMapper objectMapper) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.objectMapper = objectMapper;
        setFilterProcessesUrl("/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                             HttpServletResponse response) throws AuthenticationException {
        try {
            System.out.println("=== Authentication Request Details ===");
            System.out.println("Request URI: " + request.getRequestURI());
            System.out.println("Request Method: " + request.getMethod());
            System.out.println("Content Type: " + request.getContentType());

            String body = request.getReader().lines().collect(Collectors.joining());
            System.out.println("Request Body: " + body);

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            LoginDTO loginDTO = mapper.readValue(body, LoginDTO.class);
            System.out.println("Parsed LoginDTO: " + loginDTO);

            // 验证用户类型
            User user = userService.findByUsername(loginDTO.getUsername());
            if (user != null && !user.getUserType().equals(loginDTO.getUserType())) {
                throw new BadCredentialsException("用户类型不匹配");
            }

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
            authenticationToken.setDetails(loginDTO.getUserType());

            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException e) {
            System.err.println("Error reading authentication request: " + e.getMessage());
            e.printStackTrace();
            throw new AuthenticationServiceException("认证请求处理失败", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                         HttpServletResponse response,
                                         FilterChain chain,
                                         Authentication authResult) throws IOException {
        String username = authResult.getName();
        User user = userService.findByUsername(username);
        String token = jwtUtils.generateToken(user);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", user);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(response.getOutputStream(), ApiResponse.success("登录成功", result));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                           HttpServletResponse response,
                                           AuthenticationException failed) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String errorMessage = failed.getMessage();
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorMessage = "用户名或密码错误";
        }

        System.err.println("Authentication failed: " + errorMessage);
        failed.printStackTrace();

        objectMapper.writeValue(response.getOutputStream(), ApiResponse.error(401, errorMessage));
    }
}
