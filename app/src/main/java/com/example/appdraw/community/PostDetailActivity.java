package com.example.appdraw.community;

import com.example.appdraw.R;
import com.example.appdraw.model.Post;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import android.content.Intent;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Cao Đức Mạnh
 * @version 1.0
 */
public class PostDetailActivity extends AppCompatActivity {
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Toolbar toolbar = findViewById(R.id.toolbar_post_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        postId = getIntent().getStringExtra("POST_ID");
        if (postId != null) {
            loadPostDetails();
        }
    }

    private void loadPostDetails() {
        FirebaseFirestore.getInstance().collection("Posts").document(postId)
            .get().addOnSuccessListener(doc -> {
                if (doc.exists()) {
                    Post post = doc.toObject(Post.class);
                    if (post != null) {
                        View includedPost = findViewById(R.id.included_post);
                        if (includedPost != null) {
                            TextView tvContent = includedPost.findViewById(R.id.tv_post_content);
                            if (tvContent != null) tvContent.setText(post.getContent());

                            ImageView ivPostImg = includedPost.findViewById(R.id.iv_post_image);
                            if (ivPostImg != null && post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                                ivPostImg.setVisibility(View.VISIBLE);
                                if (post.getImageUrl().startsWith("data:image")) {
                                    byte[] decodedBytes = android.util.Base64.decode(post.getImageUrl().split(",")[1], android.util.Base64.DEFAULT);
                                    Glide.with(this).load(decodedBytes).into(ivPostImg);
                                } else {
                                    Glide.with(this).load(post.getImageUrl()).into(ivPostImg);
                                }
                                ivPostImg.setOnClickListener(v -> {
                                    Intent intent = new Intent(this, FullScreenImageActivity.class);
                                    intent.putExtra("IMAGE_URL", post.getImageUrl());
                                    startActivity(intent);
                                });
                            } else if (ivPostImg != null) {
                                ivPostImg.setVisibility(View.GONE);
                            }

                            TextView tvLikeCount = includedPost.findViewById(R.id.tv_like_count);
                            if (tvLikeCount != null) tvLikeCount.setText(String.valueOf(post.getLikesCount()));

                            TextView tvCommentCount = includedPost.findViewById(R.id.tv_comment_count);
                            if (tvCommentCount != null) tvCommentCount.setText(String.valueOf(post.getCommentsCount()));
                            
                            TextView tvTime = includedPost.findViewById(R.id.tv_post_time);
                            if (tvTime != null) {
                                long time = post.getCreatedAt();
                                long now = System.currentTimeMillis();
                                if (time < 1000000000000L) time *= 1000;
                                long diff = now - time;
                                if (diff < 60 * 1000) tvTime.setText("Vừa xong");
                                else if (diff < 60 * 60 * 1000) tvTime.setText((diff / (60 * 1000)) + " phút trước");
                                else if (diff < 24 * 60 * 60 * 1000) tvTime.setText((diff / (60 * 60 * 1000)) + " giờ trước");
                                else {
                                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
                                    tvTime.setText(sdf.format(new java.util.Date(time)));
                                }
                            }

                            // Tải thông tin tác giả bài viết
                            FirebaseFirestore.getInstance().collection("Users").document(post.getUid())
                                .get().addOnSuccessListener(userDoc -> {
                                    if (userDoc.exists() && userDoc.contains("profile")) {
                                        java.util.Map<String, Object> profile = (java.util.Map<String, Object>) userDoc.get("profile");
                                        if (profile != null) {
                                            String fullName = (String) profile.get("fullName");
                                            String avatarUrl = (String) profile.get("avatarUrl");
                                            TextView tvName = includedPost.findViewById(R.id.tv_user_name);
                                            ImageView ivAvatar = includedPost.findViewById(R.id.iv_user_avatar);
                                            ImageView ivMentorBadge = includedPost.findViewById(R.id.iv_mentor_badge);
                                            if (tvName != null) tvName.setText(fullName != null ? fullName : "Người dùng");
                                            if (ivMentorBadge != null) {
                                                ivMentorBadge.setVisibility("mentor".equals(userDoc.getString("role")) ? View.VISIBLE : View.GONE);
                                            }
                                            if (ivAvatar != null) {
                                                if (avatarUrl != null && !avatarUrl.isEmpty() && avatarUrl.startsWith("data:image")) {
                                                    byte[] b = android.util.Base64.decode(avatarUrl.split(",")[1], android.util.Base64.DEFAULT);
                                                    Glide.with(this).load(b).circleCrop().into(ivAvatar);
                                                } else if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                                    Glide.with(this).load(avatarUrl).circleCrop().into(ivAvatar);
                                                } else {
                                                    Glide.with(this).load(R.drawable.ic_default_user).circleCrop().into(ivAvatar);
                                                }
                                            }
                                        }
                                    }
                                });
                        }
                    }
                }
            });
    }
}
