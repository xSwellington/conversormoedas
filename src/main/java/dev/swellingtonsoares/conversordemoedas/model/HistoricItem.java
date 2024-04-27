package dev.swellingtonsoares.conversordemoedas.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HistoricItem {


    public int getId() {
        return id.get();
    }
    public double getQuotation() {
        return quotation.get();
    }
    public String getSourceCurrencyCode() {
        return sourceCurrencyCode.get();
    }
    public String getSourceCurrencyName() {
        return sourceCurrencyName.get();
    }
    public String getTargetCurrencyCode() {
        return targetCurrencyCode.get();
    }
    public String getTargetCurrencyName() {
        return targetCurrencyName.get();
    }


    public long getCheckedDateTimestamp() {
        return checkedDateTimestamp.get();
    }

    public long getLastQuotaUpdateTimestamp() {
        return lastQuotaUpdateTimestamp.get();
    }

    public String getTotalValue() {
        return totalValue.get();
    }

    private final SimpleIntegerProperty id;
    private final SimpleDoubleProperty quotation;
    private final SimpleStringProperty sourceCurrencyCode;
    private final SimpleStringProperty sourceCurrencyName;
    private final SimpleStringProperty targetCurrencyCode;
    private final SimpleStringProperty targetCurrencyName;
    private final SimpleLongProperty checkedDateTimestamp;
    private final SimpleLongProperty lastQuotaUpdateTimestamp;
    private final SimpleStringProperty totalValue;

    private HistoricItem() {
        id = new SimpleIntegerProperty();
        quotation = new SimpleDoubleProperty();
        sourceCurrencyCode = new SimpleStringProperty();
        sourceCurrencyName = new SimpleStringProperty();
        targetCurrencyCode = new SimpleStringProperty();
        targetCurrencyName = new SimpleStringProperty();
        checkedDateTimestamp = new SimpleLongProperty();
        lastQuotaUpdateTimestamp = new SimpleLongProperty();
        totalValue = new SimpleStringProperty();
    }

    public HistoricItem(
            int id,
            double quotation,
            String sourceCurrencyCode,
            String sourceCurrencyName,
            String targetCurrencyCode,
            String targetCurrencyName,
            long checkedDateTimestamp,
            long lastQuotaUpdateTimestamp,
            String totalValue
    ) {
        this();
        this.id.set(id);
        this.quotation.set(quotation);
        this.sourceCurrencyCode.set(sourceCurrencyCode);
        this.sourceCurrencyName.set(sourceCurrencyName);
        this.targetCurrencyCode.set(targetCurrencyCode);
        this.targetCurrencyName.set(targetCurrencyName);
        this.checkedDateTimestamp.set(checkedDateTimestamp);
        this.lastQuotaUpdateTimestamp.set( lastQuotaUpdateTimestamp );
        this.totalValue.set( totalValue );

    }

    public HistoricItem(CurrentCheckResult dataResult, int id) {
        this(
            id,
            dataResult.getQuota(),
            dataResult.getSourceCurrencyCode(),
            dataResult.getSourceCurrencyName(),
            dataResult.getTargetCurrencyCode(),
            dataResult.getSourceCurrencyName(),
            dataResult.getCheckedDateTimestamp(),
            dataResult.getRawRequest().time_last_update_unix(),
            dataResult.getTotalValue()
        );

    }


    public String scapeSpecialCharacters(String data) {
        if (data == null) throw new IllegalArgumentException("Input data cannot be null.");

        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }

        if (escapedData.contains(",")) {
            escapedData = escapedData.replaceAll(",", ".");
        }

        return escapedData;
    }

    public static HistoricItem fromCsv(String data) {
        String[] fields = data.split("#");
        return new HistoricItem(
                Integer.parseInt(fields[0]),
                Double.parseDouble(fields[1]),
                fields[2],
                fields[3],
                fields[4],
                fields[5],
                Long.parseLong(fields[6]),
                Long.parseLong(fields[7]),
                fields[8]
        );
    }

    public String toCsv() {
        List<String> l = new ArrayList<>();
        l.add(String.valueOf(this.id.get()));
        l.add(String.valueOf(this.quotation.get()));
        l.add(String.valueOf(this.sourceCurrencyCode.get()));
        l.add(String.valueOf(this.sourceCurrencyName.get()));
        l.add(String.valueOf(this.targetCurrencyCode.get()));
        l.add(String.valueOf(this.targetCurrencyName.get()));
        l.add(String.valueOf(this.checkedDateTimestamp.get()));
        l.add(String.valueOf(this.lastQuotaUpdateTimestamp.get()));
        l.add(String.valueOf(this.totalValue.get()));
        return Stream.of(l.toArray(String[]::new)).map(this::scapeSpecialCharacters).collect(Collectors.joining("#"));
    }
}
