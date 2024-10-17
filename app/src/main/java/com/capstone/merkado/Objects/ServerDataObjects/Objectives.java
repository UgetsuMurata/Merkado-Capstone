package com.capstone.merkado.Objects.ServerDataObjects;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

@SuppressWarnings("unused")
public class Objectives implements Parcelable {
    Integer id;
    String title, subtitle;
    List<Objective> objectives;

    public Objectives() {
    }

    protected Objectives(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        title = in.readString();
        subtitle = in.readString();
        objectives = in.createTypedArrayList(Objective.CREATOR);
    }

    public static final Creator<Objectives> CREATOR = new Creator<Objectives>() {
        @Override
        public Objectives createFromParcel(Parcel in) {
            return new Objectives(in);
        }

        @Override
        public Objectives[] newArray(int size) {
            return new Objectives[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public List<Objective> getObjectives() {
        return objectives;
    }

    public void setObjectives(List<Objective> objectives) {
        this.objectives = objectives;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(title);
        dest.writeString(subtitle);
        dest.writeTypedList(objectives);
    }

    public static class Objective implements Parcelable {
        Integer id;
        String objective;
        String startTrigger;
        String endTrigger;

        public Objective() {
        }

        protected Objective(Parcel in) {
            if (in.readByte() == 0) {
                id = null;
            } else {
                id = in.readInt();
            }
            objective = in.readString();
            startTrigger = in.readString();
            endTrigger = in.readString();
        }

        public static final Creator<Objective> CREATOR = new Creator<Objective>() {
            @Override
            public Objective createFromParcel(Parcel in) {
                return new Objective(in);
            }

            @Override
            public Objective[] newArray(int size) {
                return new Objective[size];
            }
        };

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getObjective() {
            return objective;
        }

        public void setObjective(String objective) {
            this.objective = objective;
        }

        public String getStartTrigger() {
            return startTrigger;
        }

        public void setStartTrigger(String startTrigger) {
            this.startTrigger = startTrigger;
        }

        public String getEndTrigger() {
            return endTrigger;
        }

        public void setEndTrigger(String endTrigger) {
            this.endTrigger = endTrigger;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            if (id == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(id);
            }
            dest.writeString(objective);
            dest.writeString(startTrigger);
            dest.writeString(endTrigger);
        }
    }
}
