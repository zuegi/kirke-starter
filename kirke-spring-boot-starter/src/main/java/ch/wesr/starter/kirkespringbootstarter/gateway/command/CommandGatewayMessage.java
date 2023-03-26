package ch.wesr.starter.kirkespringbootstarter.gateway.command;

public interface CommandGatewayMessage {
    String NO_WAY = "There is no Method annotated with CommnandHandler.class in any Class annotated with Aggregate.class";
    String TO_MANY = "There are too many annotated methods available - expecting just one";
}
