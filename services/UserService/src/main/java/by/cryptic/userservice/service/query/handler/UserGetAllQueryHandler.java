package by.cryptic.userservice.service.query.handler;

import by.cryptic.userservice.dto.UserDTO;
import by.cryptic.userservice.mapper.UserMapper;
import by.cryptic.userservice.repository.read.UserViewRepository;
import by.cryptic.userservice.service.query.UserGetAllQuery;
import by.cryptic.utils.QueryHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"users"})
public class UserGetAllQueryHandler implements QueryHandler<UserGetAllQuery, List<UserDTO>> {

    private final UserViewRepository userViewRepository;
    private final UserMapper userMapper;

    public List<UserDTO> handle(UserGetAllQuery userGetAllQuery) {
        return userViewRepository.findAll().stream().map(userMapper::toDto).toList();
    }
}
