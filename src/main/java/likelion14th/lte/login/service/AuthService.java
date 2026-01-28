package likelion14th.lte.login.service;

import jakarta.transaction.Transactional;
import likelion14th.lte.login.dto.response.AuthResponse;
import likelion14th.lte.user.domain.User;
import likelion14th.lte.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UserRepository userRepository;


}
