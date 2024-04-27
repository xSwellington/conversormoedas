package dev.swellingtonsoares.conversordemoedas.model;


import com.google.gson.Gson;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class APIUtils {

    public static final SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
    private static final Map<String, String> currencyMap;

    private static final String ApiKey = "4bd15ea1607c60ac4eb077c4";
    private static final String APIEndPoint = "https://v6.exchangerate-api.com/v6";

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    static {
        String[][] CURRENCY_LIST = {
                {"USD", "Dólar Americano"},
                {"EUR", "Euro"},
                {"JPY", "Iene Japonês"},
                {"GBP", "Libra Esterlina"},
                {"AUD", "Dólar Australiano"},
                {"CAD", "Dólar Canadense"},
                {"CHF", "Franco Suíço"},
                {"CNY", "Renminbi Chinês"},
                {"SEK", "Coroa Sueca"},
                {"NZD", "Dólar da Nova Zelândia"},
                {"BRL", "Real Brasileiro"},
                {"RUB", "Rublo Russo"},
                {"INR", "Rúpia Indiana"},
                {"KRW", "Won Sul-Coreano"},
                {"TRY", "Lira Turca"},
                {"ZAR", "Rand Sul-Africano"},
                {"HKD", "Dólar de Hong Kong"},
                {"IDR", "Rupia Indonésia"},
                {"MXN", "Peso Mexicano"},
                {"SGD", "Dólar de Singapura"},
                {"THB", "Baht Tailandês"},
                {"AED", "Dirham dos Emirados Árabes Unidos"},
                {"ARS", "Peso Argentino"},
                {"COP", "Peso Colombiano"},
                {"EGP", "Libra Egípcia"},
                {"MYR", "Ringgit Malaio"},
                {"NOK", "Coroa Norueguesa"},
                {"PHP", "Peso Filipino"},
                {"PLN", "Zloty Polonês"},
                {"SAR", "Rial Saudita"},
                {"TWD", "Novo Dólar de Taiwan"},
                {"VND", "Dong Vietnamita"},
                {"ILS", "Novo Shekel Israelense"},
                {"CZK", "Coroa Tcheca"},
                {"CLP", "Peso Chileno"},
                {"HUF", "Forint Húngaro"},
                {"QAR", "Rial Catariano"},
                {"DKK", "Coroa Dinamarquesa"},
                {"KWD", "Dinar Kuwaitiano"},
                {"RON", "Leu Romeno"},
                {"BHD", "Dinar Bareinita"},
                {"PKR", "Rupia Paquistanesa"},
                {"XOF", "Franco CFA"},
                {"UAH", "Hryvnia Ucraniano"},
                {"NGN", "Naira Nigeriana"},
                {"GHS", "Cedi Ganês"},
                {"PEN", "Sol Peruano"},
                {"UGX", "Xelim Ugandense"},
                {"BYN", "Rublo Bielorrusso"},
                {"BDT", "Taka Bengali"},
                {"RSD", "Dinar Sérvio"},
                {"HRK", "Kuna Croata"},
                {"MMK", "Quiate Mianmarense"},
                {"DZD", "Dinar Argelino"},
                {"TND", "Dinar Tunisiano"},
                {"LKR", "Rupia do Sri Lanka"},
                {"MAD", "Dirham Marroquino"},
                {"UZS", "Som Uzbeque"},
                {"SYP", "Libra Síria"},
                {"AOA", "Kwanza Angolano"},
                {"ISK", "Coroa Islandesa"},
                {"JOD", "Dinar Jordaniano"},
                {"TZS", "Xelim da Tanzânia"},
                {"GTQ", "Quetzal Guatemalteco"},
                {"BND", "Dólar de Brunei"},
                {"LBP", "Libra Libanesa"},
                {"NPR", "Rupia Nepalesa"},
                {"CUP", "Peso Cubano"},
                {"GEL", "Lari Georgiano"},
                {"XAF", "Franco CFA Central"},
                {"XCD", "Dólar do Caribe Oriental"},
                {"VEF", "Bolívar Venezuelano"},
                {"YER", "Rial Iemenita"},
                {"MZN", "Metical Moçambicano"},
                {"AMD", "Dram Armênio"},
                {"BWP", "Pula Botsuanesa"},
                {"AFN", "Afegane Afegão"},
                {"CDF", "Franco Congolês"},
                {"CRC", "Colón Costarriquenho"},
                {"DJF", "Franco Djiboutiano"},
                {"ERN", "Nakfa Eritreia"},
                {"ETB", "Birr Etíope"},
                {"FJD", "Dólar de Fiji"},
                {"GMD", "Dalasi Gambiano"},
                {"GNF", "Franco Guineense"},
                {"HTG", "Gourde Haitiano"},
                {"JMD", "Dólar Jamaicano"},
                {"KES", "Xelim Queniano"},
                {"KGS", "Som Quirguistanês"},
                {"KMF", "Franco Comoriano"},
                {"KPW", "Won Norte-Coreano"},
                {"KZT", "Tenge Cazaque"},
                {"LAK", "Kip Laosiano"},
                {"LSL", "Loti Lesota"},
                {"LRD", "Dólar Liberiano"},
                {"LYD", "Dinar Líbio"},
                {"MGA", "Ariary Malgaxe"},
                {"MKD", "Dinar Macedônio"},
                {"MNT", "Tugrik Mongol"},
                {"MOP", "Pataca de Macau"},
                {"MRO", "Ouguiya Mauritana"},
                {"MUR", "Rupia Mauriciana"},
                {"MVR", "Rupia das Maldivas"},
                {"MWK", "Kwacha Malauiano"},
                {"MXN", "Peso Mexicano"},
                {"MXV", "Unidade de Investimento Mexicana"},
                {"MDL", "Leu Moldávio"},
                {"MZN", "Metical Moçambicano"}
        };

        currencyMap = Arrays.stream(CURRENCY_LIST).collect(
                Collectors.toMap(
                        currency -> currency[0], currency -> currency[1],
                        (e, n) -> e,
                        LinkedHashMap::new
                )
        );
    }

    public static Map<String, String> getValidCurrencies() {
        return APIUtils.currencyMap;
    }

    public static CurrentCheckResult MakeRequest(String source, String target, Double value) throws IOException, InterruptedException {
        String uri = APIEndPoint + "/" + ApiKey + "/pair/" + source + "/" + target;
        var request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("accept", "application/json")
                .GET()
                .build();
        var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        Thread.sleep(10000);
        DataResult dataResult = new Gson().fromJson(response.body(), DataResult.class);
        if (!dataResult.result().equals("success"))
            throw new RuntimeException("Não foi possível verificar as informações no momento.");
        return new CurrentCheckResult(dataResult, value);
    }
}
