package ca.jrvs.apps.trading.controller;

import ca.jrvs.apps.trading.dao.AccountDao;
import ca.jrvs.apps.trading.dao.PositionDao;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.dao.SecurityOrderDao;
import ca.jrvs.apps.trading.model.dto.MarketOrderDto;
import ca.jrvs.apps.trading.model.domain.SecurityOrder;
import ca.jrvs.apps.trading.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/order")
public class OrderController {

    private OrderService orderService;
    private AccountDao accountDao;
    private QuoteDao quoteDao;
    private SecurityOrderDao securityOrderDao;
    private PositionDao positionDao;

    @Autowired
    public OrderController(OrderService orderService, AccountDao accountDao, QuoteDao quoteDao, SecurityOrderDao securityOrderDao, PositionDao positionDao) {
        this.orderService = orderService;
        this.accountDao = accountDao;
        this.quoteDao = quoteDao;
        this.securityOrderDao = securityOrderDao;
        this.positionDao = positionDao;
    }

    @PostMapping(path = "/marketOrder")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public SecurityOrder Order(@RequestBody MarketOrderDto order) {
        try {
            return orderService.executeMarketOrder(order);
        } catch (Exception e) {
            throw ResponseExceptionUtil.getResponseStatusException(e);
        }
    }
}
