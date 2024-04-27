package dev.swellingtonsoares.conversordemoedas.model;

import dev.swellingtonsoares.conversordemoedas.interfaces.ICurrentResultInfo;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HistoricItem {




    public final SimpleIntegerProperty id;
    private final SimpleDoubleProperty conversion;
    private final SimpleStringProperty sourceCurrencyCode;

    private final SimpleStringProperty sourceCurrentyName;

    private final SimpleStringProperty targetCurrencyCode;

    private final SimpleStringProperty targetCurrencyName;

    private final SimpleStringProperty checkDate;

    private final SimpleStringProperty lastConversionUpdateDate;

    private final SimpleStringProperty totalValue;


    public HistoricItem(
            int id,
            double conversion,
            String sourceCurrencyCode,
            String sourceCurrencyName,
            String targetCurrencyCode,
            String targetCurrencyName,
            String checkDate,
            String lastConversionUpdateDate,
            String totalValue
    ) {
        this.id = new SimpleIntegerProperty(id);
        this.conversion = new SimpleDoubleProperty(conversion);
        this.sourceCurrencyCode = new SimpleStringProperty(sourceCurrencyCode);
        this.sourceCurrentyName = new SimpleStringProperty(sourceCurrencyName);
        this.targetCurrencyCode = new SimpleStringProperty(targetCurrencyCode);
        this.targetCurrencyName = new SimpleStringProperty(targetCurrencyName);
        this.checkDate = new SimpleStringProperty(checkDate);
        this.lastConversionUpdateDate = new SimpleStringProperty(lastConversionUpdateDate);
        this.totalValue = new SimpleStringProperty(totalValue);
    }

    public HistoricItem(ICurrentResultInfo result, int id) {
        this(
                id,
                result.getConversion(),
                result.getSourceCurrencyCode(),
                result.getSourceCurrentyName(),
                result.getTargetCurrencyCode(),
                result.getTargetCurrentName(),
                result.getCheckDate(),
                result.getLastConversionUpdateDate(),
                result.getTotalValue()
        );
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public double getConversion() {
        return conversion.get();
    }


    public String getSourceCurrencyCode() {
        return sourceCurrencyCode.get();
    }


    public String getSourceCurrentyName() {
        return sourceCurrentyName.get();
    }


    public String getTargetCurrencyCode() {
        return targetCurrencyCode.get();
    }


    public String getTargetCurrentName() {
        return targetCurrencyName.get();
    }


    public String getLastConversionUpdateDate() {
        return lastConversionUpdateDate.get();
    }


    public String getCheckDate() {
        return checkDate.get();
    }


    public String getTotalValue() {
        return totalValue.get();
    }


    public String scapeSpecialCharacters(String data) {
        if (data == null) throw new IllegalArgumentException("Input data cannot be null.");

        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    public String toCsv() {
        List<String> l = new ArrayList<>();
        l.add(String.valueOf(id.get()));
        l.add(String.valueOf(conversion.get()));
        l.add(sourceCurrencyCode.get());
        l.add(sourceCurrentyName.get());
        l.add(targetCurrencyCode.get());
        l.add(targetCurrencyName.get());
        l.add(checkDate.get());
        l.add(lastConversionUpdateDate.get());
        l.add(totalValue.get());
        return Stream.of(l.toArray(String[]::new)).map(this::scapeSpecialCharacters).collect(Collectors.joining("#"));
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
                fields[6],
                fields[7],
                fields[8]
        );
    }

    @Override
    public String toString() {
        return toCsv();
    }
}
