package com.softdesign.devintensive.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.ui.views.AspectRatioImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    private Context mContext;
    private List<UserListRes.Datum> mUsers;
    private UserViewHolder.CustomClickListener mCustomClickListener;

    public UsersAdapter(List<UserListRes.Datum> users, UserViewHolder.CustomClickListener customClickListener) {
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
    public void onBindViewHolder(UsersAdapter.UserViewHolder holder, int position) {
        UserListRes.Datum user = mUsers.get(position);

        if (user.getPublicInfo().getPhoto()==null || user.getPublicInfo().getPhoto().isEmpty()) {

        } else {
            Picasso.with(mContext)
                    .load(user.getPublicInfo().getPhoto())////
                    .resize((int) mContext.getResources().getDimension(R.dimen.profile_image_size), (int) (mContext.getResources().getDimension(R.dimen.profile_image_size)/AspectRatioImageView.getDefaultAspectRatio()))
                    .centerCrop()
                    .placeholder(mContext.getResources().getDrawable(R.drawable.user_bg))
                    .error(mContext.getResources().getDrawable(R.drawable.user_bg))
                    .into(holder.userPhoto);
        }

        holder.mFullname.setText(user.getFullName());
        holder.mRating.setText(String.valueOf(user.getProfileValues().getRating()));
        holder.mCodeLines.setText(String.valueOf(user.getProfileValues().getLinesCode()));
        holder.mProjects.setText(String.valueOf(user.getProfileValues().getProjects()));
        if (user.getPublicInfo().getBio()==null || user.getPublicInfo().getBio().isEmpty()) {
            holder.mBio.setVisibility(View.GONE);
        } else {
            holder.mBio.setVisibility(View.VISIBLE);
            holder.mBio.setText(user.getPublicInfo().getBio());
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
