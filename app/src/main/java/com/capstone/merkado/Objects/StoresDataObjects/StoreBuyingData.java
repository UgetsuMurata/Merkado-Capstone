package com.capstone.merkado.Objects.StoresDataObjects;

public class StoreBuyingData {
    Integer serverId;
    Integer playerMarketId; // market
    Integer sellerId; // seller
    Integer playerId; // buyer
    Integer onSaleId;
    Integer quantity;
    PlayerMarkets.OnSale onSale;

    public StoreBuyingData(Integer serverId, Integer playerMarketId, Integer sellerId, Integer onSaleId, Integer quantity, PlayerMarkets.OnSale onSale) {
        this.serverId = serverId;
        this.playerMarketId = playerMarketId;
        this.sellerId = sellerId;
        this.onSaleId = onSaleId;
        this.quantity = quantity;
        this.onSale = onSale;
    }

    public Integer getServerId() {
        return serverId;
    }

    public Integer getPlayerMarketId() {
        return playerMarketId;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public Integer getOnSaleId() {
        return onSaleId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public Boolean isSameResource(PlayerMarkets.OnSale onSale) {
        return this.onSale.equals(onSale);
    }
}
