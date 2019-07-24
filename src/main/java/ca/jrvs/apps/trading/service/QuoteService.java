package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.MarketDataDao;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import ca.jrvs.apps.trading.model.domain.Quote;

import java.util.List;

public class QuoteService {

    private QuoteDao quoteDao;
    private MarketDataDao marketDataDao;

    public QuoteService(QuoteDao quoteDao, MarketDataDao marketDataDao) {
        this.quoteDao = quoteDao;
        this.marketDataDao = marketDataDao;
    }

    /**
     * Helper method which map a IexQuote to a Quote entity.
     * Note: `iexQuote.getLatestPrice() == null if the stock market is closed.
     * Make sure set a default value for number field(s).
     */
    public static Quote buildQuoteFromIexQuote(IexQuote iexQuote){
        return null;
    }

    /**
     * Add a list of new tickers to the quote table. Skip existing ticker(s).
     * - Get iexQuote
     * - convert iexQuote to Quote entity
     * - persist the quote to db
     *
     * @throws ca.jrvs.apps.trading.dao.ResourceNotFoundException if ticker is not found from IEX
     * @throws org.springframework.dao.DataAccessException if unable to retrieve data
     * @throws IllegalArgumentException for invalid input
     */
    public void initQuotes(List<String> tickers){

    }

    /**
     * Add a new ticker to the quote table. Skip existing ticker.
     *
     * @throws ca.jrvs.apps.trading.dao.ResourceNotFoundException if ticker is not found from IEX
     * @throws org.springframework.dao.DataAccessException if unable to retrieve data
     * @throws IllegalArgumentException for invalid input
     */
    public void initQuote(String ticker){

    }

    /**
     * Update quote table against IEX source
     * -get all quotes from the db
     * - foreach ticker get iexQuote
     * - persist quote to db
     *
     * @throws ca.jrvs.apps.trading.dao.ResourceNotFoundException if ticker is not found from IEX
     * @throws org.springframework.dao.DataAccessException if unable to retrieve data
     * @throws IllegalArgumentException for invalid input
     */
    public void updateMarketData(){

    }
}
