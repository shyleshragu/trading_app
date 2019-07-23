package ca.jrvs.apps.trading.model.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "traderId",
        "amount"
})
public class Account {

    @JsonProperty("id")
    private int id;
    @JsonProperty("traderId")
    private int traderId;
    @JsonProperty("amount")
    private int amount;

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty("traderId")
    public int getTraderId() {
        return traderId;
    }

    @JsonProperty("traderId")
    public void setTraderId(int traderId) {
        this.traderId = traderId;
    }

    @JsonProperty("amount")
    public int getAmount() {
        return amount;
    }

    @JsonProperty("amount")
    public void setAmount(int amount) {
        this.amount = amount;
    }

}