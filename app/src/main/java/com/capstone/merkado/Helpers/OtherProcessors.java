package com.capstone.merkado.Helpers;

import static com.capstone.merkado.DataManager.DataFunctionPackage.QASDataFunctions.processTasksQueue;

import androidx.annotation.Nullable;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.StaticData.LevelMaxSetter;
import com.capstone.merkado.Objects.QASDataObjects.QASItems;
import com.capstone.merkado.Objects.ResourceDataObjects.Inventory;
import com.capstone.merkado.Objects.ResourceDataObjects.ResourceDisplayMode;
import com.capstone.merkado.Objects.StoresDataObjects.PlayerMarkets;
import com.capstone.merkado.Objects.TaskDataObjects.PlayerTask;
import com.capstone.merkado.Objects.TaskDataObjects.TaskData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class OtherProcessors {
    public static class TimeProcessors {
        public static long getCurrentDay() {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MILLISECOND, Math.toIntExact(Merkado.getInstance().getServerTimeOffset()));
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar.getTimeInMillis();
        }
    }

    public static class TaskProcessors {
        public static List<PlayerTask> generate5Tasks(List<TaskData> taskDataList, Long exp) {
            Integer playerLevel = LevelMaxSetter.getPlayerLevel(exp) + 1;
            List<TaskData> taskData = filterByLevel(taskDataList, playerLevel);
            List<PlayerTask> generatedTasks = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                generatedTasks.add(processTaskData(getRandomItem(taskData), TaskDifficulty.EASY, i));
            }
            return generatedTasks;
        }

        public static CompletableFuture<List<QASItems>> PlayerTaskToQASItems(List<PlayerTask> playerTaskList) {
            Map<Integer, PlayerTask> questsQueueMap = new HashMap<>();
            Integer currentIndex = 0;
            for (PlayerTask playerTask : playerTaskList) {
                questsQueueMap.put(currentIndex, playerTask);
            }
            return processTasksQueue(questsQueueMap);
        }

        private static List<TaskData> filterByLevel(List<TaskData> taskDataList, Integer level) {
            return taskDataList.stream()
                    .filter(task -> task.getLevelRequirement() != null && task.getLevelRequirement() <= level)
                    .collect(Collectors.toList());
        }

        @SuppressWarnings("SameParameterValue")
        private static PlayerTask processTaskData(TaskData taskData, TaskDifficulty difficulty, int id) {
            PlayerTask playerTask = new PlayerTask();
            playerTask.setQueueId(id);
            playerTask.setTaskId(taskData.getTaskID());
            playerTask.setDone(false);
            playerTask.setTaskNote(generateNote(taskData.getNote(), difficulty));

            return playerTask;
        }

        private static String generateNote(@Nullable String note, TaskDifficulty difficulty) {
            if ("{$ITEM_EDIBLE}".equals(note)) {
                switch (difficulty) {
                    case EASY:
                        return "ITEM_EDIBLE=" + getRandomItem(Arrays.asList(2, 3, 4, 5));
                    case MODERATE:
                        return "ITEM_EDIBLE=" + getRandomItem(Arrays.asList(2, 3, 4, 5, 6, 7, 8, 9, 6, 7, 8, 9));
                    case DIFFICULT:
                        return "ITEM_EDIBLE=" + getRandomItem(Arrays.asList(2, 3, 4, 5, 6, 7, 8, 9, 6, 7, 8, 9, 10, 11, 12, 13, 10, 11, 12, 13, 10, 11, 12, 13));
                }
            } else if ("{$ITEM_RESOURCE}".equals(note)) {
                switch (difficulty) {
                    case EASY:
                        return "ITEM_RESOURCE=" + getRandomItem(Arrays.asList(14, 15, 16));
                    case MODERATE:
                        return "ITEM_RESOURCE=" + getRandomItem(Arrays.asList(14, 15, 16, 17, 18, 19, 17, 18, 19));
                    case DIFFICULT:
                        return "ITEM_RESOURCE=" + getRandomItem(Arrays.asList(14, 15, 16, 17, 18, 19, 17, 18, 19, 20, 21, 22, 23, 20, 21, 22, 23, 20, 21, 22, 23));
                }
            } else if ("{$STORY_PART}".equals(note)) {
                switch (difficulty) {
                    case EASY:
                        return "STORY_PART=SCENE";
                    case MODERATE:
                        return String.format("STORY_PART=%s", getRandomItem(Arrays.asList("CHAPTER", "SCENE")));
                    case DIFFICULT:
                        return "STORY_PART=CHAPTER";
                }
            }
            return null;
        }

        private static <T> T getRandomItem(List<T> list) {
            Random random = new Random();
            int randomIndex = random.nextInt(list.size());
            return list.get(randomIndex);
        }

        public enum TaskDifficulty {
            EASY, MODERATE, DIFFICULT
        }
    }

    public static class StoreProcessors {
        public static List<PlayerMarkets.OnSale> filterSaleList(List<PlayerMarkets.OnSale> onSaleList, ResourceDisplayMode resourceDisplayMode) {
            if (onSaleList == null) return new ArrayList<>();
            switch (resourceDisplayMode) {
                case COLLECTIBLES:
                    return onSaleList.stream()
                            .filter(onSale -> {
                                if (onSale == null) return false;
                                return "COLLECTIBLE".equalsIgnoreCase(onSale.getType());
                            })
                            .collect(Collectors.toList());
                case EDIBLES:
                    return onSaleList.stream()
                            .filter(onSale -> {
                                if (onSale == null) return false;
                                return "EDIBLE".equalsIgnoreCase(onSale.getType());
                            })
                            .collect(Collectors.toList());
                case RESOURCES:
                    return onSaleList.stream()
                            .filter(onSale -> {
                                if (onSale == null) return false;
                                return "RESOURCE".equalsIgnoreCase(onSale.getType());
                            })
                            .collect(Collectors.toList());
                default:
                    return new ArrayList<>();
            }
        }
    }

    public static class InventoryProcessors {
        public static Boolean isInventoryDisabled(Inventory inventory, Disable disable) {
            switch (disable) {
                case SELLABLE:
                    return inventory.getSellable();
                case UNSELLABLE:
                    return !inventory.getSellable();
                case NONE:
                default:
                    return false;
            }
        }

        public enum Disable {
            NONE, SELLABLE, UNSELLABLE
        }
    }
}
