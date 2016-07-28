package com.softdesign.devintensive.data.storage.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.utils.UiHelper;
import java.util.ArrayList;
import java.util.List;


public class UserDTO implements Parcelable {
    private String mPhoto;
    private String mFullName;
    private String mRating;
    private String mCodeLines;
    //private String mLikesCount;
    private String mIsMyLike;
    private String mProjects;
    private String mBio;
    private String mRemoteId;
    private List<String> mRepositories;
    private List<String> mLikesBy;

    public UserDTO(User userData) {
        List<String> repoLink = new ArrayList<>();
        List<String> likesBy = new ArrayList<>();
        mRemoteId = userData.getRemoteId();
        mPhoto = userData.getPhoto();
        mFullName = userData.getFullName();
        mRating = String.valueOf(userData.getRating());
        mCodeLines = String.valueOf(userData.getCodeLines());

        String mMyUserId = DataManager.getInstance().getPreferencesManager().getUserId();

        UiHelper.writeLog("UserDTO constructor mIsMyLike: "+String.valueOf(mIsMyLike));

        mProjects = String.valueOf(userData.getProjects());
        mBio = userData.getBio();
        for (Repository gitLink : userData.getRepositories()) {
            repoLink.add(gitLink.getRepositoryName());
        }
        mRepositories = repoLink;

        //инициализируем начальным значением, которое может поменяться
        mIsMyLike=String.valueOf(false);
        for (LikesBy like : userData.getUserLiked()) {
            likesBy.add(like.getUserRemoteId());
            if (like.getUserLiked().equals(mMyUserId)) mIsMyLike=String.valueOf(true);
        }
        mLikesBy = likesBy;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public String getFullName() {
        return mFullName;
    }

    public String getRating() {
        return mRating;
    }

    public String getCodeLines() {
        return mCodeLines;
    }


    public String getIsMyLike() {
        return mIsMyLike;
    }

    public String getProjects() {
        return mProjects;
    }

    public String getBio() {
        return mBio;
    }

    public String getRemoteId() {
        return mRemoteId;
    }

    public List<String> getRepositories() {
        return mRepositories;
    }

    public List<String> getLikesBy() {
        return mLikesBy;
    }

    public void setPhoto(String photo) {
        mPhoto = photo;
    }

    public void setFullName(String fullName) {
        mFullName = fullName;
    }

    public void setRating(String rating) {
        mRating = rating;
    }

    public void setCodeLines(String codeLines) {
        mCodeLines = codeLines;
    }


    public void setIsMyLike(String isMyLike) {
        mIsMyLike = isMyLike;
    }

    public void setProjects(String projects) {
        mProjects = projects;
    }

    public void setBio(String bio) {
        mBio = bio;
    }

    public void setRemoteId(String remoteId) {
        mRemoteId = remoteId;
    }

    public void setRepositories(List<String> repositories) {
        mRepositories = repositories;
    }

    public void setLikesBy(List<String> likesBy) {
        mLikesBy = likesBy;
    }

    protected UserDTO(Parcel in) {
        mPhoto = in.readString();
        mFullName = in.readString();
        mRating = in.readString();
        mCodeLines = in.readString();
        mIsMyLike = in.readString();
        mProjects = in.readString();
        mBio = in.readString();
        mRemoteId = in.readString();
        if (in.readByte() == 0x01) {
            mRepositories = new ArrayList<String>();
            in.readList(mRepositories, String.class.getClassLoader());
        } else {
            mRepositories = null;
        }
        if (in.readByte() == 0x01) {
            mLikesBy = new ArrayList<String>();
            in.readList(mLikesBy, String.class.getClassLoader());
        } else {
            mLikesBy = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPhoto);
        dest.writeString(mFullName);
        dest.writeString(mRating);
        dest.writeString(mCodeLines);
        dest.writeString(mIsMyLike);
        dest.writeString(mProjects);
        dest.writeString(mBio);
        dest.writeString(mRemoteId);
        if (mRepositories == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mRepositories);
        }
        if (mLikesBy == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mLikesBy);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserDTO> CREATOR = new Parcelable.Creator<UserDTO>() {
        @Override
        public UserDTO createFromParcel(Parcel in) {
            return new UserDTO(in);
        }

        @Override
        public UserDTO[] newArray(int size) {
            return new UserDTO[size];
        }
    };
}