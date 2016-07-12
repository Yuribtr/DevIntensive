package com.softdesign.devintensive.data.managers;

import android.net.Uri;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import java.util.ArrayList;
import java.util.List;

public class UserModelManager {

    public static void saveUserModelToPreferenses(DataManager dataManager, UserModelRes userModel) {
        dataManager.getPreferencesManager().setAuthToken(userModel.getData().getToken());
        dataManager.getPreferencesManager().saveUserId(userModel.getData().getUser().getId());

        int[] userRatings = {
                userModel.getData().getUser().getProfileValues().getRating(),
                userModel.getData().getUser().getProfileValues().getLinesCode(),
                userModel.getData().getUser().getProfileValues().getProjects()
        };
        dataManager.getPreferencesManager().saveUserProfileValues(userRatings);

        List<String> userFields = new ArrayList<>();
        userFields.add(userModel.getData().getUser().getContacts().getPhone());
        userFields.add(userModel.getData().getUser().getContacts().getEmail());
        userFields.add(userModel.getData().getUser().getContacts().getVk());
        if (userModel.getData().getUser().getRepositories().getRepo()!=null) {
            userFields.add(userModel.getData().getUser().getRepositories().getRepo().get(0).getGit());
        } else {
            userFields.add("");
        }
        userFields.add(userModel.getData().getUser().getPublicInfo().getBio());

        dataManager.getPreferencesManager().saveUserFio(userModel.getData().getUser().getFirstName()+" "+userModel.getData().getUser().getSecondName());

        dataManager.getPreferencesManager().saveUserProfileData(userFields);

        Uri uri = Uri.parse(userModel.getData().getUser().getPublicInfo().getPhoto());
        dataManager.getPreferencesManager().saveUserPhoto(uri);


        uri = Uri.parse(userModel.getData().getUser().getPublicInfo().getAvatar());
        dataManager.getPreferencesManager().saveUserAvatar(uri);

    }
}