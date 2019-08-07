package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.*;
import ca.jrvs.apps.trading.model.domain.Account;
import ca.jrvs.apps.trading.model.domain.Position;
import ca.jrvs.apps.trading.model.domain.Trader;
import ca.jrvs.apps.trading.model.view.TraderAccountView;
import ca.jrvs.apps.trading.util.StringUtil;
import java.lang.reflect.Parameter;
import java.util.List;

import org.apache.tomcat.util.http.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {

    private TraderDao traderDao;
    private AccountDao accountDao;
    private PositionDao positionDao;
    private SecurityOrderDao securityOrderDao;

    @Autowired
    public RegisterService(TraderDao traderDao, AccountDao accountDao,
                           PositionDao positionDao, SecurityOrderDao securityOrderDao) {
        this.traderDao = traderDao;
        this.accountDao = accountDao;
        this.positionDao = positionDao;
        this.securityOrderDao = securityOrderDao;
    }

    /**
     * Create a new trader and initialize a new account with 0 amount.
     * - validate user input (all fields must be non empty)
     * - create a trader
     * - create an account
     * - create, setup, and return a new traderAccountView
     *
     * @param trader trader info
     * @return traderAccountView
     * @throws ca.jrvs.apps.trading.dao.ResourceNotFoundException if ticker is not found from IEX
     * @throws org.springframework.dao.DataAccessException if unable to retrieve data
     * @throws IllegalArgumentException for invalid input
     */
    public TraderAccountView createTraderAndAccount(Trader trader) {
        if (trader == null)
            throw new IllegalArgumentException("Invalid trader. Null cannot be passed");

        Account account = new Account();
        Trader newTrader;
        TraderAccountView traderAccountView;


        try {
            newTrader = traderDao.save(trader);

            account.setAmount(0);
            account.setTraderId(newTrader.getId());
            account.setId(newTrader.getId());
            accountDao.save(account);

            traderAccountView = new TraderAccountView();
            traderAccountView.setTrader(newTrader);
            traderAccountView.setAccount(account);
        } catch (DataAccessException e){
            throw new ResourceNotFoundException("Create trader failure", e);
        }

        return traderAccountView;
    }

    /**
     * A trader can be deleted iff no open position and no cash balance.
     * - validate traderID
     * - get trader account by traderId and check account balance
     * - get positions by accountId and check positions
     * - delete all securityOrders, account, trader (in this order)
     *
     * @param traderId
     * @throws ca.jrvs.apps.trading.dao.ResourceNotFoundException if ticker is not found from IEX
     * @throws org.springframework.dao.DataAccessException if unable to retrieve data
     * @throws IllegalArgumentException for invalid input
     */
    public void deleteTraderById(Integer traderId) {
        if (traderId == null)
            throw new IllegalArgumentException("TraderId cannot be null");

        if (!traderDao.existsById(traderId))
            throw new IllegalArgumentException("TraderId does not exist");

        Account account = accountDao.findByTraderId(traderId);
        if (account.getAmount() != 0)
            throw new IllegalArgumentException("Cannot delete Trader account. Trader has an non-zero amount entry");

        List<Position> position2 = positionDao.findByAccount(account.getId());

        position2.forEach(position1 -> {
            if (position1.getPosition() != 0)
                throw new IllegalArgumentException("Trader has non-zero position. Trader cannot be deleted");
        });

        securityOrderDao.deleteById(account.getId());
        accountDao.deleteById(account.getId());
        traderDao.deleteById(traderId);
    }

}

