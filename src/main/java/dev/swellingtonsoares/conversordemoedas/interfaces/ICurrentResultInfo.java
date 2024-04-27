package dev.swellingtonsoares.conversordemoedas.interfaces;

public interface ICurrentResultInfo {
    double getConversion();
    String getSourceCurrencyCode();
    String getSourceCurrentyName();
    String getTargetCurrencyCode();
    String getTargetCurrentName();
    String getLastConversionUpdateDate();
    String getCheckDate();
    String getTotalValue();
}
