package com.softdesign.devintensive.data.storage.models;

import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "LIKESBY".
*/
public class LikesByDao extends AbstractDao<LikesBy, Long> {

    public static final String TABLENAME = "LIKESBY";

    /**
     * Properties of entity LikesBy.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserLiked = new Property(1, String.class, "userLiked", false, "USER_LIKED");
        public final static Property UserRemoteId = new Property(2, String.class, "userRemoteId", false, "USER_REMOTE_ID");
    };

    private DaoSession daoSession;

    private Query<LikesBy> user_UserLikedQuery;

    public LikesByDao(DaoConfig config) {
        super(config);
    }
    
    public LikesByDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"LIKESBY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"USER_LIKED\" TEXT," + // 1: userLiked
                "\"USER_REMOTE_ID\" TEXT);"); // 2: userRemoteId
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"LIKESBY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, LikesBy entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String userLiked = entity.getUserLiked();
        if (userLiked != null) {
            stmt.bindString(2, userLiked);
        }
 
        String userRemoteId = entity.getUserRemoteId();
        if (userRemoteId != null) {
            stmt.bindString(3, userRemoteId);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, LikesBy entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String userLiked = entity.getUserLiked();
        if (userLiked != null) {
            stmt.bindString(2, userLiked);
        }
 
        String userRemoteId = entity.getUserRemoteId();
        if (userRemoteId != null) {
            stmt.bindString(3, userRemoteId);
        }
    }

    @Override
    protected final void attachEntity(LikesBy entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public LikesBy readEntity(Cursor cursor, int offset) {
        LikesBy entity = new LikesBy( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // userLiked
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2) // userRemoteId
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, LikesBy entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserLiked(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setUserRemoteId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(LikesBy entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(LikesBy entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "userLiked" to-many relationship of User. */
    public List<LikesBy> _queryUser_UserLiked(String userRemoteId) {
        synchronized (this) {
            if (user_UserLikedQuery == null) {
                QueryBuilder<LikesBy> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.UserRemoteId.eq(null));
                user_UserLikedQuery = queryBuilder.build();
            }
        }
        Query<LikesBy> query = user_UserLikedQuery.forCurrentThread();
        query.setParameter(0, userRemoteId);
        return query.list();
    }

}