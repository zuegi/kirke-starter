package ch.wesr.starter.kirkespringbootstarter.gateway;

public class NoAnnotatedMethodFoundException extends RuntimeException {
    public NoAnnotatedMethodFoundException(String s) {
        super(s);
    }
}
