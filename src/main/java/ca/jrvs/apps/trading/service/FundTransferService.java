package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.AccountDao;
import ca.jrvs.apps.trading.dao.ResourceNotFoundException;
import ca.jrvs.apps.trading.model.domain.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FundTransferService {
    private static final Logger logger = LoggerFactory.getLogger(FundTransferService.class);

    private AccountDao accountDao;

    @Autowired
    public FundTransferService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    /**
     * Deposit a fund to the account which is associated with the traderId
     * - validate user input
     * - account = accountDao.findByTraderId
     * - accountDao.updateAmountById
     *
     * @param traderId trader id
     * @param fund     found amount (can't be 0)
     * @return updated Account object
     * @throws ca.jrvs.apps.trading.dao.ResourceNotFoundException if ticker is not found from IEX
     * @throws org.springframework.dao.DataAccessException        if unable to retrieve data
     * @throws IllegalArgumentException                           for invalid input
     */
    public Account deposit(Integer traderId, Double fund) {
        if (traderId == null || traderId <= 0 || fund == null || fund <= 0)
            throw new IllegalArgumentException("Invalid traderId or fund");

        if (!accountDao.existsById(traderId))
            throw new IllegalArgumentException("trader does not exist");

        Account account;
        Double amount;

        try {
            account = accountDao.findByTraderId(traderId);
            amount = account.getAmount() + fund;

            account.setAmount(amount);
            accountDao.updateAmountById(account.getId(), amount);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("Unable to retrieve data", e);
        } catch (ResourceNotFoundException ex) {
            throw new ResourceNotFoundException("Unable to access account", ex);
        }

        return account;
    }

    /**
     * Withdraw a fund from the account which is associated with the traderId
     * <p>
     * - validate user input
     * - account = accountDao.findByTraderId
     * - accountDao.updateAmountById
     *
     * @param traderId trader ID
     * @param fund     amount can't be 0
     * @return updated Account object
     * @throws ca.jrvs.apps.trading.dao.ResourceNotFoundException if ticker is not found from IEX
     * @throws org.springframework.dao.DataAccessException        if unable to retrieve data
     * @throws IllegalArgumentException                           for invalid input
     */
    public Account withdraw(Integer traderId, Double fund) {
        if (traderId == null || traderId <= 0 || fund == null || fund <= 0)
            throw new IllegalArgumentException("Invalid traderId or fund");

        if (!accountDao.existsById(traderId))
            throw new IllegalArgumentException("trader does not exist");

        Account account;
        Double amount;

        try {
            account = accountDao.findByTraderId(traderId);
            amount = account.getAmount() - fund;

            account.setAmount(amount);
            accountDao.updateAmountById(account.getId(), amount);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("Unable to retrieve data", e);
        } catch (ResourceNotFoundException ex) {
            throw new ResourceNotFoundException("Unable to access account", ex);
        }
        return account;
    }
}

