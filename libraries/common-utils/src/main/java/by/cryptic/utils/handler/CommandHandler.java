package by.cryptic.utils.handler;

public interface CommandHandler<C> {
    void handle(C command);
}
