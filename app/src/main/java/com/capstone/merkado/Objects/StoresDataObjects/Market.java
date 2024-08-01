package com.capstone.merkado.Objects.StoresDataObjects;

@SuppressWarnings("unused")
public class Market {
    Integer id;
    Boolean hadMarket;

    public Market() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getHadMarket() {
        return hadMarket;
    }

    public void setHadMarket(Boolean hadMarket) {
        this.hadMarket = hadMarket;
    }
}
