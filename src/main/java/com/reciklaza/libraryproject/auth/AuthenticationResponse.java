package com.reciklaza.libraryproject.auth;

import lombok.Builder;

@Builder
public record AuthenticationResponse(String token, String message) {
}
