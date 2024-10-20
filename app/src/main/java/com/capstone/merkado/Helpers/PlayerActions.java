package com.capstone.merkado.Helpers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.DataFunctionPackage.QASDataFunctions;
import com.capstone.merkado.DataManager.DataFunctionPackage.StoreDataFunctions;
import com.capstone.merkado.Objects.QASDataObjects.QASItems;
import com.capstone.merkado.Objects.StoryDataObjects.Chapter;
import com.capstone.merkado.Objects.TaskDataObjects.PlayerTask;
import com.capstone.merkado.Objects.TaskDataObjects.TaskData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

        public static void createResource(Integer resourceId, Integer quantity) {
            // update supply
            StoreDataFunctions.addSupplyToResource(SERVER(), resourceId, quantity);
        }
    }

    public static class Task {
        Integer counterTask0, counterTask1, counterTask2, counterTask3, counterTask4;
        List<PlayerTask> playerTaskList;
        List<TaskData> taskDataList;
        OnTaskSuccess onTaskSuccess;
        Merkado merkado;

        public Task() {
            counterTask0 = 0;
            counterTask1 = 0;
            counterTask2 = 0;
            counterTask3 = 0;
            counterTask4 = 0;
            merkado = Merkado.getInstance();
            playerTaskList = extractPlayerTasks(merkado.getTaskQASList());
            taskDataList = merkado.getTaskDataList();
            sortList();
        }

        private void sortList() {
            this.playerTaskList = this.playerTaskList.stream()
                    .sorted(Comparator.comparing(PlayerTask::getQueueId))
                    .collect(Collectors.toList());
        }

        private List<PlayerTask> extractPlayerTasks(List<QASItems> items) {
            List<PlayerTask> playerTask = new ArrayList<>();
            for (QASItems qasItems : items) {
                for (QASItems.QASDetail detail : qasItems.getQasDetails()) {
                    Object obj = detail.getOriginalObject();
                    if (obj instanceof PlayerTask) {
                        playerTask.add((PlayerTask) obj);
                    }
                }
            }
            return playerTask;
        }

        /**
         * Checks if activity is in task list and updates it.
         * @param activity PlayerActivity.SELLING, PlayerActivity.BUYING, PlayerActivity.READING.
         * @param quantity amount of the activity.
         * @param requirementCode other details that may be needed.
         */
        public void taskActivity(PlayerActivity activity, Integer quantity, String requirementCode) {
            switch (activity) {
                case SELLING:
                    crosscheckSelling(quantity, requirementCode);
                    break;
                case BUYING:
                    crosscheckBuying(quantity, requirementCode);
                    break;
                case READING:
                    crosscheckReading(quantity, requirementCode);
                    break;
            }
        }

        public void setOnTaskSuccess(OnTaskSuccess onTaskSuccess) {
            this.onTaskSuccess = onTaskSuccess;
        }

        public static String generateRequirementCodeFromResource(Integer resourceId){
            if (resourceId >= 2 && resourceId <= 13) return "ITEM_EDIBLE="+resourceId;
            if (resourceId >= 14 && resourceId <= 23) return "ITEM_RESOURCE="+resourceId;
            return null;
        }

        public static String generateRequirementCodeFromStory(String part) {
            return "STORY_PART="+part;
        }

        private void crosscheckSelling(Integer quantity, String requirementCode) {
            // TASK IDS: 2, 3
            List<PlayerTask> playerTasks = filter(Arrays.asList(2, 3));
            if (playerTasks.isEmpty()) return;
            for (PlayerTask playerTask : playerTasks) {
                if (playerTask.getDone()) continue;
                if (!playerTask.getTaskNote().equals(requirementCode)) continue;
                addToCounter(playerTask.getQueueId(), quantity);
                switch (playerTask.getTaskId()) {
                    case 2:
                    case 3:
                        Integer counter = getCounter(playerTask.getQueueId());
                        if (counter != null && counter >= 3)
                            doneQueue(playerTask.getQueueId());
                }
            }
        }

        private void crosscheckBuying(Integer quantity, String requirementCode) {
            // TASK IDS: 0, 1
            List<PlayerTask> playerTasks = filter(Arrays.asList(0, 1));
            if (playerTasks.isEmpty()) return;
            for (PlayerTask playerTask : playerTasks) {
                if (playerTask.getDone()) continue;
                if (!playerTask.getTaskNote().equals(requirementCode)) continue;
                addToCounter(playerTask.getQueueId(), quantity);
                switch (playerTask.getTaskId()) {
                    case 0:
                    case 1:
                        Integer counter = getCounter(playerTask.getQueueId());
                        if (counter != null && counter >= 3)
                            doneQueue(playerTask.getQueueId());
                }
            }
        }

        @SuppressWarnings("SwitchStatementWithTooFewBranches")
        private void crosscheckReading(Integer quantity, String requirementCode) {
            // TASK IDS: 4
            List<PlayerTask> playerTasks = filter(Collections.singletonList(4));
            if (playerTasks.isEmpty()) return;
            for (PlayerTask playerTask : playerTasks) {
                if (playerTask.getDone()) continue;
                if (!playerTask.getTaskNote().equals(requirementCode)) continue;
                addToCounter(playerTask.getQueueId(), quantity);
                switch (playerTask.getTaskId()) {
                    case 4:
                        Integer counter = getCounter(playerTask.getQueueId());
                        if (counter != null && counter >= 1)
                            doneQueue(playerTask.getQueueId());
                }
            }
        }

        private void doneQueue(Integer id) {
            this.playerTaskList.get(id).setDone(true);
            TaskData taskData = this.taskDataList.get(this.playerTaskList.get(id).getTaskId());
            if (onTaskSuccess != null)
                onTaskSuccess.done(taskData.getRewards());
            QASDataFunctions.saveAllQuests(merkado.getPlayerId(), this.playerTaskList);
        }

        private void addToCounter(Integer id, Integer quantity) {
            switch (id) {
                case 0:
                    counterTask0 += quantity;
                case 1:
                    counterTask1 += quantity;
                case 2:
                    counterTask2 += quantity;
                case 3:
                    counterTask3 += quantity;
                case 4:
                    counterTask4 += quantity;
            }
        }

        private @Nullable Integer getCounter(Integer id) {
            switch (id) {
                case 0:
                    return counterTask0;
                case 1:
                    return counterTask1;
                case 2:
                    return counterTask2;
                case 3:
                    return counterTask3;
                case 4:
                    return counterTask4;
                default:
                    return null;
            }
        }

        private List<PlayerTask> filter(@NonNull List<Integer> ids) {
            return this.playerTaskList.stream()
                    .filter(task -> ids.contains(task.getTaskId()))
                    .collect(Collectors.toList());
        }

        public interface OnTaskSuccess {
            void done(List<Chapter.GameRewards> gameRewards);
        }

        public enum PlayerActivity {
            SELLING, BUYING, READING
        }
    }
}
