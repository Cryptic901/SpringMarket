package by.cryptic.utils;

public interface CommandHandler<C> {
    void handle(C command) throws Exception;
}
