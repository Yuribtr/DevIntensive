package com.softdesign.devintensive.data.network.res;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UserLikeRes implements Parcelable {

    @SerializedName("success")
    @Expose
    public boolean success;
    @SerializedName("data")
    @Expose
    public Data data;

    public class Data implements Parcelable {

        @SerializedName("homeTask")
        @Expose
        public int homeTask;
        @SerializedName("projects")
        @Expose
        public int projects;
        @SerializedName("linesCode")
        @Expose
        public int linesCode;
        @SerializedName("likesBy")
        @Expose
        public List<String> likesBy = new ArrayList<String>();
        @SerializedName("rait")
        @Expose
        public int rait;
        @SerializedName("updated")
        @Expose
        public String updated;


        private String responseMessage;

        @SerializedName("rating")
        @Expose
        public int rating;

        public int getHomeTask() {
            return homeTask;
        }

        public int getProjects() {
            return projects;
        }

        public int getLinesCode() {
            return linesCode;
        }

        public List<String> getLikesBy() {
            return likesBy;
        }

        public int getRait() {
            return rait;
        }

        public String getUpdated() {
            return updated;
        }

        public String getResponseMessage() {
            return responseMessage;
        }

        public void setResponseMessage(String responseMessage) {
            this.responseMessage = responseMessage;
        }

        public int getRating() {
            return rating;
        }

        protected Data(Parcel in) {
            homeTask = in.readInt();
            projects = in.readInt();
            linesCode = in.readInt();
            responseMessage = in.readString();

            if (in.readByte() == 0x01) {
                likesBy = new ArrayList<String>();
                in.readList(likesBy, String.class.getClassLoader());
            } else {
                likesBy = null;
            }
            rait = in.readInt();
            updated = in.readString();
            rating = in.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(homeTask);
            dest.writeInt(projects);
            dest.writeInt(linesCode);
            dest.writeString(responseMessage);
            if (likesBy == null) {
                dest.writeByte((byte) (0x00));
            } else {
                dest.writeByte((byte) (0x01));
                dest.writeList(likesBy);
            }
            dest.writeInt(rait);
            dest.writeString(updated);
            dest.writeInt(rating);
        }

        @SuppressWarnings("unused")
        public final Parcelable.Creator<Data> CREATOR = new Parcelable.Creator<Data>() {
            @Override
            public Data createFromParcel(Parcel in) {
                return new Data(in);
            }

            @Override
            public Data[] newArray(int size) {
                return new Data[size];
            }
        };
    }

    public boolean isSuccess() {
        return success;
    }

    public Data getData() {
        return data;
    }

    protected UserLikeRes(Parcel in) {
        success = in.readByte() != 0x00;
        data = (Data) in.readValue(Data.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (success ? 0x01 : 0x00));
        dest.writeValue(data);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserLikeRes> CREATOR = new Parcelable.Creator<UserLikeRes>() {
        @Override
        public UserLikeRes createFromParcel(Parcel in) {
            return new UserLikeRes(in);
        }

        @Override
        public UserLikeRes[] newArray(int size) {
            return new UserLikeRes[size];
        }
    };
}