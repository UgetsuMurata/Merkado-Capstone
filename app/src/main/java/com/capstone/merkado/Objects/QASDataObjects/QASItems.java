package com.capstone.merkado.Objects.QASDataObjects;

import java.util.List;

public class QASItems {
    String qasCategory;
    String qasGroup;
    List<QASDetail> qasDetails;

    public QASItems() {
    }

    public String getQasCategory() {
        return qasCategory;
    }

    public void setQasCategory(String qasCategory) {
        this.qasCategory = qasCategory;
    }

    public List<QASDetail> getQasDetails() {
        return qasDetails;
    }

    public String getQasGroup() {
        return qasGroup;
    }

    public void setQasGroup(String qasGroup) {
        this.qasGroup = qasGroup;
    }

    public void setQasDetails(List<QASDetail> qasDetails) {
        this.qasDetails = qasDetails;
    }

    public static class QASDetail {
        String qasName;
        String qasShortDescription;
        String qasDescription;
        List<QASReward> qasRewards;
        int queueId;

        public QASDetail() {
        }

        public String getQasName() {
            return qasName;
        }

        public void setQasName(String qasName) {
            this.qasName = qasName;
        }

        public String getQasShortDescription() {
            return qasShortDescription;
        }

        public void setQasShortDescription(String qasShortDescription) {
            this.qasShortDescription = qasShortDescription;
        }

        public String getQasDescription() {
            return qasDescription;
        }

        public void setQasDescription(String qasDescription) {
            this.qasDescription = qasDescription;
        }

        public List<QASReward> getQasRewards() {
            return qasRewards;
        }

        public void setQasRewards(List<QASReward> qasRewards) {
            this.qasRewards = qasRewards;
        }

        public int getQueueId() {
            return queueId;
        }

        public void setQueueId(int queueId) {
            this.queueId = queueId;
        }

        public static class QASReward {
            Long resourceId;
            int resourceImage;
            Long resourceQuantity;

            public QASReward() {
            }

            public Long getResourceId() {
                return resourceId;
            }

            public void setResourceId(Long resourceId) {
                this.resourceId = resourceId;
            }

            public int getResourceImage() {
                return resourceImage;
            }

            public void setResourceImage(int resourceImage) {
                this.resourceImage = resourceImage;
            }

            public Long getResourceQuantity() {
                return resourceQuantity;
            }

            public void setResourceQuantity(Long resourceQuantity) {
                this.resourceQuantity = resourceQuantity;
            }
        }
    }
}
