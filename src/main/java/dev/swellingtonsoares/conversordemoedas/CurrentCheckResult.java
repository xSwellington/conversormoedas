package dev.swellingtonsoares.conversordemoedas;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CurrentCheckResult implements ICurrentResultInfo {

    private final DataResult result;
    private final double value;
    private final SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

    CurrentCheckResult(DataResult result, double currentValue) {
        this.result = result;
        this.value = currentValue;
    }

    @Override
    public double getConversion() {
        return result.conversion_rate();
    }

    @Override
    public String getSourceCurrencyCode() {
        return result.base_code();
    }

    @Override
    public String getSourceCurrentyName() {
        return APIUtils.getValidCurrencies().get(result.base_code());
    }

    @Override
    public String getTargetCurrencyCode() {
        return result.target_code();
    }

    @Override
    public String getTargetCurrentName() {
        return APIUtils.getValidCurrencies().get(result.target_code());
    }

    @Override
    public String getLastConversionUpdateDate() {
        return fmt.format(new Date(result.time_next_update_unix() * 1000));
    }

    @Override
    public String getCheckDate() {
        return fmt.format(new Date());
    }

    @Override
    public String getTotalValue() {
        return String.format("%.3f", result.conversion_rate() * value);
    }

    
}
