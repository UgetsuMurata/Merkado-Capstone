package com.capstone.merkado.DataManager.DataFunctionPackage;

import static com.capstone.merkado.Helpers.OtherProcessors.TimeProcessors.getCurrentDay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.FirebaseData;
import com.capstone.merkado.DataManager.StaticData.GameResourceCaller;
import com.capstone.merkado.Objects.PlayerDataObjects.PlayerFBExtractor1;
import com.capstone.merkado.Objects.QASDataObjects.QASItems;
import com.capstone.merkado.Objects.StoryDataObjects.Chapter;
import com.capstone.merkado.Objects.TaskDataObjects.PlayerTask;
import com.capstone.merkado.Objects.TaskDataObjects.TaskData;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class QASDataFunctions {
    public static CompletableFuture<List<QASItems>> getAllStories(Integer playerId) {
        FirebaseData firebaseData = new FirebaseData();

        CompletableFuture<List<QASItems>> storyQueue = getStoryQueue(playerId);
        CompletableFuture<List<QASItems>> storyHistory = getStoryHistory(playerId);

        return storyQueue.thenCombine(storyHistory, (queueList, historyList) -> {
            queueList.addAll(historyList);
            return queueList;
        });
    }

    private static CompletableFuture<List<QASItems>> getStoryQueue(Integer playerId) {
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> dataSnapshotFuture = new CompletableFuture<>();
        firebaseData.retrieveData(String.format(Locale.getDefault(), "player/%d/storyQueue", playerId), dataSnapshotFuture::complete);
        return dataSnapshotFuture.thenCompose(dataSnapshot -> {
            Map<Integer, PlayerFBExtractor1.StoryQueue> storyQueueMap = new HashMap<>();
            Integer currentIndex = 0;
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                PlayerFBExtractor1.StoryQueue storyQueue = ds.getValue(PlayerFBExtractor1.StoryQueue.class);
                storyQueueMap.put(currentIndex, storyQueue);
                currentIndex++;
            }
            return processStoryQueue(storyQueueMap, false);
        });
    }

    private static CompletableFuture<List<QASItems>> getStoryHistory(Integer playerId) {
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> dataSnapshotFuture = new CompletableFuture<>();
        firebaseData.retrieveData(String.format(Locale.getDefault(), "player/%d/storyHistory", playerId), dataSnapshotFuture::complete);
        return dataSnapshotFuture.thenCompose(dataSnapshot -> {
            Map<Integer, PlayerFBExtractor1.StoryQueue> storyQueueMap = new HashMap<>();
            Integer currentIndex = 0;
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                PlayerFBExtractor1.StoryQueue storyQueue = ds.getValue(PlayerFBExtractor1.StoryQueue.class);
                storyQueueMap.put(currentIndex, storyQueue);
                currentIndex++;
            }
            return processStoryQueue(storyQueueMap, true);
        });
    }

    private static CompletableFuture<List<QASItems>> processStoryQueue(Map<Integer, PlayerFBExtractor1.StoryQueue> storyQueueMap, Boolean history) {
        Map<String, List<QASItems.QASDetail>> qasDetailsMapping = new HashMap<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Map.Entry<Integer, PlayerFBExtractor1.StoryQueue> entry : storyQueueMap.entrySet()) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                Chapter chapter = StoryDataFunctions.getChapterFromId(entry.getValue().getChapter());
                if (chapter == null) return;
                Chapter.Scene scene = chapter.getScenes().get(entry.getValue().getCurrentScene());

                // Safely retrieve rewards with a null check
                List<QASItems.QASDetail.QASReward> qasRewards = scene.getRewards() != null ? getQasRewards(scene.getRewards()) : new ArrayList<>();

                QASItems.QASDetail qasDetail = new QASItems.QASDetail();
                qasDetail.setQasName(String.format("%s: %s", chapter.getChapter(), scene.getScene()));
                qasDetail.setQueueId(entry.getKey());
                qasDetail.setQasShortDescription(chapter.getShortDescription());
                qasDetail.setQasDescription(scene.getDescription());
                qasDetail.setQasRewards(qasRewards);
                String key = history ? "[HISTORY]" + chapter.getCategory() : chapter.getCategory();
                List<QASItems.QASDetail> detail;
                if (qasDetailsMapping.containsKey(key)) {
                    detail = qasDetailsMapping.get(key);
                    if (detail == null) detail = new ArrayList<>();
                } else {
                    detail = new ArrayList<>();
                }
                detail.add(qasDetail);
                qasDetailsMapping.put(key, detail);
            });
            futures.add(future);
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    Map<String, QASItems> qasItemsMapping = groupQASDetailsToQASItems("STORIES", qasDetailsMapping);
                    return new ArrayList<>(qasItemsMapping.values());
                });
    }

    /**
     * Gets task data using its index or id. This is used for story-mode.
     *
     * @param taskId index or id of the task.
     * @return Task instance.
     */
    public static @Nullable TaskData getTaskFromId(Integer taskId) {
        List<TaskData> taskDataList = Merkado.getInstance().getTaskDataList();
        return taskDataList.get(taskId);
    }

    public static CompletableFuture<List<QASItems>> getAllQuests(Integer playerId) {
        if (Merkado.getInstance().getTaskQASList() != null) {
            return CompletableFuture.completedFuture(Merkado.getInstance().getTaskQASList());
        }
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> dataSnapshotFuture = new CompletableFuture<>();

        firebaseData.retrieveData(
                String.format(Locale.getDefault(), "player/%d/taskQueue/tasks", playerId),
                dataSnapshotFuture::complete);

        return dataSnapshotFuture.thenCompose(dataSnapshot -> {
            Map<Integer, PlayerTask> questsQueueMap = new HashMap<>();
            Integer currentIndex = 0;
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                PlayerTask playerTask = ds.getValue(PlayerTask.class);
                questsQueueMap.put(currentIndex, playerTask);
                currentIndex++;
            }
            return processTasksQueue(questsQueueMap);
        });
    }

    public static void saveAllQuests(Integer playerId, List<PlayerTask> playerTaskList) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.setValue(
                String.format(Locale.getDefault(), "player/%d/taskQueue/tasks", playerId),
                playerTaskList
        );
        setTaskLastUpdate(playerId);
    }

    public static CompletableFuture<Long> getTaskLastUpdate(Integer playerId) {
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();

        firebaseData.retrieveData(
                String.format(Locale.getDefault(), "player/%d/taskQueue/lastUpdate", playerId),
                future::complete
        );

        return future.thenCompose(dataSnapshot -> {
            if (dataSnapshot == null) return CompletableFuture.completedFuture(null);
            return CompletableFuture.completedFuture(dataSnapshot.getValue(Long.class));
        });
    }

    public static void setTaskLastUpdate(Integer playerId) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.setValue(
                String.format(Locale.getDefault(), "player/%d/taskQueue/lastUpdate", playerId),
                getCurrentDay()
        );
    }

    public static CompletableFuture<List<QASItems>> processTasksQueue(Map<Integer, PlayerTask> storyQueueMap) {
        Map<String, List<QASItems.QASDetail>> qasDetailsMapping = new HashMap<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Map.Entry<Integer, PlayerTask> entry : storyQueueMap.entrySet()) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                // retrieve the TaskData.
                TaskData taskData = getTaskFromId(entry.getValue().getTaskId());
                if (taskData == null)
                    return;

                // process the rewards
                List<QASItems.QASDetail.QASReward> qasRewards = getQasRewards(taskData.getRewards());

                // create the QASDetail
                QASItems.QASDetail qasDetail = new QASItems.QASDetail();
                qasDetail.setQasName(taskData.getTitle());
                qasDetail.setOriginalObject(entry.getValue());
                qasDetail.setQueueId(entry.getKey());
                qasDetail.setQasShortDescription(taskData.getShortDescription());
                qasDetail.setQasDescription(taskData.getDescription());
                qasDetail.setQasRewards(qasRewards);

                // add the detail inside the map.
                List<QASItems.QASDetail> detail;
                if (qasDetailsMapping.containsKey(taskData.getCategory())) {
                    detail = qasDetailsMapping.get(taskData.getCategory());
                    if (detail == null) detail = new ArrayList<>();
                } else {
                    detail = new ArrayList<>();
                }
                detail.add(qasDetail);
                qasDetailsMapping.put(taskData.getCategory(), detail);
            });
            futures.add(future);
        }

        // group the QASDetails into its bigger list, QASItems.
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    Map<String, QASItems> qasItemsMapping = groupQASDetailsToQASItems("QUESTS", qasDetailsMapping);
                    return new ArrayList<>(qasItemsMapping.values());
                });
    }

    private static Map<String, QASItems> groupQASDetailsToQASItems(String group, Map<String, List<QASItems.QASDetail>> qasDetailMap) {
        Map<String, QASItems> qasItemsMapping = new HashMap<>();

        for (Map.Entry<String, List<QASItems.QASDetail>> qasDetailEntry : qasDetailMap.entrySet()) {
            QASItems qasItems = qasItemsMapping.get(qasDetailEntry.getKey());
            boolean isNew = false;

            if (qasItems == null) {
                qasItems = new QASItems();
                qasItems.setQasCategory(qasDetailEntry.getKey());
                qasItems.setQasGroup(group);
                qasItems.setQasDetails(new ArrayList<>());
                isNew = true;
            }

            qasItems.getQasDetails().addAll(qasDetailEntry.getValue());

            if (isNew) qasItemsMapping.put(qasDetailEntry.getKey(), qasItems);
        }

        return qasItemsMapping;
    }

    private static @NonNull List<QASItems.QASDetail.QASReward> getQasRewards(List<Chapter.GameRewards> scene) {
        List<QASItems.QASDetail.QASReward> qasRewards = new ArrayList<>();
        for (Chapter.GameRewards gameRewards : scene) {
            QASItems.QASDetail.QASReward qasReward = new QASItems.QASDetail.QASReward();
            qasReward.setResourceId(gameRewards.getResourceId());
            qasReward.setResourceQuantity(gameRewards.getResourceQuantity());
            qasReward.setResourceImage(GameResourceCaller.getResourcesImage(gameRewards.getResourceId()));
            qasRewards.add(qasReward);
        }
        return qasRewards;
    }
}
