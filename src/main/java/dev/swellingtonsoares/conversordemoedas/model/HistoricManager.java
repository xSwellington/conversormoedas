package dev.swellingtonsoares.conversordemoedas.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HistoricManager {


    private static HistoricManager manager;
    private final List<HistoricItem> historicItemList = new ArrayList<>();

    public List<HistoricItem> getHistoricItemList() {
        return historicItemList;
    }


    private HistoricManager() {}

    public static HistoricManager getInstance() {
        if (manager == null) manager = new HistoricManager();
        return manager;
    }

    public void add(HistoricItem result) throws IOException {
        historicItemList.add(result);
    }

    public boolean load() throws IOException {
        try (BufferedReader br = new BufferedReader( new FileReader("historic.csv") )) {
            String data = br.readLine();
            while (data != null && (!data.isBlank())) {
                historicItemList.add(0, HistoricItem.fromCsv(data));
                data = br.readLine();
            }
            return true;
        } catch (Exception e) {
            return  false;
        }
    }

    public void save() throws FileNotFoundException {
        try (PrintWriter pw = new PrintWriter("historic.csv")){
            historicItemList.stream().map(HistoricItem::toCsv).forEach(pw::println);
        }
    }
}
