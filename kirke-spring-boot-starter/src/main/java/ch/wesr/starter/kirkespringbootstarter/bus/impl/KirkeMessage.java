package ch.wesr.starter.kirkespringbootstarter.bus.impl;

public record KirkeMessage(Class<?> source, Object payload) {
}
