package com.softdesign.devintensive.data.database;

import android.content.Context;
import android.support.annotation.NonNull;

import com.redmadrobot.chronos.ChronosOperation;
import com.redmadrobot.chronos.ChronosOperationResult;
import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.storage.models.DaoSession;
import com.softdesign.devintensive.data.storage.models.LikesBy;
import com.softdesign.devintensive.data.storage.models.LikesByDao;
import com.softdesign.devintensive.data.storage.models.Repository;
import com.softdesign.devintensive.data.storage.models.RepositoryDao;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDao;
import com.softdesign.devintensive.utils.DevintensiveApplication;
import com.softdesign.devintensive.utils.NetworkStatusChecker;
import com.softdesign.devintensive.utils.UiHelper;

import org.greenrobot.greendao.query.DeleteQuery;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ChronosSaveUsersToDb extends ChronosOperation<String> {
    private DataManager mDataManager;
    private Context mContext;
    private DaoSession mDaoSession;
    private RepositoryDao mRepositoryDao;
    private LikesByDao mLikesByDao;
    private UserDao mUserDao;
    private String result="null";

    @Nullable
    @Override
    //Chronos will run this method in a background thread, which means you can put
    //any time-consuming calls here, as it will not affect UI thread performance
    public String run() {
        UiHelper.writeLog("ChronosSaveUsersToDb called");
        mContext=DevintensiveApplication.getContext();
        mDataManager = DataManager.getInstance();
        mDaoSession = mDataManager.getDaoSession();
        mUserDao = mDaoSession.getUserDao();
        mRepositoryDao =mDaoSession.getRepositoryDao();
        mLikesByDao = mDaoSession.getLikesByDao();

        if (NetworkStatusChecker.isNetworkAvailable(mContext)) {
            Call<UserListRes> call = mDataManager.getUserListFromNetork();
            try {
                Response<UserListRes> response = call.execute();
                if (response.code()==200) {
                    List<Repository> allRepositories = new ArrayList<Repository>();
                    List<User> allUsers = new ArrayList<User>();
                    List<String> newUsers = new ArrayList<>();
                    List<LikesBy> allLikes = new ArrayList<LikesBy>();
                    long index = 0;

                    for (UserListRes.Datum  userRes : response.body().getData()) {
                        index += 1;
                        long number = index;
                        //// TODO: проверить необходимость в этом
                        User user = mDaoSession.queryBuilder(User.class)
                                .where(UserDao.Properties.RemoteId.eq(userRes.getId())).build().unique();
                        if (user != null) number = user.getIndex();

                        //добавляем в список новых юзеров для фильтрации
                        newUsers.add(userRes.getId());
                        //сохраняем список лайков каждого пользователя в общий список
                        allLikes.addAll(getLikesListFromUserRes(userRes));
                        //сохраняем список репозиториев каждого пользователя в общий список
                        allRepositories.addAll(getRepoListFromUserRes(userRes));
                        //сохраняем каждого пользователя в общий список
                        allUsers.add(new User(userRes, number));
                    }

                    //получаем список старых/удаленных с сервера пользователей из локальной базы
                    List oldUsers =  mDaoSession.queryBuilder(User.class)
                            .where(UserDao.Properties.RemoteId.notIn(newUsers))
                            .list();
                    //очищаем этих пользователей, так как каскадного удаления нет, то делаем отдельными запросами
                    if (oldUsers!=null && oldUsers.size()>0) {
                        //удаляем старых пользователей
                        UiHelper.writeLog("ChronosSaveUsersToDb: удалили "+oldUsers.size()+" старых пользователей из локальной базы");
                        mUserDao.deleteInTx(oldUsers);

                        //удаляем репозитории удаленных/старых пользователей
                        List oldRepo =  mDaoSession.queryBuilder(Repository.class)
                                .where(RepositoryDao.Properties.UserRemoteId.notIn(newUsers))
                                .list();
                        if (oldRepo!=null && oldRepo.size()>0) {
                            UiHelper.writeLog("ChronosSaveUsersToDb: удалили "+oldRepo.size()+" старых репозиториев из локальной базы");
                            mRepositoryDao.deleteInTx(oldRepo);
                        }

                        //удаляем лайки удаленных/старых пользователей
                        List oldLikes =  mDaoSession.queryBuilder(LikesBy.class)
                                .where(LikesByDao.Properties.UserRemoteId.notIn(newUsers))
                                .list();
                        if (oldLikes!=null && oldLikes.size()>0) {
                            UiHelper.writeLog("ChronosSaveUsersToDb: удалили "+oldLikes.size()+" старых лайков из локальной базы");
                            mLikesByDao.deleteInTx(oldLikes);
                        }
                    } else {
                        UiHelper.writeLog("ChronosSaveUsersToDb: удаленных пользователей в локальной базе не обнаружено");
                    }

                    //так как лайков может быть много, очищаем старые лайки
                    mDaoSession.queryBuilder(LikesBy.class).where(LikesByDao.Properties.UserRemoteId.in(newUsers)).buildDelete().executeDeleteWithoutDetachingEntities();
                    mLikesByDao.insertOrReplaceInTx(allLikes);

                    //так как репозиториев может быть много, очищаем старые лайки
                    mDaoSession.queryBuilder(Repository.class).where(RepositoryDao.Properties.UserRemoteId.in(newUsers)).buildDelete().executeDeleteWithoutDetachingEntities();
                    mRepositoryDao.insertOrReplaceInTx(allRepositories);

                    mUserDao.insertOrReplaceInTx(allUsers);
                    result=mContext.getString(R.string.success_user_list_load);
                } else if (response.code()==404) {
                    result=mContext.getString(R.string.error_login_or_password);
                } else if (response.code()==401) {
                    result=mContext.getString(R.string.error_token_message);
                } else {
                    result=mContext.getString(R.string.error_unknown);
                }
            } catch (Exception e) {
                result=e.toString();
            }
        } else {
            result=mContext.getString(R.string.error_network_is_not_available);
        }
        return result;
    }

    private List<Repository> getRepoListFromUserRes(UserListRes.Datum userData){
        final String userId = userData.getId();
        List<Repository> repositories = new ArrayList<>();
        for (UserListRes.Repo repositoryRes : userData.getRepositories().getRepo()) {
            repositories.add(new Repository(repositoryRes, userId));
        }
        return repositories;
    }

    private List<LikesBy> getLikesListFromUserRes(UserListRes.Datum userData){
        final String userId = userData.getId();
        List<LikesBy> userLiked = new ArrayList<LikesBy>();
        for (int i=0; i<userData.getProfileValues().getLikesBy().size();i++) {
            LikesBy userLike = new LikesBy(userData.getProfileValues().getLikesBy().get(i), userId);
            userLiked.add(userLike);
        }
        return userLiked;
    }

    @NonNull
    @Override
    // To be able to distinguish results from different operations in one Chronos client
    // (most commonly an activity, or a fragment)
    // you should create an 'OperationResult<>' subclass in each operation,
    // so that it will be used as a parameter
    // in a callback method 'onOperationFinished'
    public Class<? extends ChronosOperationResult<String>> getResultClass() {
        return Result.class;
    }

    // the class is a named version of ChronosOperationResult<> generic class
    // it is required because Java disallows method overriding by using generic class with another parameter
    // and result delivery is based on calling particular methods with the exact same result class
    // later we'll see how Chronos use this class
    public final static class Result extends ChronosOperationResult<String> {
        // usually this class is empty, but you may add some methods to customize its behavior
        // however, it must have a public constructor with no arguments
    }
}