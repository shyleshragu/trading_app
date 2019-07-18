package ca.jrvs.apps.trading.dao;


import ca.jrvs.apps.trading.model.config.MarketDataConfig;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Repository
public class MarketDataDao {

    private Logger logger = (Logger) LoggerFactory.getLogger(MarketDataDao.class);

    private final String BATCH_QUOTE_URL;
    private HttpClientConnectionManager httpClientConnectionManager;

    @Autowired
    public MarketDataDao(HttpClientConnectionManager httpClientConnectionManager,
                         MarketDataConfig marketDataConfig) {
        this.httpClientConnectionManager = httpClientConnectionManager;
        BATCH_QUOTE_URL =
                marketDataConfig.getHost() + "/stock/market/batch?symbols=%s&types=quote&token="
                        + marketDataConfig.getToken();
    }

    /**
     * @throws DataRetrievalFailureException if unable to get http response
     */

    public List<IexQuote> findIexQuoteByTicker(List<String> tickerList) {
        String uri = "https://cloud.iexapis.com/v1/stock/market/batch?symbols=aapl,msft,fb&types=quote&token=pk_d27ef780aa104af1b926d05385e1a7be";
        //convert list into comma separated string
        logger.info("Get URI:" + uri);
        //Get Http response body in string
        String response = executeHttpGet(uri);
        //Iex will skip invalid symbols/ticker..we need to check it
        if (IexQuotesJson.length() != tickerList.size()) {
            throw new IllegalArgumentException("Invalid ticker/symbol");
        }

        //Unmarshal JSON object
        return iexQuotes;
    }

    public IexQuote findIexQuoteByTicker(String ticker) {
        List<IexQuote> quotes = findIexQuoteByTicker(Arrays.asList(ticker));
        if (quotes == null || quotes.size() != 1) {
            throw new DataRetrievalFailureException("Unable to get data");
        }
        return quotes.get(0);
    }

    protected String executeHttpGet(String url) {
        try (CloseableHttpClient httpClient = getHttpClient()) {
            HttpGet httpGet = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                switch (response.getStatusLine().getStatusCode()) {
                    case 200:
                        //EntityUtils toString will also close inputStream in Entity
                        String body = EntityUtils.toString(response.getEntity());
                        return Optional.ofNullable(body).orElseThrow(
                                () -> new IOException("Unexpected empty http response body"));
                    case 404:
                        throw new ResourceNotFoundException("Not found");
                    default:
                        throw new DataRetrievalFailureException(
                                "Unexpected status:" + response.getStatusLine().getStatusCode());
                }
            }
        } catch (IOException e) {
            throw new DataRetrievalFailureException("Unable Http execution error", e);
        }
    }

    private CloseableHttpClient getHttpClient() {
        return HttpClients.custom()
                .setConnectionManager(httpClientConnectionManager)
                //prevent connectionManager shutdown when calling httpClient.close()
                .setConnectionManagerShared(true)
                .build();
    }
}
