package com.capstone.merkado.DataManager.StaticData;

import com.capstone.merkado.Objects.StoryDataObjects.CharacterImg;
import com.capstone.merkado.Objects.StoryDataObjects.CharacterImg.Character;
import com.capstone.merkado.R;

import java.util.HashMap;

public class CharacterData {
    HashMap<Integer, CharacterImg> characterImgHashMap;
    HashMap<Integer, Character> characterHashMap;

    /**
     * Loads the data ready for retrieval.
     */
    public CharacterData() {
        // SET UP CHARACTER
        characterHashMap = new HashMap<>();
        characterHashMap.put(0, new Character("[MAIN_CHARACTER]", "Main Character"));
        characterHashMap.put(1, new Character("Nay", "Nanay"));

        // SET UP CHARACTER_IMG
        characterImgHashMap = new HashMap<>();
        characterImgHashMap.put(0, new CharacterImg(characterHashMap.get(0), "MC Happy", R.drawable.char_mc_happy));
        characterImgHashMap.put(1, new CharacterImg(characterHashMap.get(1), "Nanay Happy", R.drawable.char_mc_happy));
    }

    /**
     * Retrieves the character image.
     * @param index characterImgId.
     * @return CharacterImg object.
     */
    public CharacterImg getCharacterImg(Integer index){
        return characterImgHashMap.get(index);
    }

    /**
     * Retrieves the character.
     * @param index characterId.
     * @return Character object.
     */
    public Character getCharacter(Integer index) {
        return characterHashMap.get(index);
    }
}
