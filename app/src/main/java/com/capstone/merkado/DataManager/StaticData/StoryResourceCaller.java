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
                    return R.drawable.sprite_mc1_b_pajamas;
            }
        } else if (imgResource.startsWith("mc2")) {
            switch (imgResource) {
                case "mc2_b_uniform":
                    return R.drawable.sprite_mc2_b_uniform;
                case "mc2_b_casual":
                    return R.drawable.sprite_mc2_b_uniform;
            }
        } else if (imgResource.startsWith("nanay1")) {
            switch (imgResource) {
                case "nanay1_b_home":
                    return R.drawable.sprite_nanay1_b_home;
                case "nanay1_b_casual":
                    return R.drawable.sprite_nanay1_b_home;
            }
        } else if (imgResource.startsWith("nanay2")) {
            switch (imgResource) {
                case "nanay2_b_casual":
                    return R.drawable.sprite_nanay1_b_home;
            }
        } else if (imgResource.startsWith("teacher1")) {
            if ("teacher1_b_uniform".equals(imgResource))
                return R.drawable.sprite_teacher1_b_uniform;
        } else if (imgResource.startsWith("jaxon1")) {
            if ("jaxon1_b_uniform".equals(imgResource))
                return R.drawable.sprite_jaxon1_b_uniform;
        } else if (imgResource.startsWith("eian1")) {
            if ("eian1_b_uniform".equals(imgResource))
                return R.drawable.sprite_eian1_b_uniform;
        } else if (imgResource.startsWith("pork_vendor1")) {
            if ("pork_vendor1_b_casual".equals(imgResource))
                return R.drawable.sprite_jaxon1_b_uniform;
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
                    return R.drawable.sprite_mc1_f_compassion;
                case "mc1_f_embarassed":
                    return R.drawable.sprite_mc1_f_sleepy;
                case "mc1_f_nervous":
                    return R.drawable.sprite_mc1_f_confused;
                case "mc1_f_curious":
                    return R.drawable.sprite_mc1_f_normal;
            }
        } else if (imgResource.startsWith("mc2")) {
            switch (imgResource) {
                case "mc2_f_thinking":
                    return R.drawable.sprite_mc2_f_thinking;
                case "mc2_f_thinking2":
                    return R.drawable.sprite_mc2_f_thinking2;
            }
        } else if (imgResource.startsWith("nanay1")) {
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
                    return R.drawable.sprite_nanay1_f_confused;
                case "nanay1_f_normal":
                    return R.drawable.sprite_nanay1_f_angry;
                case "nanay1_f_laughing":
                    return R.drawable.sprite_nanay1_f_talking2;
            }
        } else if (imgResource.startsWith("nanay2")) {
            switch (imgResource) {
                case "nanay2_f_reminiscing":
                    return R.drawable.sprite_nanay1_f_confused;
            }
        } else if (imgResource.startsWith("teacher1")) {
            switch (imgResource) {
                case "teacher1_f_compassion":
                    return R.drawable.sprite_teacher1_f_compassion;
                case "teacher1_f_laugh":
                    return R.drawable.sprite_teacher1_f_laugh;
                case "teacher1_f_talking1":
                    return R.drawable.sprite_teacher1_f_talking1;
                case "teacher1_f_talking2":
                    return R.drawable.sprite_teacher1_f_talking2;
            }
        } else if (imgResource.startsWith("jaxon1")) {
            switch (imgResource) {
                case "jaxon1_f_confused":
                    return R.drawable.sprite_jaxon1_f_confused;
                case "jaxon1_f_normal":
                    return R.drawable.sprite_jaxon1_f_normal;
                case "jaxon1_f_compassion":
                    return R.drawable.sprite_jaxon1_f_compassion;
                case "jaxon1_f_talking":
                    return R.drawable.sprite_jaxon1_f_talking;
            }
        } else if (imgResource.startsWith("eian1")) {
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
        } else if (imgResource.startsWith("pork_vendor1")) {
            switch (imgResource) {
                case "pork_vendor1_f_normal":
                    return R.drawable.sprite_jaxon1_f_normal;
                case "pork_vendor1_f_talking":
                    return R.drawable.sprite_jaxon1_f_talking;
                case "pork_vendor1_f_confused":
                    return R.drawable.sprite_jaxon1_f_confused;
                case "pork_vendor1_f_compassion":
                    return R.drawable.sprite_jaxon1_f_compassion;
            }
        }
        return R.drawable.sprite_empty_b_empty;
    }

    public static int retrieveBackgroundResource(String imgResource) {
        if ("bedroom".equals(imgResource)) return R.drawable.scene_bedroom;
        else if ("black screen".equals(imgResource)) return R.drawable.scene_black_screen;
        else if ("home".equals(imgResource)) return R.drawable.scene_home;
        else if ("market_inside".equals(imgResource)) return R.drawable.scene_market_inside;
        else if ("market_outside".equals(imgResource)) return R.drawable.scene_market_outside;
        else if ("pork_shop".equals(imgResource)) return R.drawable.scene_pork_shop;
        else if ("school".equals(imgResource)) return R.drawable.scene_school;
        else if ("school cafeteria".equals(imgResource)) return R.drawable.scene_school_cafeteria;
        else if ("streets".equals(imgResource)) return R.drawable.scene_streets;
        else return R.drawable.bg_splash_screen; // this is a default background resource
    }

    public static int retrievePropResource(String imgResource) {
        return R.drawable.sprite_empty_b_empty;
    }

    public static int retrieveDialogueBoxResource(String imgResource) {
        switch (imgResource) {
            case "[USER_NAME]":
                return R.drawable.gui_textbox_mc;
            case "NANAY":
                return R.drawable.gui_textbox_nanay;
            case "TEACHER":
                return R.drawable.gui_textbox_teacher;
            case "STUDENTS":
                return R.drawable.gui_textbox_students;
            case "EIAN":
                return R.drawable.gui_textbox_eian;
            case "TINDERA":
                return R.drawable.gui_textbox_pork_vendor;
            default:
                return R.drawable.gui_textbox_empty;
        }
    }

    public static int getBGM(String bgmFile) {
        if ("merkado_theme".equals(bgmFile)) return R.raw.bgm_merkado;
        else return -1;
    }

    public static int getSFX(String sfxFile) {
        if ("school_bell".equals(sfxFile)) return R.raw.sfx_bell_ring;
        else return -1;
    }
}
