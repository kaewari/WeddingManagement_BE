/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qltc.filters;

import com.qltc.components.JwtService;
import com.qltc.pojo.Permission;
import com.qltc.pojo.User;
import com.qltc.service.UserPermissionService;
import com.qltc.service.UserService;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

/**
 *
 * @author sonho
 */
public class JwtAuthenticationTokenFilter extends UsernamePasswordAuthenticationFilter {

    private final static String TOKEN_HEADER = "authorization";
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserPermissionService permissionService;
//    @Autowired
//    private UsersGroupService usersGroupService;

    @Override
    @SuppressWarnings("empty-statement")
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authToken = httpRequest.getHeader(TOKEN_HEADER);
        if (jwtService.validateTokenLogin(authToken)) {
            String name = jwtService.getNameFromToken(authToken);
            User user = userService.getUserByName(name);
            if (user != null) {
                boolean enabled = true;
                boolean accountNonExpired = true;
                boolean credentialsNonExpired = true;
                boolean accountNonLocked = true;
//                Object[] permissions = this.userPermissionRepo.getPermissionsByUserId(user.getId()).toArray();
//                Set<GrantedAuthority> authorities = new HashSet<>();
//                for (Object permission : permissions) {
//                    authorities.add(new SimpleGrantedAuthority(permission.toString()));
//                }
                Object[] permissions = this.userService.getPermissions(user.getId()).toArray();
                Set<GrantedAuthority> authorities = new HashSet<>();
                for (Object permission : permissions) {
                    authorities.add(new SimpleGrantedAuthority((String) permission));
                }

//                Set<GrantedAuthority> authorities = new HashSet<>();
//                authorities.add(new SimpleGrantedAuthority("USER"));
                UserDetails userDetail = new org.springframework.security.core.userdetails.User(name, user.getPassword(), enabled, accountNonExpired,
                        credentialsNonExpired, accountNonLocked, authorities);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetail,
                        null, userDetail.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }
}
