package by.cryptic.userservice.service.handler;

import by.cryptic.userservice.dto.UserDTO;
import by.cryptic.userservice.mapper.UserMapper;
import by.cryptic.userservice.repository.read.UserViewRepository;
import by.cryptic.userservice.service.UserGetAllQuery;
import by.cryptic.utils.QueryHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserGetAllQueryHandler implements QueryHandler<UserGetAllQuery, List<UserDTO>> {

    private final UserViewRepository userViewRepository;

    @Override
    public List<UserDTO> handle(UserGetAllQuery userGetAllQuery) {
        List<UserDTO> result = userViewRepository.findAll().stream().map(UserMapper::toDto).toList();
        if (result.isEmpty()) {
            throw new EntityNotFoundException("There are no users :(");
        }
        return result;
    }
}
