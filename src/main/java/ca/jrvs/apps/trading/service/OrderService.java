package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.*;
import ca.jrvs.apps.trading.model.domain.*;
import ca.jrvs.apps.trading.model.dto.MarketOrderDto;
import java.sql.SQLException;

import com.sun.org.apache.xpath.internal.operations.Quo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.crypto.Data;

@Service
@Transactional
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private AccountDao accountDao;
    private SecurityOrderDao securityOrderDao;
    private QuoteDao quoteDao;
    private PositionDao positionDao;

    @Autowired
    public OrderService(AccountDao accountDao, SecurityOrderDao securityOrderDao,
                        QuoteDao quoteDao, PositionDao positionDao) {
        this.accountDao = accountDao;
        this.securityOrderDao = securityOrderDao;
        this.quoteDao = quoteDao;
        this.positionDao = positionDao;
    }

    /**
     * Execute a market order
     *
     * - validate the order (e.g. size, and ticker)
     * - Create a securityOrder (for security_order table)
     * - Handle buy or sell order
     *   - buy order : check account balance
     *   - sell order: check position for the ticker/symbol
     *   - (please don't forget to update securityOrder.status)
     * - Save and return securityOrder
     *
     * NOTE: you will need to some helper methods (protected or private)
     *
     * @param orderDto market order
     * @return SecurityOrder from security_order table
     * @throws org.springframework.dao.DataAccessException if unable to get data from DAO
     * @throws IllegalArgumentException for invalid input
     */
    public SecurityOrder executeMarketOrder(MarketOrderDto orderDto) {
        if (orderDto.getSize() == null || orderDto.getTicker() == null)
            throw new IllegalArgumentException("Invalid orderDto");

        if (!quoteDao.existsById(orderDto.getTicker()))
            throw new IllegalArgumentException("Invalid ticker");

        SecurityOrder securityOrder = new SecurityOrder();

        Quote quote;
        Account account;

        try {
            quote = quoteDao.findById(orderDto.getTicker());
            account = accountDao.findByTraderId(orderDto.getAccountId());

            securityOrder.setAccountId(account.getId());
            securityOrder.setTicker(orderDto.getTicker());
            securityOrder.setSize(orderDto.getSize());
            securityOrder.setPrice(quote.getAskPrice());
            securityOrder.setStatus(OrderStatus.PENDING);

            if (securityOrder.getSize() > 0) {
                securityOrder.setStatus(buyOrder(account, quote, orderDto));
            }
            else {
                securityOrder.setStatus(sellOrder(account, quote, orderDto));
            }
        } catch (DataAccessException e){
            throw new IllegalArgumentException("Unable to retrieve data", e);
        }

        return securityOrderDao.save(securityOrder);
    }

    private OrderStatus sellOrder(Account account, Quote quote, MarketOrderDto orderDto) {
        double cost = orderDto.getSize() * quote.getAskPrice();
        Position position = positionDao.findByTickerAndAccount(orderDto.getTicker(), orderDto.getAccountId());

        if (position.getPosition() >= orderDto.getSize()) {
            account.setAmount(account.getAmount() + cost);
            accountDao.updateAmountById(account.getId(), account.getAmount());
            return OrderStatus.FILLED;
        }
        else {
            return OrderStatus.CANCELED;
        }
    }

    private OrderStatus buyOrder(Account account, Quote quote, MarketOrderDto orderDto) {
        double cost = orderDto.getSize() * quote.getAskPrice();

        if (account.getAmount() >= cost) {
            account.setAmount(account.getAmount() - cost);
            accountDao.updateAmountById(account.getId(), account.getAmount());
            return OrderStatus.FILLED;
        } else  {
            return OrderStatus.CANCELED;
        }
    }

}

