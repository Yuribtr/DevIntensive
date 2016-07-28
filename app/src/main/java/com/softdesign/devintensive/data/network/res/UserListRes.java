package com.softdesign.devintensive.data.network.res;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserListRes implements Parcelable {

    @SerializedName("success")
    @Expose
    public boolean success;
    @SerializedName("data")
    @Expose
    public List<Datum> data = new ArrayList<Datum>();

    public boolean isSuccess() {
        return success;
    }

    public List<Datum> getData() {
        return data;
    }

    public class Repositories {

        @SerializedName("repo")
        @Expose
        public List<Repo> repo = new ArrayList<Repo>();
        @SerializedName("updated")
        @Expose
        public String updated;

        public List<Repo> getRepo() {
            return repo;
        }

        public String getUpdated() {
            return updated;
        }
    }

    public class Datum implements Parcelable {

        @SerializedName("_id")
        @Expose
        public String id;
        @SerializedName("first_name")
        @Expose
        public String firstName;
        @SerializedName("second_name")
        @Expose
        public String secondName;
        @SerializedName("__v")
        @Expose
        public int v;
        @SerializedName("repositories")
        @Expose
        public Repositories repositories;
        @SerializedName("profileValues")
        @Expose
        public ProfileValues profileValues;
        @SerializedName("publicInfo")
        @Expose
        public PublicInfo publicInfo;
        @SerializedName("specialization")
        @Expose
        public String specialization;
        @SerializedName("updated")
        @Expose
        public String updated;

        public String getId() {
            return id;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getSecondName() {
            return secondName;
        }

        public String getFullName() {
            return firstName + " " + secondName;
        }

        public int getV() {
            return v;
        }

        public Repositories getRepositories() {
            return repositories;
        }

        public ProfileValues getProfileValues() {
            return profileValues;
        }

        public PublicInfo getPublicInfo() {
            return publicInfo;
        }

        public String getSpecialization() {
            return specialization;
        }

        public String getUpdated() {
            return updated;
        }

        protected Datum(Parcel in) {
            id = in.readString();
            firstName = in.readString();
            secondName = in.readString();
            v = in.readInt();
            repositories = (Repositories) in.readValue(Repositories.class.getClassLoader());
            profileValues = (ProfileValues) in.readValue(ProfileValues.class.getClassLoader());
            publicInfo = (PublicInfo) in.readValue(PublicInfo.class.getClassLoader());
            specialization = in.readString();
            updated = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(firstName);
            dest.writeString(secondName);
            dest.writeInt(v);
            dest.writeValue(repositories);
            dest.writeValue(profileValues);
            dest.writeValue(publicInfo);
            dest.writeString(specialization);
            dest.writeString(updated);
        }

        @SuppressWarnings("unused")
        public final Parcelable.Creator<Datum> CREATOR = new Parcelable.Creator<Datum>() {
            @Override
            public Datum createFromParcel(Parcel in) {
                return new Datum(in);
            }

            @Override
            public Datum[] newArray(int size) {
                return new Datum[size];
            }
        };
    }


    public class Repo implements Parcelable {

        @SerializedName("_id")
        @Expose
        public String id;
        @SerializedName("git")
        @Expose
        public String git;
        @SerializedName("title")
        @Expose
        public String title;

        public String getId() {
            return id;
        }

        public String getGit() {
            return git;
        }

        public String getTitle() {
            return title;
        }

        protected Repo(Parcel in) {
            id = in.readString();
            git = in.readString();
            title = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(git);
            dest.writeString(title);
        }

        @SuppressWarnings("unused")
        public final Parcelable.Creator<Repo> CREATOR = new Parcelable.Creator<Repo>() {
            @Override
            public Repo createFromParcel(Parcel in) {
                return new Repo(in);
            }

            @Override
            public Repo[] newArray(int size) {
                return new Repo[size];
            }
        };
    }

    public class PublicInfo implements Parcelable {

        @SerializedName("bio")
        @Expose
        public String bio;
        @SerializedName("avatar")
        @Expose
        public String avatar;
        @SerializedName("photo")
        @Expose
        public String photo;
        @SerializedName("updated")
        @Expose
        public String updated;

        public String getBio() {
            return bio;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getPhoto() {
            return photo;
        }

        public String getUpdated() {
            return updated;
        }

        protected PublicInfo(Parcel in) {
            bio = in.readString();
            avatar = in.readString();
            photo = in.readString();
            updated = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(bio);
            dest.writeString(avatar);
            dest.writeString(photo);
            dest.writeString(updated);
        }

        @SuppressWarnings("unused")
        public final Parcelable.Creator<PublicInfo> CREATOR = new Parcelable.Creator<PublicInfo>() {
            @Override
            public PublicInfo createFromParcel(Parcel in) {
                return new PublicInfo(in);
            }

            @Override
            public PublicInfo[] newArray(int size) {
                return new PublicInfo[size];
            }
        };
    }

    public class ProfileValues implements Parcelable {

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

        public int getRating() {
            return rating;
        }

        protected ProfileValues(Parcel in) {
            homeTask = in.readInt();
            projects = in.readInt();
            linesCode = in.readInt();
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
        public final Parcelable.Creator<ProfileValues> CREATOR = new Parcelable.Creator<ProfileValues>() {
            @Override
            public ProfileValues createFromParcel(Parcel in) {
                return new ProfileValues(in);
            }

            @Override
            public ProfileValues[] newArray(int size) {
                return new ProfileValues[size];
            }
        };
    }


    protected UserListRes(Parcel in) {
        success = in.readByte() != 0x00;
        if (in.readByte() == 0x01) {
            data = new ArrayList<Datum>();
            in.readList(data, Datum.class.getClassLoader());
        } else {
            data = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (success ? 0x01 : 0x00));
        if (data == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(data);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserListRes> CREATOR = new Parcelable.Creator<UserListRes>() {
        @Override
        public UserListRes createFromParcel(Parcel in) {
            return new UserListRes(in);
        }

        @Override
        public UserListRes[] newArray(int size) {
            return new UserListRes[size];
        }
    };
}