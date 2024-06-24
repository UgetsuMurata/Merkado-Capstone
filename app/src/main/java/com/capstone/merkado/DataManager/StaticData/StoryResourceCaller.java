package com.capstone.merkado.DataManager.StaticData;

import com.capstone.merkado.R;

public class StoryResourceCaller {

    public static int retrieveCharacterBodyResource(String imgResource) {
        if (imgResource == null) return R.drawable.sprite_body_empty;
        if (imgResource.startsWith("mc_sample1")) {
            if ("mc_sample1_pajamas".equals(imgResource))
                return R.drawable.sprite_body_mc_sample1_pajamas;
            else if ("mc_sample1_uniform".equals(imgResource))
                return R.drawable.sprite_body_mc_sample1_uniform;
        } else if (imgResource.startsWith("mc_sample2")) {
            if ("mc_sample2_uniform".equals(imgResource))
                return R.drawable.sprite_body_mc_sample2_uniform;
        } else if (imgResource.startsWith("nanay_sample1")) {
            if ("nanay_sample1_home".equals(imgResource))
                return R.drawable.sprite_body_nanay_sample1_home;
        } else if (imgResource.startsWith("teacher")) {
            if ("teacher_uniform".equals(imgResource))
                return R.drawable.sprite_body_teacher_uniform;
        } else if (imgResource.startsWith("friend1")) {
            if ("friend1_uniform".equals(imgResource))
                return R.drawable.sprite_body_friend1_uniform;
        } else if (imgResource.startsWith("friend2")) {
            if ("friend2_uniform".equals(imgResource))
                return R.drawable.sprite_body_friend2_uniform;
        }
        return -1;
    }

    public static int retrieveCharacterFaceResource(String imgResource) {
        if (imgResource == null) return R.drawable.sprite_body_empty;
        if (imgResource.startsWith("mc_sample1")) {
            if ("mc_sample1_compassion".equals(imgResource))
                return R.drawable.sprite_face_mc_sample1_compassion;
            else if ("mc_sample1_normal".equals(imgResource))
                return R.drawable.sprite_face_mc_sample1_normal;
            else if ("mc_sample1_sad".equals(imgResource))
                return R.drawable.sprite_face_mc_sample1_sad;
            else if ("mc_sample1_sleepy".equals(imgResource))
                return R.drawable.sprite_face_mc_sample1_sleepy;
            else if ("mc_sample1_confused".equals(imgResource))
                return R.drawable.sprite_face_mc_sample1_confused;
        } else if (imgResource.startsWith("mc_sample2")) {
            if ("mc_sample2_thinking".equals(imgResource))
                return R.drawable.sprite_face_mc_sample2_thinking;
            else if ("mc_sample2_thinking2".equals(imgResource))
                return R.drawable.sprite_face_mc_sample2_thinking2;
        } else if (imgResource.startsWith("nanay_sample1")) {
            if ("nanay_sample1_confused".equals(imgResource))
                return R.drawable.sprite_face_nanay_sample1_confused;
            else if ("nanay_sample1_talking".equals(imgResource))
                return R.drawable.sprite_face_nanay_sample1_talking;
            else if ("nanay_sample1_talking2".equals(imgResource))
                return R.drawable.sprite_face_nanay_sample1_talking2;
            else if ("nanay_sample1_angry".equals(imgResource))
                return R.drawable.sprite_face_nanay_sample1_angry;
        } else if (imgResource.startsWith("teacher")) {
            if ("teacher_compassion".equals(imgResource))
                return R.drawable.sprite_face_teacher_compassion;
            else if ("teacher_laugh".equals(imgResource))
                return R.drawable.sprite_face_teacher_laugh;
            else if ("teacher_talking1".equals(imgResource))
                return R.drawable.sprite_face_teacher_talking1;
            else if ("teacher_talking2".equals(imgResource))
                return R.drawable.sprite_face_teacher_talking2;
        } else if (imgResource.startsWith("friend1")) {
            if ("friend1_compassion".equals(imgResource))
                return R.drawable.sprite_face_friend1_compassion;
            else if ("friend1_confused".equals(imgResource))
                return R.drawable.sprite_face_friend1_confused;
            else if ("friend1_normal".equals(imgResource))
                return R.drawable.sprite_face_friend1_normal;
            else if ("friend1_talking".equals(imgResource))
                return R.drawable.sprite_face_friend1_talking;
        } else if (imgResource.startsWith("friend2")) {
            if ("friend2_compassion".equals(imgResource))
                return R.drawable.sprite_face_friend2_compassion;
            else if ("friend2_confused".equals(imgResource))
                return R.drawable.sprite_face_friend2_confused;
            else if ("friend2_normal".equals(imgResource))
                return R.drawable.sprite_face_friend2_normal;
            else if ("friend2_talking".equals(imgResource))
                return R.drawable.sprite_face_friend2_talking;
        }
        return R.drawable.sprite_body_empty;
    }

    public static int retrieveBackgroundResource(String imgResource) {
        if ("bedroom".equals(imgResource)) return R.drawable.scene_bedroom;
        else if ("school".equals(imgResource)) return R.drawable.scene_school;
        else if ("school cafeteria".equals(imgResource)) return R.drawable.scene_school_cafeteria;
        else if ("black screen".equals(imgResource)) return R.drawable.scene_black_screen;
        else return R.drawable.bg_splash_screen; // this is a default background resource
    }

    public static int retrievePropResource(String imgResource) {
        if ("friend1_sweat".equals(imgResource)) return R.drawable.sprite_prop_friend1_sweat;
        else if ("friend2_camera".equals(imgResource)) return R.drawable.sprite_prop_friend2_camera;
        else return R.drawable.sprite_body_empty;
    }

    public static int retrieveDialogueBoxResource(String imgResource) {
        if ("NANAY".equals(imgResource)) return R.drawable.gui_textbox_nanay;
        else if ("[USER_NAME]".equals(imgResource)) return R.drawable.gui_textbox_mc;
        else if ("TEACHER".equals(imgResource)) return R.drawable.gui_textbox_teacher;
        else if ("STUDENTS".equals(imgResource)) return R.drawable.gui_textbox_students;
        else return R.drawable.gui_textbox_empty;
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
