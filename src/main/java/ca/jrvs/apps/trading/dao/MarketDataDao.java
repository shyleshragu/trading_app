package ca.jrvs.apps.trading.dao;


import ca.jrvs.apps.trading.model.config.MarketDataConfig;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static ca.jrvs.apps.trading.util.JsonUtil.toObjectFromJson;


@Repository
public class MarketDataDao {
    private Logger logger = LoggerFactory.getLogger(MarketDataDao.class);

    private final String QUOTE_URL;
    private HttpClientConnectionManager httpClientConnectionManager;

    //public MarketDataDao(HttpClientConnectionManager httpClientConnectionManager, MarketDataConfig marketDataConfig){
    public MarketDataDao(HttpClientConnectionManager httpClientConnectionManager){
        this.httpClientConnectionManager = httpClientConnectionManager;
    //    QUOTE_URL = marketDataConfig.getHost() + "/stock/market/batch?symbols=%s&types=quote&token=" + marketDataConfig.getToken();
        QUOTE_URL = "https://cloud.iexapis.com/stable/stock/market/batch?symbols=%s&types=quote&token=" + System.getenv("IEX_PUB_TOKEN");
    }


    public List<IexQuote> findIexQuoteByTicker(List<String> tickerList) throws NotAuthorizedException, ResourceNotFoundException, IOException {
        List<String> batchSymbols = new ArrayList<>();
        batchSymbols.add("aapl");

        String tick = String.join(",", batchSymbols);
        String url = String.format(QUOTE_URL, tick);

        String response = responseexcute(url);

        JSONObject iexQuotesJson = new JSONObject();
        if (iexQuotesJson.length() == 0)
            throw new ResourceNotFoundException("Not found");
        else if (iexQuotesJson.length() != tickerList.size())
            throw new IllegalArgumentException("Invalid ticker/symbol");

        //Unmarshal JSON object
        List<IexQuote> iexQuoteList = new ArrayList<>();
        iexQuotesJson.keys().forEachRemaining((ticker -> {
            try {
                String qtstr = ((JSONObject) iexQuotesJson.get(ticker)).get("quote").toString();
                IexQuote iexQuote = toObjectFromJson(qtstr, IexQuote.class);
                iexQuoteList.add(iexQuote);
            } catch (IOException e) {
                throw new DataRetrievalFailureException("Unable to parse response: " + iexQuotesJson.get(ticker), e);
            }
        }));
        return iexQuoteList;
    }

    public IexQuote findIexQuoteByTicker(String ticker) throws NotAuthorizedException, ResourceNotFoundException, IOException {
        List<IexQuote> quotes = findIexQuoteByTicker(Arrays.asList(ticker));

        if (quotes == null || quotes.size() != 1)
            throw new DataRetrievalFailureException("Unable to get data");

        return quotes.get(0);
    }

    private String responseexcute(String url) throws NotAuthorizedException, ResourceNotFoundException {
        try (CloseableHttpClient httpClient = getHttpClient()){
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            switch (response.getStatusLine().getStatusCode()){
                case 200:
                    String body = EntityUtils.toString(response.getEntity());
                    return Optional.ofNullable(body).orElseThrow(() -> new IOException("Unexpected empty http response body"));

                case 401:
                    throw new NotAuthorizedException("Unauthorized");

                case 403:
                    throw new AccessDeniedException("Forbidden");
                case 404:
                    //throw new ResourceNotFoundException("ticker is not found");
                    throw new ResourceNotFoundException("ticker is not found");

                default:
                    throw new DataRetrievalFailureException("Unexpected status: " + response.getStatusLine().getStatusCode());

            }

        } catch (IOException e){
            throw new DataRetrievalFailureException("Unable Http execution error: \n", e);
        }


    }

    private CloseableHttpClient getHttpClient(){
     return HttpClients.custom().setConnectionManager(httpClientConnectionManager).setConnectionManagerShared(true).build();
    }

    /**
     * Exceptions are made as the exception directories could not be found
     */
    public class NotAuthorizedException extends Throwable {
        public NotAuthorizedException(String unauthorized) {
            super(unauthorized);
        }
    }

    public class ResourceNotFoundException extends Throwable {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}


