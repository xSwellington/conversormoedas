package dev.swellingtonsoares.conversordemoedas.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import static dev.swellingtonsoares.conversordemoedas.model.APIUtils.fmt;

public class CurrentCheckResult {

    private final DataResult result;
    private final double value;


    private final long checkDateTimestamp;

    CurrentCheckResult(DataResult result, double currentValue) {
        this.result = result;
        this.value = currentValue;
        checkDateTimestamp = new Date().getTime();
    }

    public double getQuota() {
        return result.conversion_rate();
    }

    public DataResult getRawRequest(){
        return result;
    }

    public String getSourceCurrencyCode() {
        return result.base_code();
    }

    public String getSourceCurrencyName() {
        return APIUtils.getValidCurrencies().get(result.base_code());
    }


    public String getTargetCurrencyCode() {
        return result.target_code();
    }


    public String getTargetCurrentName() {
        return APIUtils.getValidCurrencies().get(result.target_code());
    }

    public String getLastQuotaUpdatedFormatedDate() {
        return fmt.format(new Date(result.time_next_update_unix() * 1000));
    }

    public long getCheckedDateTimestamp() {
        return checkDateTimestamp;
    }



    public String getTotalValue() {
        return String.format("%.3f", result.conversion_rate() * value);
    }


}
