package by.cryptic.springmarket.service;

import by.cryptic.springmarket.dto.*;
import by.cryptic.springmarket.mapper.CategoryMapper;
import by.cryptic.springmarket.mapper.FullProductMapper;
import by.cryptic.springmarket.mapper.ProductMapper;
import by.cryptic.springmarket.model.Product;
import by.cryptic.springmarket.repository.CategoryRepository;
import by.cryptic.springmarket.repository.ProductRepository;
import by.cryptic.springmarket.specification.ProductSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final FullProductMapper fullProductMapper;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public Page<ProductDTO> getAllProducts(String name, String createdBy, String category,
                                           BigDecimal min, BigDecimal max, Integer page,
                                           Integer size, String sortBy, String order) {
        Specification<Product> spec = Specification.where(null);
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
        return productRepository.findAll(spec, PageRequest.of
                        (page - 1, size, Sort.by(Sort.Direction.fromString(order), sortBy)))
                .map(productMapper::toDto);
    }

    public FullProductDto getProductById(UUID id) {
        return fullProductMapper.toDto(productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id : %s".formatted(id))));
    }

    @Transactional
    public FullProductDto create(CreateProductDTO productDTO) {
        CategoryDTO category = categoryMapper.toDto(categoryRepository.findById(productDTO.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id : %s")));
        FullProductDto dto = FullProductDto.builder()
                .name(productDTO.name())
                .description(productDTO.description())
                .quantity(productDTO.quantity())
                .price(productDTO.price())
                .image(productDTO.image())
                .category(category)
                .build();
        productRepository.save(fullProductMapper.toEntity(dto));
        return dto;
    }

    public FullProductDto update(UUID id, UpdateProductDTO updateProductDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id : %s".formatted(id)));
        productMapper.updateEntity(product, updateProductDTO);
        return fullProductMapper.toDto(productRepository.save(product));

    }

    public void delete(UUID id) {
        productRepository.deleteById(id);
    }
}
