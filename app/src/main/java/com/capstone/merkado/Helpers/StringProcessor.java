package com.capstone.merkado.Helpers;

import com.capstone.merkado.Application.Merkado;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class StringProcessor {

    /**
     * Processes the placement string of the characters.
     * @param placement raw string.
     * @return HashMap
     */
    public static HashMap<Placement.Label, Placement.Value> extractPlacement(String placement){
        HashMap<Placement.Label, Placement.Value> labelValueHashMap = new HashMap<>();

        String[] placements = placement.split(";");
        Placement.Value slotValue = Placement.Value.SLOT1;
        Placement.Value layerValue = Placement.Value.BODY;

        if ("2".equals(placements[0])) slotValue = Placement.Value.SLOT2;
        else if ("3".equals(placements[0])) slotValue = Placement.Value.SLOT3;
        else if ("4".equals(placements[0])) slotValue = Placement.Value.SLOT4;
        else if ("5".equals(placements[0])) slotValue = Placement.Value.SLOT5;
        else if ("6".equals(placements[0])) slotValue = Placement.Value.SLOT5;

        if ("FACE".equals(placements[1])) layerValue = Placement.Value.FACE;
        else if ("PROP".equals(placements[1])) layerValue = Placement.Value.PROP;

        labelValueHashMap.put(Placement.Label.SLOT, slotValue);
        labelValueHashMap.put(Placement.Label.LAYER, layerValue);

        return labelValueHashMap;
    }

    public static class Placement {
        public enum Label {
            SLOT, LAYER
        }
        public enum Value {
            SLOT1, SLOT2, SLOT3, SLOT4, SLOT5, BODY, FACE, PROP
        }
    }

    /**
     * This method processes the codes in raw dialogues into a displayable one.
     * @param rawDialogue raw dialogue from the database.
     * @return processed string.
     */
    public static String dialogueProcessor(String rawDialogue) {
        String processed = rawDialogue;
        if (rawDialogue.contains("{$USER_NAME}")) {
            processed = rawDialogue.replaceAll("\\{\\$USER_NAME\\}", Merkado.getInstance().getAccount().getUsername());
        }
        return processed;
    }

    /**
     * Converts the number into spaced string. This is for the numbers that uses the font "logo.ttf" which needs to be spaced out for numbers.
     * @param number raw number (<i>Integer</i> datatype).
     * @return Spaced out <i>String</i> of the number.
     */
    public static String numberToSpacedString(Integer number) {
        StringBuilder finalString = new StringBuilder();

        String rawString = String.valueOf(number);
        for (int i = 0; i < rawString.length(); i++) {
            finalString.append(rawString.charAt(i)).append(" ");
        }

        return finalString.toString();
    }

    public static String titleCase(String rawString) {
        if (rawString == null || rawString.isEmpty()) {
            return rawString;
        }

        return Arrays.stream(rawString.split("\\s+"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}
