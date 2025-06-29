package by.cryptic.productservice.service.query.product.handler;

import by.cryptic.productservice.dto.ProductDTO;
import by.cryptic.productservice.mapper.ProductMapper;
import by.cryptic.productservice.model.read.ProductView;
import by.cryptic.productservice.repository.read.ProductViewRepository;
import by.cryptic.productservice.service.query.product.SortParamsQuery;
import by.cryptic.productservice.specification.ProductSpecification;
import by.cryptic.utils.QueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"products"})
public class ProductGetAllQueryHandler implements QueryHandler<SortParamsQuery, Page<ProductDTO>> {

    private final ProductViewRepository productViewRepository;
    private final ProductMapper productMapper;

    public Page<ProductDTO> handle(SortParamsQuery dto) {
        Specification<ProductView> spec = (root, query,
                                           builder) -> null;
        String name = dto.name();
        String createdBy = dto.createdBy();
        String category = dto.category();
        BigDecimal min = dto.min();
        BigDecimal max = dto.max();
        Integer page = dto.page();
        Integer size = dto.size();
        String sortBy = dto.sortBy();
        String order = dto.order();
        if (name != null && !name.isBlank()) {
            spec = spec.and(ProductSpecification.hasName(name));
        }
        if (createdBy != null && !createdBy.isBlank()) {
            spec = spec.and(ProductSpecification.hasCreatedBy(createdBy));
        }
        if (category != null && !category.isBlank()) {
            spec = spec.and(ProductSpecification.hasCategory(category));
        }
        if (min != null && max != null && max.compareTo(min) > 0) {
            spec = spec.and(ProductSpecification.hasPriceBetween(min, max));
        }
        return productViewRepository.findAll(spec, PageRequest.of
                        (page - 1, size, Sort.by(Sort.Direction.fromString(order), sortBy)))
                .map(productMapper::toDto);
    }
}
