package com.capstone.merkado.Helpers;

import com.capstone.merkado.DataManager.DataFunctionPackage.StoryDataFunctions;
import com.capstone.merkado.Objects.StoryDataObjects.PlayerStory;
import com.capstone.merkado.Objects.StoryDataObjects.Variable;

import java.util.concurrent.CompletableFuture;

public class StoryVariableHelper {

    public static CompletableFuture<PlayerStory> processVariable(Integer playerId, Integer storyQueue, Variable variable, PlayerStory playerStory) {
        if ("GET".equals(variable.getMethod())) {
            CompletableFuture<Variable> future = new CompletableFuture<>();
            // GET VARIABLE
            StoryDataFunctions.getVariableFromPlayer(playerId, storyQueue, variable).thenAccept(future::complete);
            return future.thenCompose(newVariable -> {
                if (newVariable == null) return CompletableFuture.completedFuture(playerStory);
                PlayerStory newPlayerStory = Processors.process(
                        newVariable.getCode(),
                        stringToObject(newVariable.getValue(), newVariable.getValueType()),
                        playerStory);
                return CompletableFuture.completedFuture(newPlayerStory);
            });
        } else {
            // SET VARIABLE
            StoryDataFunctions.addVariableToPlayer(playerId, storyQueue, variable);
            return CompletableFuture.completedFuture(playerStory);
        }
    }

    public static Object stringToObject(String variable, String variableType) {
        switch (variableType) {
            case "NUMERICAL":
                return Integer.parseInt(variable);
            case "DECIMAL":
                return Float.parseFloat(variable);
            case "BOOLEAN":
                return "TRUE".equals(variable);
            default:
                return variable;
        }
    }

    public static class Processors {

        public static PlayerStory process(String code, Object value, PlayerStory playerStory) {
            if (code.equals("GO_TO_LINEGROUP")) return goToLineGroup((Integer) value, playerStory);
            return playerStory;
        }

        private static PlayerStory goToLineGroup(Integer lineGroupId, PlayerStory playerStory) {
            playerStory.setNextLineGroup(
                    StoryDataFunctions.getLineGroupFromId(playerStory.getChapter().getId(),
                            playerStory.getCurrentScene().getId(),
                            lineGroupId));
            return playerStory;
        }
    }
}
