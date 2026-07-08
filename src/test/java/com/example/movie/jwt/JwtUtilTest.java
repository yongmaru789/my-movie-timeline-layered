package com.example.movie.jwt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private static final String SECRET = "Rg3O4E2I+OvtbUH8IhqyKoTqyr9xm2BuNO+EkvQYQZs=";

    @Test
    @DisplayName("같은 secret으로 만든 서로 다른 JwtUtil 인스턴스끼리도 토큰을 검증할 수 있다")
    void tokenIsValidAcrossInstancesWithSameSecret() {
        // Given: 서버 재시작이나 다중 인스턴스 운영 상황을 흉내내기 위해
        // 같은 secret으로 별도의 JwtUtil 인스턴스 두 개를 생성
        JwtUtil issuer = new JwtUtil(SECRET);
        JwtUtil verifier = new JwtUtil(SECRET);

        // When: 한쪽 인스턴스에서 토큰을 발급
        String token = issuer.generateToken("testuser");

        // Then: 다른 인스턴스에서도 같은 토큰을 정상적으로 검증할 수 있어야 함
        assertThat(verifier.validateToken(token)).isTrue();
        assertThat(verifier.extractUsername(token)).isEqualTo("testuser");
    }
}
