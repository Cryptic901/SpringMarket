package by.cryptic.userservice.service.handler;

import by.cryptic.userservice.dto.UserDTO;
import by.cryptic.userservice.mapper.UserMapper;
import by.cryptic.userservice.repository.read.UserViewRepository;
import by.cryptic.utils.QueryHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserGetByIdQueryHandler implements QueryHandler<UUID, UserDTO> {

    private final UserViewRepository userViewRepository;

    @Override
    @Cacheable(cacheNames = "users", key = "'user:' + #id")
    public UserDTO handle(UUID id) {
        return UserMapper.toDto(userViewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: %s".formatted(id))));
    }
}
