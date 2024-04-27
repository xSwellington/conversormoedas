package dev.swellingtonsoares.conversordemoedas;

import dev.swellingtonsoares.conversordemoedas.model.APIUtils;
import dev.swellingtonsoares.conversordemoedas.model.Currency;
import dev.swellingtonsoares.conversordemoedas.model.HistoricItem;
import dev.swellingtonsoares.conversordemoedas.model.HistoricManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.Date;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private final ObservableList<String> currencies = FXCollections.observableArrayList();
    private final HistoricManager historyManager = HistoricManager.getInstance();
    private final StringConverter<String> converter = new StringConverter<>() {
        @Override
        public String toString(String s) {
            return APIUtils.getValidCurrencies().get(s);
        }

        @Override
        public String fromString(String s) {
            return null;
        }
    };

    private Comparator<HistoricItem> comparatorCheckedDate = (o1, o2) -> (int) ( o2.getCheckedDateTimestamp() - o1.getCheckedDateTimestamp());


    @FXML
    private Button btnCheck;

    @FXML
    private ComboBox<String> cbSourceCoin;

    @FXML
    private ComboBox<String> cbTargetCoin;

    @FXML
    private Label lbCotacao;

    @FXML
    private Label lbLastUpdate;

    @FXML
    private Label lbValorTotal;

    @FXML
    private TableColumn<HistoricItem, String> tbColumnSavedInfo;

    @FXML
    private TableColumn<HistoricItem, Integer> tbColumnId;

    @FXML
    private TableColumn<HistoricItem, String> tbColumnLastUpdate;

    @FXML
    private TableColumn<HistoricItem, String> tbColumnPrice;

    @FXML
    private TableColumn<HistoricItem, String> tbColumnSourceCoin;

    @FXML
    private TableColumn<HistoricItem, String> tbColumnTargetCoin;

    @FXML
    private TableView<HistoricItem> tbHistory;

    @FXML
    private TextField tfCoinValue;


    private void populateTableRow(HistoricItem history) {
        tbHistory.getItems().add(history);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("System started");
        currencies.addAll(APIUtils.getValidCurrencies().keySet().stream().sorted().toList());

        cbSourceCoin.setItems(currencies);
        cbTargetCoin.setItems(currencies);

        cbSourceCoin.setConverter(converter);
        cbTargetCoin.setConverter(converter);

        tfCoinValue.textProperty().addListener((observableValue, __, n) -> {
            try {
                btnCheck.setDisable(Double.parseDouble(n) <= 0);
                tfCoinValue.setStyle("-fx-background-color: white; -fx-text-fill: black");
            } catch (NumberFormatException e) {
                btnCheck.setDisable(true);
                tfCoinValue.setStyle("-fx-background-color: red; -fx-text-fill: white");
            }
        });

        btnCheck.setOnAction(actionEvent -> {

            Currency.Builder()
                    .addSource(cbSourceCoin.getValue())
                    .addTarget(cbTargetCoin.getValue())
                    .addValue(Double.parseDouble(tfCoinValue.textProperty().get()))
                    .onStart((__) -> {
                        Platform.runLater(() -> {
                            btnCheck.setDisable(true);
                            cbTargetCoin.setDisable(true);
                            cbSourceCoin.setDisable(true);
                            tfCoinValue.setDisable(true);
                            lbCotacao.setText("Cotação: Verificando...");
                            lbValorTotal.setText("Valor Total: Verificando...");
                            lbLastUpdate.setText("Última Atualização: Verificando...");
                        });

                    }).onFinish(result -> {

                        Task<Void> task = new Task<>() {
                            @Override
                            protected Void call() {
                                Platform.runLater(() -> {
                                    btnCheck.setDisable(false);
                                    cbTargetCoin.setDisable(false);
                                    cbSourceCoin.setDisable(false);
                                    tfCoinValue.setDisable(false);
                                    lbCotacao.setText(String.format("Cotação: %.4f %s ( %s )", result.getQuota(), result.getTargetCurrencyCode(), result.getTargetCurrencyCode()));
                                    lbValorTotal.setText(String.format("Valor total: %s %s ( %s )", result.getTotalValue(), result.getTargetCurrencyCode(), result.getTargetCurrentName()));
                                    lbLastUpdate.setText("Última Atualização: " + result.getLastQuotaUpdatedFormatedDate());
                                });
                                HistoricItem h = new HistoricItem(result, historyManager.getHistoricItemList().size() + 1);
                                historyManager.add(h);
                                populateTableRow(h);
                                return null;
                            }
                        };
                        new Thread(task).start();
                    }).onError(message -> {
                        Alert a = new Alert(Alert.AlertType.ERROR);
                        a.setTitle("Conversor de Moedas");
                        a.setContentText(message);
                        a.showAndWait();
                    }).build();
        });

        tbColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tbColumnSourceCoin.setCellValueFactory(new PropertyValueFactory<>("sourceCurrencyCode"));
        tbColumnTargetCoin.setCellValueFactory(new PropertyValueFactory<>("targetCurrencyCode"));
        tbColumnPrice.setCellValueFactory(new PropertyValueFactory<>("quotation"));

        tbColumnLastUpdate.setCellValueFactory(historicItemCell -> new SimpleStringProperty(
                APIUtils.fmt.format(new Date(historicItemCell.getValue().getLastQuotaUpdateTimestamp() * 1000))
        ));

        tbColumnSavedInfo.setCellValueFactory(historicItemCell -> new SimpleStringProperty(
                APIUtils.fmt.format(new Date(historicItemCell.getValue().getCheckedDateTimestamp()))
        ));

        tbHistory.getItems().addListener((ListChangeListener<HistoricItem>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    tbHistory.getItems().sort(comparatorCheckedDate);
                }
            }
        });

        try {
            if (HistoricManager.getInstance().load()) {
                HistoricManager.getInstance().getHistoricItemList().stream()
                        .sorted(comparatorCheckedDate)
                        .forEach(this::populateTableRow);
            }
        } catch (IOException ignored) {
        }
    }
}