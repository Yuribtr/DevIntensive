package com.softdesign.devintensive.data.network.res;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UserListRes implements Parcelable {

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("data")
    @Expose
    private ArrayList<Datum> data = new ArrayList<Datum>();

    public ArrayList<Datum> getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }

    public class Datum implements Parcelable {
        
        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("first_name")
        @Expose
        private String firstName;
        @SerializedName("second_name")
        @Expose
        private String secondName;
        @SerializedName("__v")
        @Expose
        private int v;
        @SerializedName("repositories")
        @Expose
        private Repositories repositories;
        @SerializedName("profileValues")
        @Expose
        private ProfileValues profileValues;
        @SerializedName("publicInfo")
        @Expose
        private PublicInfo publicInfo;
        @SerializedName("specialization")
        @Expose
        private String specialization;
        @SerializedName("updated")
        @Expose
        private String updated;

        public String getId() {
            return id;
        }

        public PublicInfo getPublicInfo() {
            return publicInfo;
        }

        public Repositories getRepositories() {
            return repositories;
        }

        public ProfileValues getProfileValues() {
            return profileValues;
        }

        public String getFullName() {
            return firstName + " " + secondName;
        }

        public class Repositories implements Parcelable {
            @SerializedName("repo")
            @Expose
            private List<Repo> repo = new ArrayList<Repo>();
            @SerializedName("updated")
            @Expose
            private String updated;

            public List<Repo> getRepo() {
                return repo;
            }



            protected Repositories(Parcel in) {
                if (in.readByte() == 0x01) {
                    repo = new ArrayList<Repo>();
                    in.readList(repo, Repo.class.getClassLoader());
                } else {
                    repo = null;
                }
                updated = in.readString();
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                if (repo == null) {
                    dest.writeByte((byte) (0x00));
                } else {
                    dest.writeByte((byte) (0x01));
                    dest.writeList(repo);
                }
                dest.writeString(updated);
            }

            @SuppressWarnings("unused")
            public final Parcelable.Creator<Repositories> CREATOR = new Parcelable.Creator<Repositories>() {
                @Override
                public Repositories createFromParcel(Parcel in) {
                    return new Repositories(in);
                }

                @Override
                public Repositories[] newArray(int size) {
                    return new Repositories[size];
                }
            };
        }


        public class Repo implements Parcelable {
            @SerializedName("_id")
            @Expose
            private String id;
            @SerializedName("git")
            @Expose
            private String git;
            @SerializedName("title")
            @Expose
            private String title;

            public String getGit() {
                return git;
            }

            public String getId() {
                return id;
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
            private String bio;
            @SerializedName("avatar")
            @Expose
            private String avatar;
            @SerializedName("photo")
            @Expose
            private String photo;
            @SerializedName("updated")
            @Expose
            private String updated;

            public String getBio() {
                return bio;
            }

            public String getAvatar() {
                return avatar;
            }

            public String getPhoto() {
                return photo;
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
            private int homeTask;
            @SerializedName("projects")
            @Expose
            private int projects;
            @SerializedName("linesCode")
            @Expose
            private int linesCode;
            @SerializedName("rait")
            @Expose
            private int rait;
            @SerializedName("updated")
            @Expose
            private String updated;

            public int getProjects() {
                return projects;
            }

            public int getLinesCode() {
                return linesCode;
            }

            public int getRating() {
                return rait;
            }

            protected ProfileValues(Parcel in) {
                homeTask = in.readInt();
                projects = in.readInt();
                linesCode = in.readInt();
                rait = in.readInt();
                updated = in.readString();
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
                dest.writeInt(rait);
                dest.writeString(updated);
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