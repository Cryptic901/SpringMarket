package by.cryptic.utils;

public interface QueryHandler<C, Q> {
    Q handle(C command);
}
