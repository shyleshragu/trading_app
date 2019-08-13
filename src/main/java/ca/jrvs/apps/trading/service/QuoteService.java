package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.MarketDataDao;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import ca.jrvs.apps.trading.model.domain.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class QuoteService {
    private static final Logger logger = LoggerFactory.getLogger(QuoteService.class);
    private QuoteDao quoteDao;
    private MarketDataDao marketDataDao;

    @Autowired
    public QuoteService(QuoteDao quoteDao, MarketDataDao marketDataDao) {
        this.quoteDao = quoteDao;
        this.marketDataDao = marketDataDao;
    }

    /**
     * Helper method which map a IexQuote to a Quote entity.
     * Note: `iexQuote.getLatestPrice() == null if the stock market is closed.
     * Make sure set a default value for number field(s).
     */
    public static Quote buildQuoteFromIexQuote(IexQuote iexQuote) {
        if (iexQuote == null)
            throw new IllegalArgumentException("Error: iexQuote is empty");

        Quote quote = new Quote();

        quote.setAskPrice(Double.parseDouble(iexQuote.getIexAskPrice()));
        quote.setAskSize(Integer.parseInt(iexQuote.getIexAskSize()));
        quote.setBidPrice(Double.parseDouble(iexQuote.getIexBidPrice()));
        quote.setBidSize(Integer.parseInt(iexQuote.getIexBidSize()));
        quote.setLastPrice(Double.parseDouble(iexQuote.getLatestPrice()));
        quote.setTicker(iexQuote.getSymbol());

        return quote;
    }

    /**
     * Add a list of new tickers to the quote table. Skip existing ticker(s).
     * - Get iexQuote
     * - convert iexQuote to Quote entity
     * - persist the quote to db
     *
     * @throws ca.jrvs.apps.trading.dao.ResourceNotFoundException if ticker is not found from IEX
     * @throws org.springframework.dao.DataAccessException        if unable to retrieve data
     * @throws IllegalArgumentException                           for invalid input
     */
    public void initQuotes(List<String> tickers) {
        List<IexQuote> iexQuotes = marketDataDao.findIexQuoteByTicker(tickers);
        List<Quote> quotes = new ArrayList<>();

        int i=0;
        quotes.forEach(quote -> {
            if (!quoteDao.existsById(iexQuotes.get(i).getSymbol())) {
                quotes.add(buildQuoteFromIexQuote(iexQuotes.get(i)));
                quoteDao.save(quotes.get(i));
            }
        });
    }

    /**
     * Add a new ticker to the quote table. Skip existing ticker.
     *
     * @throws ca.jrvs.apps.trading.dao.ResourceNotFoundException if ticker is not found from IEX
     * @throws org.springframework.dao.DataAccessException        if unable to retrieve data
     * @throws IllegalArgumentException                           for invalid input
     */
    public void initQuote(String ticker) {
        initQuotes(Collections.singletonList(ticker));
    }

    /**
     * Update quote table against IEX source
     * -get all quotes from the db
     * - foreach ticker get iexQuote
     * - persist quote to db
     *
     * @throws ca.jrvs.apps.trading.dao.ResourceNotFoundException if ticker is not found from IEX
     * @throws org.springframework.dao.DataAccessException        if unable to retrieve data
     * @throws IllegalArgumentException                           for invalid input
     */
    public void updateMarketData() throws IOException {
        List<Quote> quotes = quoteDao.findAll();
        List<IexQuote> iexQuotes = new ArrayList<>();
        List<Quote> updateQuotes = new ArrayList<>();

        for (Quote quote : quotes) {
            iexQuotes.add(marketDataDao.findIexQuoteByTicker(quote.getTicker()));
        }
        for (IexQuote iexQuote : iexQuotes) {
            updateQuotes.add(buildQuoteFromIexQuote(iexQuote));
        }
        quoteDao.update(updateQuotes);
    }

    public void updateQuote(Quote quote) {
        if (quote == null)
            throw new IllegalArgumentException("Error: Parameter is empty");

        quote.setTicker(quote.getTicker().toUpperCase());
        quoteDao.update(Collections.singletonList(quote));
    }
}
