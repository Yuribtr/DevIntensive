package com.softdesign.devintensive.data.storage.models;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

@Entity(active = true, nameInDb = "LIKESBY")
public class LikesBy {

    @Id
    private Long id;

    private String userLiked;

    private String userRemoteId;

    /** Used for active entity operations. */
    @Generated(hash = 1721354425)
    private transient LikesByDao myDao;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;



    public LikesBy(String userLiked, String userRemoteId) {
        this.userLiked = userLiked;
        this.userRemoteId = userRemoteId;
    }



    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }



    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }



    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }



    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 122203027)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getLikesByDao() : null;
    }



    public String getUserRemoteId() {
        return this.userRemoteId;
    }



    public void setUserRemoteId(String userRemoteId) {
        this.userRemoteId = userRemoteId;
    }



    public String getUserLiked() {
        return this.userLiked;
    }



    public void setUserLiked(String userLiked) {
        this.userLiked = userLiked;
    }



    public Long getId() {
        return this.id;
    }



    public void setId(Long id) {
        this.id = id;
    }



    @Generated(hash = 823762358)
    public LikesBy(Long id, String userLiked, String userRemoteId) {
        this.id = id;
        this.userLiked = userLiked;
        this.userRemoteId = userRemoteId;
    }



    @Generated(hash = 991161490)
    public LikesBy() {
    }



}

