package dev.swellingtonsoares.conversordemoedas;

@FunctionalInterface
public interface CurrencyEventHandler <T> {
    void call(T result);
}
