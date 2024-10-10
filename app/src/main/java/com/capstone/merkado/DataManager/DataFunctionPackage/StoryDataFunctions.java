package com.capstone.merkado.DataManager.DataFunctionPackage;

import androidx.annotation.Nullable;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.FirebaseData;
import com.capstone.merkado.Objects.PlayerDataObjects.PlayerFBExtractor1;
import com.capstone.merkado.Objects.StoryDataObjects.Chapter;
import com.capstone.merkado.Objects.StoryDataObjects.LineGroup;
import com.capstone.merkado.Objects.StoryDataObjects.Variable;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class StoryDataFunctions {
    /**
     * Gets line groups data using its index or id. This is used for story-mode.
     *
     * @param lineGroupIndex index or id of line group.
     * @return LineGroup instance.
     */
    public static @Nullable LineGroup getLineGroupFromId(Long chapterId, Long sceneId, Integer lineGroupIndex) {
        if (chapterId == null || sceneId == null || lineGroupIndex == null) return null;
        Merkado merkado = Merkado.getInstance();
        List<Chapter> chapterList = merkado.getChapterList();
        Chapter chapter;
        if (chapterList.size() > chapterId) chapter = chapterList.get(Math.toIntExact(chapterId));
        else return null;
        Chapter.Scene scene;
        if (chapter.getScenes().size() > sceneId)
            scene = chapter.getScenes().get(Math.toIntExact(sceneId));
        else return null;
        if (scene.getLineGroup().size() > lineGroupIndex)
            return scene.getLineGroup().get(lineGroupIndex);
        else return null;
    }

    /**
     * Gets story data using its index or id. This is used for story-mode.
     *
     * @param chapterId index or id of the story.
     * @return Story instance.
     */
    public static @Nullable Chapter getChapterFromId(Integer chapterId) {
        Merkado merkado = Merkado.getInstance();
        List<Chapter> chapterList = merkado.getChapterList();
        return chapterList.get(chapterId);
    }

    public static void changeCurrentLineGroup(Integer lineGroupId, Integer playerId, Integer storyQueueId) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.setValue(String.format(Locale.getDefault(), "player/%d/storyQueue/%d/currentLineGroup", playerId, storyQueueId), lineGroupId);
    }

    public static void changeNextLineGroup(Integer lineGroupId, Integer playerId, Integer storyQueueId) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.setValue(String.format(Locale.getDefault(), "player/%d/storyQueue/%d/nextLineGroup", playerId, storyQueueId), lineGroupId);
    }

    public static void changeCurrentScene(Integer sceneId, Integer playerId, Integer storyQueueId) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.setValue(String.format(Locale.getDefault(), "player/%d/storyQueue/%d/currentScene", playerId, storyQueueId), sceneId);
    }

    public static void changeNextScene(Integer sceneId, Integer playerId, Integer storyQueueId) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.setValue(String.format(Locale.getDefault(), "player/%d/storyQueue/%d/nextScene", playerId, storyQueueId), sceneId);
    }

    public static void removeStoryQueueId(Integer playerId, Integer storyQueueId) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.removeData(String.format(Locale.getDefault(), "player/%d/storyQueue/%d", playerId, storyQueueId));
        readjustStoryQueueListKeys(String.format(Locale.getDefault(), "player/%d/storyQueue", playerId));
    }

    private static void readjustStoryQueueListKeys(String path) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.retrieveData(path, dataSnapshot -> {
            List<PlayerFBExtractor1.StoryQueue> storyQueueList = new ArrayList<>();
            if (dataSnapshot == null || !dataSnapshot.exists()) return;
            for (DataSnapshot storyQueueDS : dataSnapshot.getChildren()) {
                PlayerFBExtractor1.StoryQueue storyQueue = storyQueueDS.getValue(PlayerFBExtractor1.StoryQueue.class);
                if (storyQueue != null) storyQueueList.add(storyQueue);
            }
            firebaseData.setValue(path, storyQueueList);
        });
    }

    public static void addStoryToPlayer(Integer playerId, PlayerFBExtractor1.StoryQueue storyQueue) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.retrieveData(
                String.format(Locale.getDefault(), "player/%d/storyQueue/", playerId),
                dataSnapshot -> {
                    if (dataSnapshot == null) return;
                    Long index = dataSnapshot.getChildrenCount();
                    firebaseData.setValue(
                            String.format(Locale.getDefault(),
                                    "player/%d/storyQueue/%d", playerId, index),
                            storyQueue
                    );
                });
    }

    public static void addStoryHistoryToPlayer(Integer playerId, Long chapter, Long scene) {
        FirebaseData firebaseData = new FirebaseData();

        PlayerFBExtractor1.StoryQueue storyQueue = new PlayerFBExtractor1.StoryQueue();
        storyQueue.setChapter(Math.toIntExact(chapter));
        storyQueue.setCurrentLineGroup(0);
        storyQueue.setCurrentScene(Math.toIntExact(scene));
        storyQueue.setNextLineGroup(1);
        storyQueue.setNextScene(1);

        firebaseData.retrieveData(
                String.format(Locale.getDefault(), "player/%d/storyHistory/", playerId),
                dataSnapshot -> {
                    if (dataSnapshot == null) return;
                    Long index = dataSnapshot.getChildrenCount();
                    firebaseData.setValue(
                            String.format(Locale.getDefault(),
                                    "player/%d/storyHistory/%d", playerId, index),
                            storyQueue
                    );
                });
    }

    public static void addVariableToPlayer(Integer playerId, Integer storyQueueId, Variable variable) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.setValue(
                String.format(Locale.getDefault(),
                        "player/%d/storyQueue/%d/variableHolder/%s",
                        playerId, storyQueueId, variable.getName()),
                variable
        );
    }

    public static CompletableFuture<Variable> getVariableFromPlayer(Integer playerId, Integer storyQueueId, Variable variable) {
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();

        firebaseData.retrieveData(
                String.format(Locale.getDefault(),
                        "player/%d/storyQueue/%d/variableHolder/%s",
                        playerId, storyQueueId, variable.getName()),
                future::complete
        );

        return future.thenCompose(dataSnapshot -> {
            if (dataSnapshot == null) return CompletableFuture.completedFuture(null);
            return CompletableFuture.completedFuture(dataSnapshot.getValue(Variable.class));
        });
    }
}
