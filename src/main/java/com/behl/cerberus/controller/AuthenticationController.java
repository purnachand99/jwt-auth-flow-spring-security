package com.behl.cerberus.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.behl.cerberus.dto.RefreshTokenRequestDto;
import com.behl.cerberus.dto.TokenSuccessResponseDto;
import com.behl.cerberus.dto.UserLoginRequestDto;
import com.behl.cerberus.service.AuthenticationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
public class AuthenticationController {

	private final AuthenticationService authenticationService;

	@PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Logs in user into the system", description = "Returns Access-token and Refresh-token on successfull authentication which provides access to protected endpoints")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Authentication successfull"),
			@ApiResponse(responseCode = "401", description = "Bad credentials provided. Failed to authenticate user") })
	public ResponseEntity<TokenSuccessResponseDto> login(@Valid @RequestBody final UserLoginRequestDto userLoginRequest) {
		final var tokenResponse = authenticationService.login(userLoginRequest);
		return ResponseEntity.ok(tokenResponse);
	}

	@PutMapping(value = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Refreshes Access-Token for a user", description = "Provides a new Access-token against the user for which the non expired refresh-token is provided")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Access-token refreshed"),
			@ApiResponse(responseCode = "403", description = "Refresh token has expired. Failed to refresh access token") })
	public ResponseEntity<TokenSuccessResponseDto> refreshToken(@Valid @RequestBody final RefreshTokenRequestDto refreshTokenRequest) {
		final var tokenResponse = authenticationService.refreshToken(refreshTokenRequest);
		return ResponseEntity.ok(tokenResponse);
	}
	
}
