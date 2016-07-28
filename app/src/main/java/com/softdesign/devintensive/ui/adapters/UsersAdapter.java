package com.softdesign.devintensive.ui.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.models.DaoSession;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.utils.CustomClickListener;
import com.softdesign.devintensive.ui.views.AspectRatioImageView;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.DevintensiveApplication;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.softdesign.devintensive.utils.UiHelper;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder>
        implements Filterable, ItemTouchHelperAdapter {
    private static final String TAG = ConstantManager.TAG_PREFIX + "UsersAdapter";
    Context mContext;
    List<User> mUsers;
    List<User> mFilteredUsers;
    List<User> mRemovalUsers;
    CustomClickListener mCustomClickListener;
    UserFilter mFilter;
    DaoSession mDaoSession;
    UsersAdapter mUsersAdapter = this;

    public UsersAdapter(List<User> users, CustomClickListener customClickListener) {
        mUsers = users;
        mFilteredUsers = new ArrayList<>();
        mFilteredUsers.addAll(users);
        mRemovalUsers = new ArrayList<>();
        mCustomClickListener = customClickListener;
        mFilter = new UserFilter(UsersAdapter.this);
        mDaoSession = DevintensiveApplication.getDaoSession();
        mContext = DevintensiveApplication.getContext();
        UiHelper.writeLog("UsersAdapter constructor");
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        UiHelper.writeLog("onCreateViewHolder");
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.item_user_list, parent, false);
        return new UserViewHolder(convertView, mCustomClickListener);
    }

    @Override
    public void onBindViewHolder(final UserViewHolder holder, int position) {
        UiHelper.writeLog("onBindViewHolder");
        final User user = mFilteredUsers.get(position);
        final String userPhoto;
        //если этот юзер есть в списке на удаление, то устанавливаем ему разметку Удален
        if (mRemovalUsers.contains(user)) {
            holder.mNotDeletedLayout.setVisibility(View.GONE);
            holder.mDeletedLayout.setVisibility(View.VISIBLE);
            holder.mDeleted.setText(String.format(mContext.getString(R.string.user_deleted_from_list),
                    mFilteredUsers.get(position).getFullName()));
            //устанавливаем обработчик события для отмены удаления
            holder.mRevert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRemovalUsers.remove(user);
                    notifyItemChanged(holder.getAdapterPosition());
                }
            });
        } else {
            //обычнм образом инициализируем карточку юзера
            holder.mNotDeletedLayout.setVisibility(View.VISIBLE);
            holder.mDeletedLayout.setVisibility(View.GONE);
            if (user.getPhoto().isEmpty()) {
                userPhoto = "null";
                UiHelper.writeLog(mContext.getString(R.string.no_photo_assigned)+user.getFullName());
            } else {
                userPhoto = user.getPhoto();
            }
            DataManager.getInstance().getPicasso()
                    .load(userPhoto)
                    .resize((int) DevintensiveApplication.getContext().getResources().getDimension(R.dimen.profile_image_size), (int) (DevintensiveApplication.getContext().getResources().getDimension(R.dimen.profile_image_size)/AspectRatioImageView.getDefaultAspectRatio()))
                    .centerCrop()
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .error(holder.mDummy)
                    .placeholder(holder.mDummy)
                    .into(holder.mUserPhoto, new Callback() {
                        @Override
                        public void onSuccess() {
                            UiHelper.writeLog(mContext.getString(R.string.success_photo_loaded_from_cache)+user.getFullName());
                        }
                        @Override
                        public void onError() {
                            if (userPhoto != "null") {
                                UiHelper.writeLog(mContext.getString(R.string.error_load_photo_from_cache) + user.getFullName());
                                DataManager.getInstance().getPicasso()
                                        .load(userPhoto)
                                        .resize((int) DevintensiveApplication.getContext().getResources().getDimension(R.dimen.profile_image_size), (int) (DevintensiveApplication.getContext().getResources().getDimension(R.dimen.profile_image_size) / AspectRatioImageView.getDefaultAspectRatio()))
                                        .centerCrop()
                                        .error(holder.mDummy)
                                        .placeholder(holder.mDummy)
                                        .into(holder.mUserPhoto, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                UiHelper.writeLog(mContext.getString(R.string.success_photo_loaded_from_network) + user.getFullName());
                                            }

                                            @Override
                                            public void onError() {
                                                UiHelper.writeLog(mContext.getString(R.string.error_load_photo_from_network) + userPhoto);
                                            }
                                        });
                            }
                        }
                    });
            holder.mFullName.setText(user.getFullName());
            holder.mRating.setText(String.valueOf(user.getRating()));
            holder.mCodeLines.setText(String.valueOf(user.getCodeLines()));
            holder.mProjects.setText(String.valueOf(user.getProjects()));
            holder.mLikesCount.setText(String.valueOf(user.getUserLiked().size()));

            if (user.getIsMyLike())
                holder.mLikesImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
            else
                holder.mLikesImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));


            if (user.getBio() == null || user.getBio().isEmpty()) {
                holder.mBio.setVisibility(View.GONE);
            } else {
                holder.mBio.setVisibility(View.VISIBLE);
                holder.mBio.setText(user.getBio());
            }
        }
    }


    @Override
    public int getItemCount() {
        return mFilteredUsers.size();
    }

    @Override
    public void onItemDismiss(int position) {
        itemDelete(position);
    }

    private boolean itemDelete (int position){
        boolean result = false;
        if (mUsers.size() == mFilteredUsers.size()) {
            User user = mUsers.get(position);
            if (user!=null) {
                if (!mRemovalUsers.contains(user)) {
                    //если данного юзера в списке на удаление нет то добавляем в список на удаление (первый свайп)
                    mRemovalUsers.add(user);
                    //уведомляем RecyclerView об изменении списка
                    notifyItemChanged(position);
                    result=true;
                } else {
                    //если уже есть (второй свайп), то удаляем его из списков и базы
                    mUsers.remove(user);
                    //удаляем из списка на удаление
                    mRemovalUsers.remove(user);
                    //удаляем из списка фильтра
                    mFilteredUsers.remove(user);
                    UiHelper.writeLog("удаляем пользователя " + user.getFullName());
                    //удаляем пользователя из базы
                    mDaoSession.delete(user);
                    //уведомляем RecyclerView об изменении списка
                    notifyItemRemoved(position);
                    result=true;
                }
            }
        }
        return result;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (mUsers.size() != mFilteredUsers.size()) return;
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; ++i) {
                Collections.swap(mUsers, i, i + 1);
                Collections.swap(mFilteredUsers, i, i + 1);
                changeUsersInOrder(mUsers.get(i), mUsers.get(i + 1));
            }
        } else {
            for (int i = fromPosition; i > toPosition; --i) {
                Collections.swap(mUsers, i, i - 1);
                Collections.swap(mFilteredUsers, i, i - 1);
                changeUsersInOrder(mUsers.get(i), mUsers.get(i - 1));
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    private void changeUsersInOrder(User user1, User user2) {
        long tempIndex = user1.getIndex();
        user1.setIndex(user2.getIndex());
        user2.setIndex(tempIndex);
        user1.update();
        user2.update();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public List<User> getRemovalUsers() {
        return mRemovalUsers;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AspectRatioImageView mUserPhoto;
        private CardView mCardView;
        private LinearLayout mDeletedLayout;
        private LinearLayout mNotDeletedLayout;
        private TextView mFullName, mRating, mCodeLines, mProjects, mBio, mDeleted, mLikesCount;
        private Button mShowMore, mRevert, mConfirm;
        private Drawable mDummy;
        private ImageView mLikesImage;
        private boolean isCardLongPressed = false;

        private CustomClickListener mListener;

        public UserViewHolder(View itemView, CustomClickListener customClickListener) {

            super(itemView);
            UiHelper.writeLog("UserViewHolder constructor");
            this.mListener = customClickListener;
            mCardView = (CardView) itemView.findViewById(R.id.cardView);
            mCardView.setMaxCardElevation(100);
            mCardView.setCardElevation(0);
            mUserPhoto = (AspectRatioImageView) itemView.findViewById(R.id.user_photo);
            mFullName = (TextView) itemView.findViewById(R.id.user_full_name_txt);
            mRating = (TextView) itemView.findViewById(R.id.rating_txt);
            mCodeLines = (TextView) itemView.findViewById(R.id.code_lines_txt);
            mProjects = (TextView) itemView.findViewById(R.id.projects_txt);
            mBio = (TextView) itemView.findViewById(R.id.bio_txt);
            mShowMore = (Button) itemView.findViewById(R.id.more_info_btn);
            mDummy = mUserPhoto.getContext().getResources().getDrawable(R.drawable.user_bg);
            mShowMore.setOnClickListener(this);
            mLikesImage = (ImageView) itemView.findViewById(R.id.likes_img);
            mLikesCount = (TextView) itemView.findViewById(R.id.likes_count);

            mDeletedLayout = (LinearLayout) itemView.findViewById(R.id.user_deleted_layout);
            mNotDeletedLayout = (LinearLayout) itemView.findViewById(R.id.user_not_deleted_layout);
            mDeleted = (TextView) itemView.findViewById(R.id.user_deleted_tv);
            mRevert = (Button) itemView.findViewById(R.id.user_delete_revert_btn);

//            mCardView.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View view, MotionEvent motionEvent) {
//                    view.onTouchEvent(motionEvent);
//
//                    switch (motionEvent.getAction()) {
//                        case MotionEvent.ACTION_DOWN: // нажатие
//                            UiHelper.writeLog("нажатие");
//                            return true;//для предотвращения повторного вызова onLongClick
//                        case MotionEvent.ACTION_MOVE: // движение
//                            UiHelper.writeLog("движение");
//                            break;
//                        case MotionEvent.ACTION_UP: // отпускание
//                            UiHelper.writeLog("отпускание");
//                            break;
//                        case MotionEvent.ACTION_CANCEL:
//                            UiHelper.writeLog("отмена");
//                            return false;
//                    }
//                    return false;
//                }
//            });
//
//            mCardView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    UiHelper.writeLog("onClick "+view.getId());
//                }
//            });

            //mCardView.onInterceptTouchEvent();
//


//            mCardView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    //mCardView.setCardElevation(50);
//                    //mCardView.setHovered(true);
////                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
////                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
////                    mCardView.setLayoutParams(layoutParams);
//                    UiHelper.writeLog("onLongClick "+view.getId());
//                    isCardLongPressed = true;
//                    return true;//блокируем вызов поторных OnLongClick
//                }
//            });
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onUserItemClickListener(mUsers.indexOf(mFilteredUsers.get(getAdapterPosition())));
            }
        }

    }

    public class UserFilter extends Filter {
        private UsersAdapter mAdapter;

        public UserFilter(UsersAdapter adapter) {
            super();
            mAdapter = adapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            mFilteredUsers.clear();
            FilterResults results = new FilterResults();
            if (constraint.length() == 0) {
                mFilteredUsers.addAll(mUsers);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (User user : mUsers) {
                    if (user.getFullName().toLowerCase().contains(filterPattern)) {
                        mFilteredUsers.add(user);
                    }
                }
            }
            results.values = mFilteredUsers;
            results.count = mFilteredUsers.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mAdapter.notifyDataSetChanged();
        }

    }
}