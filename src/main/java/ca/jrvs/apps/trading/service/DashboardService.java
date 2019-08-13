package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.*;
import ca.jrvs.apps.trading.model.domain.Account;
import ca.jrvs.apps.trading.model.domain.Position;
import ca.jrvs.apps.trading.model.view.PortfolioView;
import ca.jrvs.apps.trading.model.view.TraderAccountView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class DashboardService {
    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);

    private TraderDao traderDao;
    private PositionDao positionDao;
    private SecurityOrderDao securityOrderDao;
    private AccountDao accountDao;


    @Autowired
    public DashboardService(TraderDao traderDao, PositionDao positionDao, SecurityOrderDao securityOrderDao, AccountDao accountDao) {
        this.traderDao = traderDao;
        this.positionDao = positionDao;
        this.securityOrderDao = securityOrderDao;
        this.accountDao = accountDao;
    }

    /**
     * Create and return a traderAccountView by trader ID
     * - get trader account by id
     * - get trader info by id
     * - create and return a traderAccountView
     *
     * @param traderId trader ID
     * @return traderAccountView
     * @throws ca.jrvs.apps.trading.dao.ResourceNotFoundException if ticker is not found from IEX
     * @throws org.springframework.dao.DataAccessException        if unable to retrieve data
     * @throws IllegalArgumentException                           for invalid input
     */
    public TraderAccountView getTraderAccount(Integer traderId) {
        if (traderId == null || traderId <= 0)
            throw new IllegalArgumentException("Invalid traderId");

        if (!accountDao.existsById(traderId))
            throw new IllegalArgumentException("Trader does not exist");

        TraderAccountView traderAccountView = new TraderAccountView();

        try {
            traderAccountView.setAccount(accountDao.findByTraderId(traderId));
            traderAccountView.setTrader(traderDao.findById(traderId));

        } catch (DataAccessException e) {
            throw new IllegalArgumentException("Unable to retrieve data", e);
        } catch (ResourceNotFoundException ex) {
            throw new ResourceNotFoundException("Unable to access account", ex);
        }

        return traderAccountView;
    }

    /**
     * Create and return portfolioView by trader ID
     * - get account by trader id
     * - get positions by account id
     * - create and return a portfolioView
     *
     * @param traderId
     * @return portfolioView
     * @throws ca.jrvs.apps.trading.dao.ResourceNotFoundException if ticker is not found from IEX
     * @throws org.springframework.dao.DataAccessException        if unable to retrieve data
     * @throws IllegalArgumentException                           for invalid input
     */
    public PortfolioView getProfileViewByTraderId(Integer traderId) {
        if (traderId == null || traderId <= 0)
            throw new IllegalArgumentException("Invalid traderId");

        if (!accountDao.existsById(traderId))
            throw new IllegalArgumentException("Trader does not exist");

        PortfolioView portfolioView = new PortfolioView();
        Account account;
        List<Position> positions;
        PortfolioView.SecurityRow securityRow = new PortfolioView.SecurityRow();

        try {
            account = accountDao.findByTraderId(traderId);
            positions = positionDao.findByAccount(account.getId());
            securityRow.setPosition((Position) positions);
            securityRow.setTicker(((Position) positions).getTicker());
            portfolioView.setSecurityRows(Collections.singletonList(securityRow));
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("Unable to retrieve data", e);
        } catch (ResourceNotFoundException ex) {
            throw new ResourceNotFoundException("Unable to access account", ex);
        }

        return portfolioView;
    }
}

