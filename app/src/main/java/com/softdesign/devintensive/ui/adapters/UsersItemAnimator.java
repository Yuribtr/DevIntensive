package com.softdesign.devintensive.ui.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.utils.UiHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersItemAnimator extends DefaultItemAnimator {
    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);
    private int mLastAddAnimatedItem = -2;
    private Map<RecyclerView.ViewHolder, AnimatorSet> likeAnimationsMap = new HashMap<>();
    private Map<RecyclerView.ViewHolder, AnimatorSet> heartAnimationsMap = new HashMap<>();

    @Override
    public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull List<Object> payloads) {
        return true;
    }

    @NonNull
    @Override
    public ItemHolderInfo recordPreLayoutInformation(@NonNull RecyclerView.State state, @NonNull RecyclerView.ViewHolder viewHolder, int changeFlags, @NonNull List<Object> payloads) {

        if (changeFlags == FLAG_CHANGED) {
            for (Object payload : payloads) {
                if (payload instanceof String) {
                    return new UserItemHolderInfo((String) payload);
                }
            }
        }

        return super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads);
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder.getItemViewType() == UsersAdapter.VIEW_TYPE_DEFAULT) {
            if (viewHolder.getLayoutPosition() > mLastAddAnimatedItem) {
                mLastAddAnimatedItem++;
                runEnterAnimation((UsersAdapter.UserViewHolder) viewHolder);
                return false;
            }
        }
        dispatchAddFinished(viewHolder);
        return false;
    }

    private void runEnterAnimation(final UsersAdapter.UserViewHolder holder) {
        final int screenHeight = UiHelper.getScreenHeight(holder.itemView.getContext());
        holder.itemView.setTranslationY(screenHeight);
        holder.itemView.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(700)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        dispatchAddFinished(holder);
                    }
                })
                .start();
    }


    @Override
    public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder, @NonNull RecyclerView.ViewHolder newHolder, @NonNull ItemHolderInfo preInfo, @NonNull ItemHolderInfo postInfo) {
        cancelCurrentAnimationIfExists(newHolder);

        if (preInfo instanceof UserItemHolderInfo) {
            UserItemHolderInfo userItemHolderInfo = (UserItemHolderInfo) preInfo;
            UsersAdapter.UserViewHolder holder = (UsersAdapter.UserViewHolder) newHolder;

            //animateHeartButton(holder);
            //updateLikesCounter(holder, holder.getFeedItem().likesCount);
//            if (UsersAdapter.ACTION_LIKE_IMAGE_CLICKED.equals(userItemHolderInfo.updateAction)) {
//                animatePhotoLike(holder);
//            }
        }

        return false;
    }

    private void cancelCurrentAnimationIfExists(RecyclerView.ViewHolder item) {
        if (likeAnimationsMap.containsKey(item)) {
            likeAnimationsMap.get(item).cancel();
        }
        if (heartAnimationsMap.containsKey(item)) {
            heartAnimationsMap.get(item).cancel();
        }
    }

    private void animateHeartButton(final UsersAdapter.UserViewHolder holder) {
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(holder.getLikesImage(), "rotation", 0f, 360f);
        rotationAnim.setDuration(300);
        rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.getLikesImage(), "scaleX", 0.2f, 1f);
        bounceAnimX.setDuration(300);
        bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

        ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.getLikesImage(), "scaleY", 0.2f, 1f);
        bounceAnimY.setDuration(300);
        bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
        bounceAnimY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                holder.getLikesImage().setImageResource(R.drawable.ic_favorite_black_24dp);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                heartAnimationsMap.remove(holder);
                dispatchChangeFinishedIfAllAnimationsEnded(holder);
            }
        });

        animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);
        animatorSet.start();

        heartAnimationsMap.put(holder, animatorSet);
    }


    private void dispatchChangeFinishedIfAllAnimationsEnded(UsersAdapter.UserViewHolder holder) {
        if (likeAnimationsMap.containsKey(holder) || heartAnimationsMap.containsKey(holder)) {
            return;
        }
        dispatchAnimationFinished(holder);
    }

    private void updateLikesCounter(UsersAdapter.UserViewHolder holder, int toValue) {

        String likesCountTextFrom = holder.getLikesCount().getResources().getQuantityString(
                R.plurals.likes_count, toValue - 1, toValue - 1
        );

        holder.getLikesCount().setText(likesCountTextFrom);

        String likesCountTextTo = holder.getLikesCount().getResources().getQuantityString(
                R.plurals.likes_count, toValue, toValue
        );
        holder.getLikesCount().setText(likesCountTextTo);
    }


    public static class UserItemHolderInfo extends ItemHolderInfo {
        public String updateAction;

        public UserItemHolderInfo(String updateAction) {
            this.updateAction = updateAction;
        }
    }
}

