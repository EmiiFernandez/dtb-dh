/*package dropthebass.equipo4.service.impl;

import dropthebass.equipo4.entity.RefreshToken;
import dropthebass.equipo4.repository.IRefreshTokenRepository;
import dropthebass.equipo4.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private IRefreshTokenRepository refreshTokenRepository;
    @Autowired
    private IUserRepository userRepository;

    public RefreshToken createRefreshToken(String email) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(userRepository.findByEmail(email).get())
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(7200000 ))//2 horas
                .build();
        return refreshTokenRepository.save(refreshToken);
    }


    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }


    public RefreshToken verifyExpiration(RefreshToken token) {
        if (Instant.now().compareTo(token.getExpiryDate()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    public void invalidateRefreshToken(String userEmail) {
        // Busca el refresh token asociado al usuario por su correo electrónico
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByUserEmail(userEmail);

        if (optionalRefreshToken.isPresent()) {
            RefreshToken refreshToken = optionalRefreshToken.get();

            // Elimina el refresh token de la base de datos (invalidación)
            refreshTokenRepository.delete(refreshToken);
        }
    }
}

*/