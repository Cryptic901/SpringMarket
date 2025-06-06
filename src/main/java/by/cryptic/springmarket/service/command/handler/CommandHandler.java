package by.cryptic.springmarket.service.command.handler;

public interface CommandHandler<C> {
    void handle(C command) throws Exception;
}
