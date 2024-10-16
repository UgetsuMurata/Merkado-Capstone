package com.capstone.merkado.DataManager.StaticData;

import com.capstone.merkado.R;

public class StoryResourceCaller {

    public static int retrieveCharacterBodyResource(String imgResource) {
        if (imgResource == null) return R.drawable.sprite_empty_b_empty;
        if (imgResource.startsWith("mc1")) {
            switch (imgResource) {
                case "mc1_b_pajamas":
                    return R.drawable.sprite_mc1_b_pajamas;
                case "mc1_b_uniform":
                    return R.drawable.sprite_mc1_b_uniform;
                case "mc1_b_casual":
                    return R.drawable.sprite_mc1_b_casual;
            }
        }
        else if (imgResource.startsWith("mc2")) {
            switch (imgResource) {
                case "mc2_b_uniform":
                    return R.drawable.sprite_mc2_b_uniform;
                case "mc2_b_casual":
                    return R.drawable.sprite_mc2_b_casual;
            }
        }
        else if (imgResource.startsWith("nanay1")) {
            switch (imgResource) {
                case "nanay1_b_home":
                    return R.drawable.sprite_nanay1_b_home;
                case "nanay1_b_casual":
                    return R.drawable.sprite_nanay1_b_casual;
                case "nanay1_b_modest":
                    return R.drawable.sprite_nanay1_b_modest;
                case "nanay1_b_normal2":
                    return R.drawable.sprite_nanay1_b_normal;
                case "nanay1_b_casual_reminiscing":
                    return R.drawable.sprite_nanay1_b_casual_reminisce;
            }
        }
        else if (imgResource.startsWith("teacher1")) {
            switch (imgResource) {
                case "teacher1_b_uniform1":
                    return R.drawable.sprite_teacher1_b_uniform1;
                case "teacher1_b_uniform2":
                    return R.drawable.sprite_teacher1_b_uniform2;
                case "teacher1_b_uniform3":
                    return R.drawable.sprite_teacher1_b_uniform3;
            }
        }
        else if (imgResource.startsWith("jaxon1")) {
            if ("jaxon1_b_uniform".equals(imgResource))
                return R.drawable.sprite_jaxon1_b_uniform;
        }
        else if (imgResource.startsWith("eian1")) {
            if ("eian1_b_uniform".equals(imgResource))
                return R.drawable.sprite_eian1_b_uniform;
        }
        else if (imgResource.startsWith("aling_isabel")) {
            if ("aling_isabel1_b_casual".equals(imgResource))
                return R.drawable.sprite_aling_isabel1_b_casual;
        }
        else if (imgResource.startsWith("pork_vendor1")) {
            if ("pork_vendor1_b_normal".equals(imgResource))
                return R.drawable.sprite_pork_vendor1_b_normal;
        }
        else if (imgResource.startsWith("nene")) {
            if ("nene_b_normal".equals(imgResource))
                return R.drawable.sprite_nene1_b_normal;
        }
        else if (imgResource.startsWith("tito1")) {
            if ("tito1_b_normal".equals(imgResource))
                return R.drawable.sprite_tito1_b_normal;
        }
        else if (imgResource.startsWith("kapitan1")) {
            if ("kapitan1_b_normal".equals(imgResource))
                return R.drawable.sprite_kapitan1_b_normal;
        }
        else if (imgResource.startsWith("tatay")) {
            if ("tatay_b_normal".equals(imgResource))
                return R.drawable.sprite_tatay1_b_normal;
        }
        else if (imgResource.startsWith("pharma")) {
            if ("pharma_b_normal".equals(imgResource))
                return R.drawable.sprite_pharmacist1_b_normal;
        }
        else if (imgResource.startsWith("rp1")) {
            if ("rp1_b_normal".equals(imgResource))
                return R.drawable.sprite_rp1_b_normal;
        }
        else if (imgResource.startsWith("rp21")) {
            if ("rp21_b_normal".equals(imgResource))
                return R.drawable.sprite_rp21_b_normal;
        }
        else if (imgResource.startsWith("nibble1")) { // change this in .json
            if (imgResource.equals("nibble1_b_normal")) {
                return R.drawable.sprite_nibble1_b_normal;
            }
        }
        else if (imgResource.startsWith("nibble2")) { // change this in .json
            if (imgResource.equals("nibble2_b_cute")) {
                return R.drawable.sprite_nibble2_b_cute;
            }
        }
        return R.drawable.sprite_empty_b_empty;
    }

    public static int retrieveCharacterFaceResource(String imgResource) {
        if (imgResource == null) return R.drawable.sprite_empty_b_empty;
        if (imgResource.startsWith("mc1")) {
            switch (imgResource) {
                case "mc1_f_normal":
                    return R.drawable.sprite_mc1_f_normal;
                case "mc1_f_compassion":
                    return R.drawable.sprite_mc1_f_compassion;
                case "mc1_f_sleepy":
                    return R.drawable.sprite_mc1_f_sleepy;
                case "mc1_f_confused":
                    return R.drawable.sprite_mc1_f_confused;
                case "mc1_f_amazed":
                    return R.drawable.sprite_mc1_f_amazed;
                case "mc1_f_embarrassed":
                    return R.drawable.sprite_mc1_f_embarrassed;
                case "mc1_f_nervous":
                    return R.drawable.sprite_mc1_f_nervous;
                case "mc1_f_curious":
                    return R.drawable.sprite_mc1_f_curious;
                case "mc1_f_smirk":
                    return R.drawable.sprite_mc1_f_smirk;
            }
        }
        else if (imgResource.startsWith("mc2")) {
            switch (imgResource) {
                case "mc2_f_thinking":
                    return R.drawable.sprite_mc2_f_thinking;
                case "mc2_f_thinking_closed_eyes":
                    return R.drawable.sprite_mc2_f_thinking2;
                case "mc2_f_thinking_open_eyes":
                    return R.drawable.sprite_mc2_f_thinking3;
            }
        }
        else if (imgResource.startsWith("nanay1")) {
            switch (imgResource) {
                case "nanay1_f_confused":
                    return R.drawable.sprite_nanay1_f_confused;
                case "nanay1_f_talking":
                    return R.drawable.sprite_nanay1_f_talking;
                case "nanay1_f_talking2":
                    return R.drawable.sprite_nanay1_f_talking2;
                case "nanay1_f_angry":
                    return R.drawable.sprite_nanay1_f_angry;
                case "nanay1_f_compassion":
                    return R.drawable.sprite_nanay1_f_compassion;
                case "nanay1_f_normal":
                    return R.drawable.sprite_nanay1_f_normal;
                case "nanay1_f_laughing":
                    return R.drawable.sprite_nanay1_f_laughing;
                case "nanay1_f_sad":
                    return R.drawable.sprite_nanay1_f_sad;
                case "nanay1_f_smile": // not used.
                    return R.drawable.sprite_nanay1_f_smile;
                case "nanay1_f_reminiscing":
                    return R.drawable.sprite_nanay1_f_reminiscing;
            }
        }
        else if (imgResource.startsWith("teacher1")) {
            switch (imgResource) {
                case "teacher1_f_compassion":
                    return R.drawable.sprite_teacher1_f_compassion;
                case "teacher1_f_laughing":
                    return R.drawable.sprite_teacher1_f_laughing;
                case "teacher1_f_talking1":
                    return R.drawable.sprite_teacher1_f_talking1;
                case "teacher1_f_talking2":
                    return R.drawable.sprite_teacher1_f_talking2;
            }
        }
        else if (imgResource.startsWith("jaxon1")) {
            switch (imgResource) {
                case "jaxon1_f_confused":
                    return R.drawable.sprite_jaxon1_f_confused;
                case "jaxon1_f_normal":
                    return R.drawable.sprite_jaxon1_f_normal;
                case "jaxon1_f_compassion":
                    return R.drawable.sprite_jaxon1_f_compassion;
                case "jaxon1_f_laughing":
                    return R.drawable.sprite_jaxon1_f_laughing;
            }
        }
        else if (imgResource.startsWith("eian1")) {
            switch (imgResource) {
                case "eian1_f_confused":
                    return R.drawable.sprite_eian1_f_confused;
                case "eian1_f_normal":
                    return R.drawable.sprite_eian1_f_normal;
                case "eian1_f_compassion":
                    return R.drawable.sprite_eian1_f_compassion;
                case "eian1_f_talking":
                    return R.drawable.sprite_eian1_f_talking;
            }
        }
        else if (imgResource.startsWith("aling_isabel")) {
            switch (imgResource) {
                case "aling_isabel1_f_normal":
                    return R.drawable.sprite_aling_isabel1_f_normal;
                case "aling_isabel1_f_talking":
                    return R.drawable.sprite_aling_isabel1_f_talking;
                case "aling_isabel1_f_confused":
                    return R.drawable.sprite_aling_isabel1_f_confused;
                case "aling_isabel1_f_compassion":
                    return R.drawable.sprite_aling_isabel1_f_compassion;
            }
        }
        else if (imgResource.startsWith("pork_vendor1")) {
            switch (imgResource) {
                case "pork_vendor1_f_normal":
                    return R.drawable.sprite_pork_vendor1_f_normal;
                case "pork_vendor1_f_talking":
                    return R.drawable.sprite_pork_vendor1_f_talking;
                case "pork_vendor1_f_disappointed":
                    return R.drawable.sprite_pork_vendor1_f_disappointed;
            }
        }
        else if (imgResource.startsWith("nene")) {
            switch (imgResource) {
                case "nene_f_disappointed":
                    return R.drawable.sprite_nene1_f_disappointed;
                case "nene_f_relieved":
                    return R.drawable.sprite_nene1_f_relieved;
                case "nene_f_smile":
                    return R.drawable.sprite_nene1_f_smiling;
                case "nene_f_sad":
                    return R.drawable.sprite_nene1_f_sad;
                case "nene_f_normal":
                    return R.drawable.sprite_nene1_f_normal;
            }

        }
        else if (imgResource.startsWith("tito1")) {
            switch (imgResource) {
                case "tito1_f_smile":
                    return R.drawable.sprite_tito1_f_smile;
                case "tito1_f_normal":
                    return R.drawable.sprite_tito1_f_normal;
                case "tito1_f_talking":
                    return R.drawable.sprite_tito1_f_talking;
            }
        }
        else if (imgResource.startsWith("kapitan1")) {
            switch (imgResource) {
                case "kapitan1_f_normal":
                    return R.drawable.sprite_kapitan1_f_normal;
                case "kapitan1_f_talking":
                    return R.drawable.sprite_kapitan1_f_talking;
                case "kapitan1_f_smile":
                    return R.drawable.sprite_kapitan1_f_smiling;
                case "kapitan1_f_worried":
                    return R.drawable.sprite_kapitan1_f_worried;
            }
        }
        else if (imgResource.startsWith("tatay")) {
            switch (imgResource) {
                case "tatay_f_smile":
                    return R.drawable.sprite_tatay1_f_smiling;
                case "tatay_f_talking":
                    return R.drawable.sprite_tatay1_f_talking;
                case "tatay_f_confused":
                    return R.drawable.sprite_tatay1_f_confused;
                case "tatay_f_normal":
                    return R.drawable.sprite_tatay1_f_normal;
            }
        }
        else if (imgResource.startsWith("pharma")) {
            switch (imgResource) {
                case "pharma_f_normal":
                    return R.drawable.sprite_pharmacist1_f_normal;
                case "pharma_f_talking":
                    return R.drawable.sprite_pharmacist1_f_talking;
            }
        }
        else if (imgResource.startsWith("rp1")) {
            switch (imgResource) {
                case "rp1_f_normal":
                    return R.drawable.sprite_rp1_f_normal;
                case "rp1_f_curious":
                    return R.drawable.sprite_rp1_f_curious;
                case "rp1_f_talking":
                    return R.drawable.sprite_rp1_f_talking;
            }
        }
        else if (imgResource.startsWith("rp21")) {
            switch (imgResource) {
                case "rp21_f_normal":
                    return R.drawable.sprite_rp21_f_normal;
                case "rp21_f_talking":
                    return R.drawable.sprite_rp21_f_talking;
            }
        }
        else if (imgResource.startsWith("nibble1")) {
            switch (imgResource) {
                case "nibble_f_worried":
                    return R.drawable.sprite_nibble1_f_normal;
                case "nibble_f_normal":
                    return R.drawable.sprite_nibble1_f_normal;
                case "nibble_f_angry":
                    return R.drawable.sprite_nibble1_f_normal;
                case "nibble_f_reminscing":
                    return R.drawable.sprite_nibble1_f_normal;
                case "nibble_f_smile":
                    return R.drawable.sprite_nibble1_f_normal;
            }
        }
        else if (imgResource.startsWith("nibble2")) {
            switch (imgResource) {
                case "nibble_f_worried":
                    return R.drawable.sprite_nibble2_f_cute;
                case "nibble_f_cute":
                    return R.drawable.sprite_nibble2_f_cute;
                case "nibble_f_angry":
                    return R.drawable.sprite_nibble2_f_cute;
                case "nibble_f_reminscing":
                    return R.drawable.sprite_nibble2_f_cute;
                case "nibble_f_smile":
                    return R.drawable.sprite_nibble2_f_cute;
            }
        }
        return R.drawable.sprite_empty_b_empty;
    }

    public static int retrieveBackgroundResource(String imgResource) {
        if ("living_room".equals(imgResource)) return R.drawable.scene_living_room;
        else if ("streets".equals(imgResource)) return R.drawable.scene_streets;
        else if ("classroom".equals(imgResource)) return R.drawable.scene_classroom;
        else if ("school_cafeteria".equals(imgResource)) return R.drawable.scene_school_cafeteria;
        else if ("market".equals(imgResource)) return R.drawable.scene_market;
        else if ("dining_table".equals(imgResource)) return R.drawable.scene_dining_table;
        else if ("balcon".equals(imgResource)) return R.drawable.scene_home;
        else if ("bedroom".equals(imgResource)) return R.drawable.scene_bedroom;
        else if ("pharmacy".equals(imgResource)) return R.drawable.scene_market;
        else if ("barangay_hall".equals(imgResource)) return R.drawable.scene_streets;
        else if ("grocery_store".equals(imgResource)) return R.drawable.scene_market;
        else return R.drawable.scene_black_screen; // this is a default background resource
    }

    @SuppressWarnings("unused")
    public static int retrievePropResource(String imgResource) {
        return R.drawable.sprite_empty_b_empty;
    }

    public static int retrieveDialogueBoxResource(String imgResource) {
        switch (imgResource) {
            case "ALING_ISABEL":
                return R.drawable.gui_textbox_aling_isabel;
            case "EIAN":
                return R.drawable.gui_textbox_eian;
            case "JAXON":
                return R.drawable.gui_textbox_jaxon;
            case "KAPITAN":
                return R.drawable.gui_textbox_kapitan;
            case "[USER_NAME]":
                return R.drawable.gui_textbox_mc;
            case "NANAY":
                return R.drawable.gui_textbox_nanay;
            case "NENE":
                return R.drawable.gui_textbox_nene;
            case "NIBBLE":
                return R.drawable.gui_textbox_nibble;
            case "PHARMACIST":
                return R.drawable.gui_textbox_pharmacist;
            case "TINDERA":
                return R.drawable.gui_textbox_pork_vendor;
            case "STUDENTS":
                return R.drawable.gui_textbox_students;
            case "TATAY":
                return R.drawable.gui_textbox_tatay;
            case "TEACHER":
                return R.drawable.gui_textbox_teacher;
            case "TITO":
                return R.drawable.gui_textbox_tito;
            case "EMPTY":
            default:
                return R.drawable.gui_textbox_empty;
        }
    }

    public static int getBGM(String bgmFile) {
        switch (bgmFile) {
            case "market_music":
                return R.raw.bgm_market_music;
            case "market_ambience":
                return R.raw.bgm_market_ambience;
            case "sunset":
                return R.raw.bgm_sunset;
            case "problem_rises":
                return R.raw.bgm_problem_rises;
            case "morning":
                return R.raw.bgm_morning;
            case "cafeteria_music":
                return R.raw.bgm_cafeteria;
            case "cafeteria_ambience":
                return R.raw.bgm_cafeteria_ambience;
            case "learning":
                return R.raw.bgm_learning;
            case "nibble_mystery":
                return R.raw.bgm_nibble_mystery;
            case "discovery":
                return R.raw.bgm_discovery;
            case "taking_quiz":
                return R.raw.bgm_taking_quiz;
            case "failed_quiz":
                return R.raw.bgm_failed_quiz;
            case "passed_quiz":
                return R.raw.bgm_passed_quiz;
            case "idle":
                return R.raw.bgm_idle;
            case "factory":
                return R.raw.bgm_factory;
            case "selling":
                return R.raw.bgm_selling;
            case "menu":
                return R.raw.bgm_menu;
            default:
                return -1;
        }
    }

    public static int getSFX(String sfxFile) {
        if ("school_bell".equals(sfxFile)) return R.raw.sfx_school_bell;
        else return -1;
    }
}
