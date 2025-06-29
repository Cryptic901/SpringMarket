package by.cryptic.productservice.specification;

import by.cryptic.productservice.model.read.ProductView;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    //Root<T> — позволяет получить доступ к атрибутам сущности.
    //
    //CriteriaQuery<?> — используется для настройки запроса (например, выборка, сортировка).
    //
    //CriteriaBuilder — предоставляет методы для создания условий, таких как сравнения, логические операции и т. д.

    public static Specification<ProductView> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("name"), name);
    }

    public static Specification<ProductView> hasCreatedBy(String createdBy) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("createdBy"), createdBy);
    }

    public static Specification<ProductView> hasCategory(String category) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("category").get("name"), category);
    }

    public static Specification<ProductView> hasPriceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("price"), min, max);
    }
}
