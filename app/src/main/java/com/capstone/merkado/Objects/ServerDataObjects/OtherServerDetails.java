package com.capstone.merkado.Objects.ServerDataObjects;

@SuppressWarnings("unused")
public class OtherServerDetails {
    ReachedLevels reachedLevels;
    Bots bots;

    public OtherServerDetails() {
    }

    public ReachedLevels getReachedLevels() {
        return reachedLevels;
    }

    public void setReachedLevels(ReachedLevels reachedLevels) {
        this.reachedLevels = reachedLevels;
    }

    public Bots getBots() {
        return bots;
    }

    public void setBots(Bots bots) {
        this.bots = bots;
    }

    public static class ReachedLevels {
        Integer lvl1, lvl2, lvl3, lvl4;

        public ReachedLevels() {
        }

        public ReachedLevels(Integer lvl1, Integer lvl2, Integer lvl3, Integer lvl4) {
            this.lvl1 = lvl1;
            this.lvl2 = lvl2;
            this.lvl3 = lvl3;
            this.lvl4 = lvl4;
        }

        public Integer getLvl1() {
            return lvl1;
        }

        public void setLvl1(Integer lvl1) {
            this.lvl1 = lvl1;
        }

        public Integer getLvl2() {
            return lvl2;
        }

        public void setLvl2(Integer lvl2) {
            this.lvl2 = lvl2;
        }

        public Integer getLvl3() {
            return lvl3;
        }

        public void setLvl3(Integer lvl3) {
            this.lvl3 = lvl3;
        }

        public Integer getLvl4() {
            return lvl4;
        }

        public void setLvl4(Integer lvl4) {
            this.lvl4 = lvl4;
        }
    }
    public static class Bots {
        Boolean store, factory;

        public Bots() {
        }

        public Bots(Boolean store, Boolean factory) {
            this.store = store;
            this.factory = factory;
        }

        public Boolean getStore() {
            return store;
        }

        public void setStore(Boolean store) {
            this.store = store;
        }

        public Boolean getFactory() {
            return factory;
        }

        public void setFactory(Boolean factory) {
            this.factory = factory;
        }
    }
}
