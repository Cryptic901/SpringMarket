package by.cryptic.springmarket.mapper;

import by.cryptic.springmarket.dto.FullOrderDTO;
import by.cryptic.springmarket.model.read.CustomerOrderView;
import by.cryptic.springmarket.model.write.CustomerOrder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FullOrderMapper {
    CustomerOrder toEntity(FullOrderDTO fullOrderDTO);

    FullOrderDTO toDto(CustomerOrderView customerOrder);
}
