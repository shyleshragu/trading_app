package ca.jrvs.apps.trading.model.domain;


public class SecurityOrder implements Entity<Integer> {

    private Integer id;
    private Integer accountId;
    private OrderStatus status;
    private String ticker;
    private Integer size;
    private Double price;
    private String notes;

    public SecurityOrder(Integer id, Integer accountId, OrderStatus status, String ticker, Integer size, Double price, String notes) {
        this.id = id;
        this.accountId = accountId;
        this.status = status;
        this.ticker = ticker;
        this.size = size;
        this.price = price;
        this.notes = notes;
    }

    public SecurityOrder() {
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer integer) {
        this.id = id;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
