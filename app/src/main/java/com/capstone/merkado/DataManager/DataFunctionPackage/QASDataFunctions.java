package com.capstone.merkado.DataManager.DataFunctionPackage;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.capstone.merkado.DataManager.FirebaseData;
import com.capstone.merkado.DataManager.StaticData.GameResourceCaller;
import com.capstone.merkado.Objects.PlayerDataObjects.PlayerFBExtractor1;
import com.capstone.merkado.Objects.QASDataObjects.QASItems;
import com.capstone.merkado.Objects.StoryDataObjects.Chapter;
import com.capstone.merkado.Objects.TaskDataObjects.TaskData;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("unused")
public class QASDataFunctions {
    /**
     * Gets task data using its index or id. This is used for story-mode.
     *
     * @param taskId index or id of the task.
     * @return Task instance.
     */
    public static @Nullable TaskData getTaskFromId(Integer taskId) {
        final CompletableFuture<TaskData> future = new CompletableFuture<>();

        FirebaseData firebaseData = new FirebaseData();

        firebaseData.retrieveData(String.format(Locale.getDefault(), "tasks/%d", taskId), dataSnapshot -> {
            if (dataSnapshot == null || !dataSnapshot.exists()) {
                future.complete(null);
                return;
            }
            TaskData task = dataSnapshot.getValue(TaskData.class);
            future.complete(task);
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e("getTaskFromId", String.format("Error occurred when getting future: %s", e));
            return null;
        }
    }

    public static CompletableFuture<List<QASItems>> getAllQuests(Integer playerId) {
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> dataSnapshotFuture = new CompletableFuture<>();

        firebaseData.retrieveData(String.format(Locale.getDefault(), "player/%d/storyQueue", playerId), dataSnapshotFuture::complete);

        return dataSnapshotFuture.thenCompose(dataSnapshot -> {
            Map<Integer, PlayerFBExtractor1.TaskQueue> questsQueueMap = new HashMap<>();
            Integer currentIndex = 0;
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                PlayerFBExtractor1.TaskQueue taskQueue = ds.getValue(PlayerFBExtractor1.TaskQueue.class);
                questsQueueMap.put(currentIndex, taskQueue);
            }
            return processTasksQueue(questsQueueMap);
        });
    }

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

    public static CompletableFuture<List<QASItems>> getAllQuestsAndStories(Integer playerId) {
        CompletableFuture<List<QASItems>> allStoriesFuture = getAllStories(playerId);
        CompletableFuture<List<QASItems>> allQuestsFuture = getAllQuests(playerId);

        return allStoriesFuture.thenCombine(allQuestsFuture, (allStories, allQuests) -> {
            List<QASItems> allItems = new ArrayList<>();
            if (allStories != null) {
                allItems.addAll(allStories);
            }
            if (allQuests != null) {
                allItems.addAll(allQuests);
            }
            return allItems;
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

    private static CompletableFuture<List<QASItems>> processTasksQueue(Map<Integer, PlayerFBExtractor1.TaskQueue> storyQueueMap) {
        Map<String, List<QASItems.QASDetail>> qasDetailsMapping = new HashMap<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Map.Entry<Integer, PlayerFBExtractor1.TaskQueue> entry : storyQueueMap.entrySet()) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                // retrieve the TaskData.
                TaskData taskData = getTaskFromId(entry.getValue().getTask());

                // process the TaskData into QASDetail.
                if (taskData == null)
                    return; // do not include from final list if it cannot be retrieved.

                // process the rewards
                List<QASItems.QASDetail.QASReward> qasRewards = getQasRewards(taskData.getRewards());

                // create the QASDetail
                QASItems.QASDetail qasDetail = new QASItems.QASDetail();
                qasDetail.setQasName(taskData.getTitle());
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


}
