package com.softdesign.devintensive.ui.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.ui.views.AspectRatioImageView;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.DevintensiveApplication;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    private static final String TAG = ConstantManager.TAG_PREFIX+" UsersAdapter";
    private Context mContext;
    private List<User> mUsers;//01:02 #7
    private UserViewHolder.CustomClickListener mCustomClickListener;

    public UsersAdapter(List<User> users, UserViewHolder.CustomClickListener customClickListener) {
        mUsers = users;
        this.mCustomClickListener = customClickListener;
    }

    @Override
    public UsersAdapter.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list, parent, false);
        return new UserViewHolder(convertView, mCustomClickListener);
    }

    @Override
    public void onBindViewHolder(final UsersAdapter.UserViewHolder holder, int position) {
        final User user = mUsers.get(position);
        final String userPhoto;
        if (user.getPhoto().isEmpty()) {
            userPhoto="null";
            Log.e(TAG, " user with name "+ user.getFullName()+" has empty photo");
        } else {
            userPhoto = user.getPhoto();
        }
        DataManager.getInstance().getPicasso()
                .load(userPhoto)
                .error(holder.mDummy)
                .placeholder(holder.mDummy)
                .resize((int) DevintensiveApplication.getContext().getResources().getDimension(R.dimen.profile_image_size), (int) (DevintensiveApplication.getContext().getResources().getDimension(R.dimen.profile_image_size)/AspectRatioImageView.getDefaultAspectRatio()))
                .centerCrop()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.userPhoto, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, " loaded user photo from cache");
                        }
                        @Override
                        public void onError() {
                            Log.d(TAG, " can't load user photo from cache");
                            DataManager.getInstance().getPicasso()
                                    .load(userPhoto)
                                    .error(holder.mDummy)
                                    .placeholder(holder.mDummy)
                                    .resize((int) DevintensiveApplication.getContext().getResources().getDimension(R.dimen.profile_image_size), (int) (DevintensiveApplication.getContext().getResources().getDimension(R.dimen.profile_image_size)/AspectRatioImageView.getDefaultAspectRatio()))
                                    .centerCrop()
                                    .into(holder.userPhoto, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            Log.d(TAG, " loaded user photo from network");
                                        }

                                        @Override
                                        public void onError() {
                                            Log.d(TAG, " can't load user photo from network: "+userPhoto);
                                        }
                                    });
                        }
                    });

        holder.mFullname.setText(user.getFullName());
        holder.mRating.setText(String.valueOf(user.getRating()));
        holder.mCodeLines.setText(String.valueOf(user.getCodeLines()));
        holder.mProjects.setText(String.valueOf(user.getProjects()));
        if (user.getBio()==null || user.getBio().isEmpty()) {
            holder.mBio.setVisibility(View.GONE);
        } else {
            holder.mBio.setVisibility(View.VISIBLE);
            holder.mBio.setText(user.getBio());
        }
    }


    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected AspectRatioImageView userPhoto;
        protected TextView mFullname, mRating, mCodeLines, mProjects, mBio;
        protected Button mShowMore;
        protected Drawable mDummy;

        private CustomClickListener mListener;

        public UserViewHolder(View itemView, CustomClickListener customClickListener) {
            super(itemView);
            this.mListener = customClickListener;

            userPhoto = (AspectRatioImageView) itemView.findViewById(R.id.user_photo);
            mFullname = (TextView) itemView.findViewById(R.id.user_full_name_txt);
            mRating = (TextView) itemView.findViewById(R.id.rating_txt);
            mCodeLines = (TextView) itemView.findViewById(R.id.code_lines_txt);
            mProjects = (TextView) itemView.findViewById(R.id.projects_txt);
            mBio = (TextView) itemView.findViewById(R.id.bio_txt);
            mShowMore = (Button) itemView.findViewById(R.id.more_info_btn);

            mDummy = userPhoto.getContext().getResources().getDrawable(R.drawable.user_bg);
            mShowMore.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mListener!=null) {
                mListener.onUserItemClickListener(getAdapterPosition());
            }

        }

        public interface CustomClickListener {

            void onUserItemClickListener(int position);

        }
    }
}
