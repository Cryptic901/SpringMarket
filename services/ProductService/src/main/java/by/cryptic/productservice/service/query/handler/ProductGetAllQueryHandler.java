package by.cryptic.productservice.service.query.handler;

import by.cryptic.productservice.mapper.ProductMapper;
import by.cryptic.productservice.model.read.ProductView;
import by.cryptic.productservice.service.query.SortParamsQuery;
import by.cryptic.utils.DTO.ProductDTO;
import by.cryptic.utils.QueryHandler;
import lombok.RequiredArgsConstructor;
import org.bson.types.Decimal128;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductGetAllQueryHandler implements QueryHandler<SortParamsQuery, Page<ProductDTO>> {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<ProductDTO> handle(SortParamsQuery dto) {
        List<Criteria> criteriaList = new ArrayList<>();
        String name = dto.name();
        String createdBy = dto.createdBy();
        String category = dto.category();
        BigDecimal min = dto.min();
        BigDecimal max = dto.max();
        int page = dto.page() - 1;
        int size = dto.size();
        String sortBy = dto.sortBy();
        String order = dto.order();

        if (name != null && !name.isBlank()) {
            criteriaList.add(Criteria.where("name").is(name));
        }
        if (createdBy != null && !createdBy.isBlank()) {
            criteriaList.add(Criteria.where("createdBy").is(createdBy));
        }
        if (category != null && !category.isBlank()) {
            criteriaList.add(Criteria.where("category").is(category));
        }
        if (min != null && max != null && max.compareTo(min) > 0) {
            criteriaList.add(Criteria.where("price").gte(new Decimal128(min)).lte(new Decimal128(max)));
        }

        Criteria combinedCriteria = new Criteria();
        if (!criteriaList.isEmpty()) {
            combinedCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        }

        Query query = new Query(combinedCriteria);
        Sort.Direction direction = Sort.Direction.fromString(order);
        query.with(Sort.by(direction, sortBy));
        query.skip((long) page * size).limit(size);

        List<ProductView> products = mongoTemplate
                .find(query, ProductView.class);
        long total = mongoTemplate.count(Query.of(query)
                .limit(0).skip(0), ProductView.class);
        List<ProductDTO> productDTOS = products.stream()
                .map(ProductMapper::toDto).toList();

        return new PageImpl<>(productDTOS, PageRequest.of(page, size), total);
    }
}
