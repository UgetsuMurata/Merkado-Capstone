package com.capstone.merkado.Helpers;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.DataFunctionPackage.StoreDataFunctions;

public class PlayerActions {

    public static String SERVER() {
        Merkado merkado = Merkado.getInstance();
        return merkado.getPlayer().getServer();
    }

    public static class Store {
        public static void buying(Integer resourceId, Integer quantity) {
            // update demand
            StoreDataFunctions.addDemandToResource(SERVER(), resourceId, quantity);
            StoreDataFunctions.addSupplyToResource(SERVER(), resourceId, quantity * -1);

        }

        public static void selling(Integer resourceId, Integer quantity) {

        }

        public static void createResource(Integer resourceId, Integer quantity) {
            // update supply
            StoreDataFunctions.addSupplyToResource(SERVER(), resourceId, quantity);
        }
    }
}
