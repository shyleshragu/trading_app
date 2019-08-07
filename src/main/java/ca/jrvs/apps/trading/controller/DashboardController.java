package ca.jrvs.apps.trading.controller;

import ca.jrvs.apps.trading.dao.AccountDao;
import ca.jrvs.apps.trading.dao.PositionDao;
import ca.jrvs.apps.trading.dao.SecurityOrderDao;
import ca.jrvs.apps.trading.dao.TraderDao;
import ca.jrvs.apps.trading.model.view.PortfolioView;
import ca.jrvs.apps.trading.model.view.TraderAccountView;
import ca.jrvs.apps.trading.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private DashboardService dashboardService;
    private TraderDao traderDao;
    private PositionDao positionDao;
    private SecurityOrderDao securityOrderDao;
    private AccountDao accountDao;

    @Autowired
    public DashboardController(DashboardService dashboardService, TraderDao traderDao, PositionDao positionDao, SecurityOrderDao securityOrderDao, AccountDao accountDao) {
        this.dashboardService = dashboardService;
        this.traderDao = traderDao;
        this.positionDao = positionDao;
        this.securityOrderDao = securityOrderDao;
        this.accountDao = accountDao;
    }

    @GetMapping(path = "/portfolio/traderId/{traderId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PortfolioView getPortfolio(@PathVariable Integer traderId) {
        try {
            return dashboardService.getProfileViewByTraderId(traderId);
        } catch (Exception e) {
            throw ResponseExceptionUtil.getResponseStatusException(e);
        }
    }

    @GetMapping(path = "/profile/traderId/{traderId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public TraderAccountView getProfile(@PathVariable Integer traderId) {
        try {
            return dashboardService.getTraderAccount(traderId);
        } catch (Exception e) {
            throw ResponseExceptionUtil.getResponseStatusException(e);
        }
    }

}
