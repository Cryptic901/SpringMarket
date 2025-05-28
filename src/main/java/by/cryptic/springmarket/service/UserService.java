package by.cryptic.springmarket.service;

import by.cryptic.springmarket.dto.UpdateUserDTO;
import by.cryptic.springmarket.dto.UserDTO;
import by.cryptic.springmarket.mapper.UserMapper;
import by.cryptic.springmarket.model.AppUser;
import by.cryptic.springmarket.repository.AppUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"users"})
public class UserService {
    private final AppUserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDTO> findAll() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    @Cacheable(key = "'user:' + #id")
    public UserDTO findById(UUID id) {
        return userMapper.toDto(userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: %s".formatted(id))));
    }

    @Transactional
    @CachePut(key = "'user:' + #id")
    public UserDTO update(UUID id, UpdateUserDTO userDTO) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: %s".formatted(id)));
        userMapper.updateEntity(user, userDTO);
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    @CacheEvict(key = "'user:' + #id")
    public void delete(UUID id) {
        userRepository.deleteById(id);
    }
}
