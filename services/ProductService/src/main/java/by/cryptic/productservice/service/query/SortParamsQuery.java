package by.cryptic.productservice.service.query;

import java.math.BigDecimal;

public record SortParamsQuery(String name, String createdBy, String category,
                              BigDecimal min, BigDecimal max, Integer page,
                              Integer size, String sortBy, String order) {
}
