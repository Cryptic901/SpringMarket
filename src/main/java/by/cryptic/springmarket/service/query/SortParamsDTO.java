package by.cryptic.springmarket.service.query;

import java.math.BigDecimal;

public record SortParamsDTO(String name, String createdBy, String category,
                            BigDecimal min, BigDecimal max, Integer page,
                            Integer size, String sortBy, String order) {
}
