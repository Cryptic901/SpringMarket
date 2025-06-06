package by.cryptic.springmarket.service.query.handler;

public interface QueryHandler<C, Q> {
    Q handle(C command);
}
