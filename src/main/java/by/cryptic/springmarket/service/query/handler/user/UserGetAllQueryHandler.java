package by.cryptic.springmarket.service.query.handler.user;

import by.cryptic.springmarket.dto.UserDTO;
import by.cryptic.springmarket.mapper.UserMapper;
import by.cryptic.springmarket.repository.read.UserViewRepository;
import by.cryptic.springmarket.service.query.UserGetAllQuery;
import by.cryptic.springmarket.service.query.handler.QueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;

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
