package com.teachertransfer.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            boolean isRegistrationToken = false;
            try {
                isRegistrationToken = Boolean.TRUE.equals(jwtUtil.isRegistrationToken(jwt));
            } catch (Exception e) {
                // Ignore parsing errors
            }

            UserDetails userDetails = null;
            boolean isTokenValid = false;

            if (isRegistrationToken) {
                // For registration, we don't look up the user in DB
                isTokenValid = jwtUtil.validateToken(jwt, username);
                if (isTokenValid) {
                    userDetails = org.springframework.security.core.userdetails.User.builder()
                            .username(username)
                            .password("") // Pre-verified phone
                            .authorities("ROLE_PRE_REGISTERED")
                            .build();
                }
            } else {
                try {
                    userDetails = this.userDetailsService.loadUserByUsername(username);
                    isTokenValid = jwtUtil.validateToken(jwt, userDetails.getUsername());
                } catch (Exception e) {
                    // User not found or invalid token
                }
            }

            if (isTokenValid && userDetails != null) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                jwt, // Stash the token as credentials
                                userDetails.getAuthorities()
                        );
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}