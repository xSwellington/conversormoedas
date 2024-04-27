package dev.swellingtonsoares.conversordemoedas.interfaces;

@FunctionalInterface
public interface CurrencyEventHandler <T> {
    void call(T result);
}
