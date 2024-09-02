package com.capstone.merkado.Objects.ResourceDataObjects;

public enum RESOURCES {
    KUTSINTA(2),
    TAHO(3),
    TURON(4),
    BALUT(5),
    LUMPIA(6),
    HALO_HALO(7),
    BIBINGKA(8),
    PANCIT(9),
    SINIGANG(10),
    ADOBO(11),
    KARE_KARE(12),
    LETSON(13),
    GRAIN(14),
    COAL(15),
    TIMBER(16),
    OIL_BARRELS(17),
    TOOLBOX(18),
    METAL_SCRAPS(19),
    IRON_INGOTS(20),
    SILK(21),
    GOLD_ORE(22),
    GEMS(23);

    public final int value;

    RESOURCES(int value) {
        this.value = value;
    }
}
