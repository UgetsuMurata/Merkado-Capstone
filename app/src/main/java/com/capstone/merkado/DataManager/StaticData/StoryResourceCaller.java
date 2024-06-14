package com.capstone.merkado.DataManager.StaticData;

import com.capstone.merkado.R;

public class StoryResourceCaller {

    public static int retrieveCharacterBodyResource(String imgResource) {
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
        }
        return -1;
    }

    public static int retrieveCharacterFaceResource(String imgResource) {
        if (imgResource.startsWith("mc_sample1")) {
            if ("mc_sample1_compassion".equals(imgResource))
                return R.drawable.sprite_face_mc_sample1_compassion;
            else if ("mc_sample1_normal".equals(imgResource))
                return R.drawable.sprite_face_mc_sample1_normal;
            else if ("mc_sample1_sad".equals(imgResource))
                return R.drawable.sprite_face_mc_sample1_sad;
            else if ("mc_sample1_sleepy".equals(imgResource))
                return R.drawable.sprite_face_mc_sample1_sleepy;
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
        }
        return -1;
    }

    public static int retrieveBackgroundResource(String imgResource) {
        if ("bedroom".equals(imgResource)) return R.drawable.scene_bedroom;
        else return -1;
    }

    public static int retrieveDialogueBoxResource(String imgResource) {
        if ("NANAY".equals(imgResource)) return R.drawable.gui_textbox_nanay;
        else if ("[USER_NAME]".equals(imgResource)) return R.drawable.gui_textbox_mc;
        else return R.drawable.gui_textbox_empty;
    }
}
