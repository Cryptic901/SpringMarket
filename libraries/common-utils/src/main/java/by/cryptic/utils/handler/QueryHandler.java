package by.cryptic.utils.handler;

public interface QueryHandler<C, Q> {
    Q handle(C command);
}
