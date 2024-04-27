package dev.swellingtonsoares.conversordemoedas.model;

import dev.swellingtonsoares.conversordemoedas.interfaces.CurrencyEventHandler;

import java.security.InvalidParameterException;

public class Currency {

    private CurrencyEventHandler<Void> onStart;
    private CurrencyEventHandler<CurrentCheckResult> onFinish;
    private CurrencyEventHandler<String> onError;
    private String source;
    private String target;
    private Double value;

    public static Currency Builder() {
        return new Currency();
    }

    public Currency addSource(String source) {
        this.source = source;
        return this;
    }

    public Currency addTarget(String target) {
        this.target = target;
        return this;
    }

    public Currency addValue(Double s) {
        this.value = s;
        return this;
    }

    public Currency onStart(CurrencyEventHandler<Void> callback) {
        this.onStart = callback;
        return this;
    }

    public Currency onFinish(CurrencyEventHandler<CurrentCheckResult> callback) {
        this.onFinish = callback;
        return this;
    }

    public Currency onError(CurrencyEventHandler<String> callback) {
        this.onError = callback;
        return this;
    }

    public void build() {
        try {
            if (source == null || target == null || source.isBlank() || target.isBlank())
                throw new InvalidParameterException("Defina a moeda de ORIGEM e DESTINO.");

            if (this.onStart != null) {
                new Thread(() -> this.onStart.call(null)).start();
            }

            new Thread(() -> {
                try {
                    CurrentCheckResult result = APIUtils.MakeRequest(source, target, value);
                    if (this.onFinish != null) {
                        this.onFinish.call(result);
                    }
                } catch (Exception e) {
                    if (this.onError != null) this.onError.call(e.getMessage());
                }
            }).start();

        } catch (Exception e) {
            if (this.onError != null) this.onError.call(e.getMessage());
        }
    }
}




