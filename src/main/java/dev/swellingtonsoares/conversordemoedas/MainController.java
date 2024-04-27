package dev.swellingtonsoares.conversordemoedas;

import dev.swellingtonsoares.conversordemoedas.model.APIUtils;
import dev.swellingtonsoares.conversordemoedas.model.Currency;
import dev.swellingtonsoares.conversordemoedas.model.HistoricItem;
import dev.swellingtonsoares.conversordemoedas.model.HistoricManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
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
    private TableColumn<HistoricItem, String> lbColumnSavedInfo;

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
                        Task<Void> task = new Task<>() {
                            @Override
                            protected Void call() throws InterruptedException {
                                Thread.sleep(1);
                                Platform.runLater(() -> {
                                    btnCheck.setDisable(true);
                                    cbTargetCoin.setDisable(true);
                                    cbSourceCoin.setDisable(true);
                                    tfCoinValue.setDisable(true);
                                    lbCotacao.setText("Cotação: Verificando...");
                                    lbValorTotal.setText("Valor Total: Verificando...");
                                    lbLastUpdate.setText("Última Atualização: Verificando...");
                                });
                                return null;
                            }
                        };
                        new Thread(task).start();

                    }).onFinish(result -> {

                        Task<Void> task = new Task<>() {
                            @Override
                            protected Void call() throws IOException {
                                Platform.runLater(() -> {
                                    btnCheck.setDisable(false);
                                    cbTargetCoin.setDisable(false);
                                    cbSourceCoin.setDisable(false);
                                    tfCoinValue.setDisable(false);
                                    lbCotacao.setText("Cotação: " + result.getTargetCurrencyCode() + " " + result.getConversion() + "( " + result.getTargetCurrentName() + " )");
                                    lbValorTotal.setText("Valor Total: " + result.getTargetCurrencyCode() + " " + result.getTotalValue() + " ( " + result.getTargetCurrentName() + " )");
                                    lbLastUpdate.setText("Última Atualização: " + result.getLastConversionUpdateDate());
                                });
                                HistoricItem h = new HistoricItem(result, HistoricManager.getInstance().getHistoricItemList().size() + 1);
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
        tbColumnPrice.setCellValueFactory(new PropertyValueFactory<>("conversion"));
        tbColumnLastUpdate.setCellValueFactory(new PropertyValueFactory<>("lastConversionUpdateDate"));
        lbColumnSavedInfo.setCellValueFactory(new PropertyValueFactory<>("checkDate"));

        try {
            if (HistoricManager.getInstance().load()) {
                HistoricManager.getInstance().getHistoricItemList().forEach(this::populateTableRow);
            }
        } catch (IOException ignored) {
        }
    }
}