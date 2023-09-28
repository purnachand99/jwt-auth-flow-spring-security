package com.behl.cerberus.filter;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.behl.cerberus.repository.UserRepository;
import com.behl.cerberus.utility.JwtUtility;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtility jwtUtils;
	private final UserRepository userRepository;
	
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";

	@Override
	@SneakyThrows
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
		final var authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

		if (StringUtils.isNotEmpty(authorizationHeader)) {
			if (authorizationHeader.startsWith(BEARER_PREFIX)) {
				final var token = authorizationHeader.replace(BEARER_PREFIX, StringUtils.EMPTY);
				final var userId = jwtUtils.extractUserId(token);
				final var isTokenValid = jwtUtils.validateToken(token, userId);
				
				if (Boolean.TRUE.equals(isTokenValid)) {
					final var user = userRepository.findById(userId).orElseThrow(IllegalStateException::new);
					final var authentication = new UsernamePasswordAuthenticationToken(
							user.getEmailId(), user.getPassword(), List.of());
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			}
		}
		filterChain.doFilter(request, response);
	}

}