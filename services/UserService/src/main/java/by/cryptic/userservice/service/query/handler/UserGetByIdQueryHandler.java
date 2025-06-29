package by.cryptic.userservice.service.query.handler;

import by.cryptic.userservice.dto.UserDTO;
import by.cryptic.userservice.mapper.UserMapper;
import by.cryptic.userservice.repository.read.UserViewRepository;
import by.cryptic.utils.QueryHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"users"})
public class UserGetByIdQueryHandler implements QueryHandler<UUID, UserDTO> {

    private final UserMapper userMapper;
    private final UserViewRepository userViewRepository;

    @Cacheable(key = "'user:' + #id")
    public UserDTO handle(UUID id) {
        return userMapper.toDto(userViewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: %s".formatted(id))));
    }
}
