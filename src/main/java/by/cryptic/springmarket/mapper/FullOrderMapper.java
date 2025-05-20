package by.cryptic.springmarket.mapper;

import by.cryptic.springmarket.dto.FullOrderDTO;
import by.cryptic.springmarket.model.CustomerOrder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FullOrderMapper {
    CustomerOrder toEntity(FullOrderDTO fullOrderDTO);

    FullOrderDTO toDto(CustomerOrder customerOrder);
}
