package ca.jrvs.apps.trading.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.jrvs.apps.trading.dao.*;
import ca.jrvs.apps.trading.model.domain.*;
import ca.jrvs.apps.trading.model.dto.MarketOrderDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {

    //capture parameter when calling securityOrderDao.save
    @Captor
    ArgumentCaptor<SecurityOrder> captorSecurityOrder;

    //mock all dependencies
    @Mock
    private AccountDao accountDao;
    @Mock
    private QuoteDao quoteDao;
    @Mock
    private SecurityOrderDao securityOrderDao;
    @Mock
    private PositionDao positionDao;
    @Mock
    private List<Position> positions;
    @Mock
    private List<Account> accounts;
    @Mock
    private SecurityOrder securityOrder;

    //injecting mocked dependencies to the testing class via constructor
    @InjectMocks
    private OrderService orderService;
    Quote quote;
    Position position;
    Account account;

    //setup test data
    private MarketOrderDto orderDto;

    @Before
    public void setup() {
        quote = new Quote();
        quote.setTicker("AAPL");
        quote.setAskPrice(10.0);
        quote.setBidPrice(10.0);

        positions = new ArrayList<>();
        position = new Position();
        position.setAccountId(1);
        position.setPosition(100);
        position.setTicker("AAPL");
        positions.add(position);

        account = new Account();
        account.setId(1);
        account.setTraderId(1);
        account.setAmount(1000.0);
        accounts.add(account);

        securityOrder = new SecurityOrder();
        securityOrder.setTicker("AAPL");
        securityOrder.setAccountId(1);
        securityOrder.setSize(100);
        securityOrder.setStatus(OrderStatus.PENDING);

    }

    public void whenMocker(){
        when(quoteDao.existsById(orderDto.getTicker())).thenReturn(true);
        when(accountDao.findByTraderId(anyInt())).thenReturn(account);
        when(quoteDao.findById(orderDto.getTicker())).thenReturn(quote);
        when(securityOrderDao.save(any())).thenReturn(any());
    }

    @Test
    public void executeMarketOrderHappyPath() {
        orderDto = new MarketOrderDto(account.getTraderId(), position.getTicker(), position.getPosition());
        orderDto.setAccountId(1);
        orderDto.setSize(1);
        orderDto.setTicker("AAPL");
        whenMocker();

        orderService.executeMarketOrder(orderDto);
        verify(securityOrderDao).save(captorSecurityOrder.capture());
        SecurityOrder captorOrder = captorSecurityOrder.getValue();
        assertEquals(OrderStatus.FILLED, captorOrder.getStatus());
    }

    @Test
    public void executeMarketOrderSadPath() {
        orderDto = new MarketOrderDto(account.getTraderId(), position.getTicker(), position.getPosition());
        orderDto.setAccountId(2);
        orderDto.setSize(101); //MarkerOrderDto size greater than position size
        orderDto.setTicker("AAPL");
        whenMocker();

        orderService.executeMarketOrder(orderDto);
        verify(securityOrderDao).save(captorSecurityOrder.capture());
        SecurityOrder captorOrder = captorSecurityOrder.getValue();
        assertEquals(OrderStatus.CANCELED, captorOrder.getStatus());
    }

}