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

    private com.google.firebase.firestore.ListenerRegistration commentsListener;
    private String currentCommentFilter = "Cũ nhất";
    private String replyingToCommentId = null;
    private String replyingToUserName = null;

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
            loadComments();
            setupCommentInput();
            setupCommentFilter();
        }
    }

    private void setupCommentFilter() {
        TextView tvFilter = findViewById(R.id.tv_comment_filter);
        if (tvFilter != null) {
            tvFilter.setOnClickListener(v -> {
                android.widget.PopupMenu popup = new android.widget.PopupMenu(this, tvFilter);
                popup.getMenu().add("Mới nhất");
                popup.getMenu().add("Cũ nhất");
                popup.setOnMenuItemClickListener(item -> {
                    String title = item.getTitle().toString();
                    tvFilter.setText(title + " ▼");
                    currentCommentFilter = title;
                    loadComments();
                    return true;
                });
                popup.show();
            });
        }
    }

    private void setupCommentInput() {
        ImageView btnSend = findViewById(R.id.btn_send_comment);
        android.widget.EditText etComment = findViewById(R.id.et_comment);
        
        btnSend.setOnClickListener(v -> {
            String text = etComment.getText().toString().trim();
            if (text.isEmpty()) return;
            
            String uid = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();
            if (uid == null) {
                android.widget.Toast.makeText(this, "Vui lòng đăng nhập!", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            
            btnSend.setEnabled(false);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String commentId = db.collection("Posts").document(postId).collection("Comments").document().getId();
            com.example.appdraw.model.Comment comment = new com.example.appdraw.model.Comment(commentId, uid, text, System.currentTimeMillis());
            comment.setParentId(replyingToCommentId);
            
            db.collection("Posts").document(postId).collection("Comments").document(commentId)
                .set(comment)
                .addOnSuccessListener(aVoid -> {
                    etComment.setText("");
                    btnSend.setEnabled(true);
                    cancelReply();
                    db.collection("Posts").document(postId).update("commentsCount", com.google.firebase.firestore.FieldValue.increment(1));
                    android.widget.Toast.makeText(this, "Đã gửi bình luận", android.widget.Toast.LENGTH_SHORT).show();
                    
                    // Gửi thông báo cho tác giả
                    db.collection("Posts").document(postId).get().addOnSuccessListener(doc -> {
                        if (doc.exists() && doc.getString("uid") != null && !doc.getString("uid").equals(uid)) {
                            com.example.appdraw.utils.NotificationHelper.sendNotification(doc.getString("uid"), "COMMENT", "đã bình luận về bài viết của bạn: " + text, postId);
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    btnSend.setEnabled(true);
                    android.widget.Toast.makeText(this, "Lỗi gửi bình luận", android.widget.Toast.LENGTH_SHORT).show();
                });
        });

        ImageView btnCancelReply = findViewById(R.id.btn_cancel_reply);
        if (btnCancelReply != null) {
            btnCancelReply.setOnClickListener(v -> cancelReply());
        }
    }

    private void cancelReply() {
        replyingToCommentId = null;
        replyingToUserName = null;
        View llReplyStatus = findViewById(R.id.ll_reply_status);
        if (llReplyStatus != null) llReplyStatus.setVisibility(View.GONE);
        android.widget.EditText etComment = findViewById(R.id.et_comment);
        if (etComment != null) {
            etComment.setHint("Viết bình luận...");
            etComment.setText("");
        }
    }

    private void loadComments() {
        android.widget.LinearLayout llCommentsContainer = findViewById(R.id.ll_comments_container);
        if (llCommentsContainer == null) return;
        
        if (commentsListener != null) {
            commentsListener.remove();
        }

        com.google.firebase.firestore.Query.Direction direction = currentCommentFilter.equals("Mới nhất") ? com.google.firebase.firestore.Query.Direction.DESCENDING : com.google.firebase.firestore.Query.Direction.ASCENDING;

        commentsListener = FirebaseFirestore.getInstance().collection("Posts").document(postId).collection("Comments")
            .orderBy("createdAt", direction)
            .addSnapshotListener((value, error) -> {
                if (error != null || value == null) return;
                llCommentsContainer.removeAllViews();
                
                java.util.List<com.example.appdraw.model.Comment> topLevelComments = new java.util.ArrayList<>();
                java.util.List<com.example.appdraw.model.Comment> replies = new java.util.ArrayList<>();
                
                for (com.google.firebase.firestore.QueryDocumentSnapshot doc : value) {
                    com.example.appdraw.model.Comment comment = doc.toObject(com.example.appdraw.model.Comment.class);
                    if (comment.getParentId() == null || comment.getParentId().isEmpty()) {
                        topLevelComments.add(comment);
                    } else {
                        replies.add(comment);
                    }
                }
                
                if (currentCommentFilter.equals("Mới nhất")) {
                    java.util.Collections.sort(replies, (c1, c2) -> Long.compare(c1.getCreatedAt(), c2.getCreatedAt()));
                }
                
                for (com.example.appdraw.model.Comment topComment : topLevelComments) {
                    renderComment(topComment, llCommentsContainer, false);
                    for (com.example.appdraw.model.Comment reply : replies) {
                        if (topComment.getId().equals(reply.getParentId())) {
                            renderComment(reply, llCommentsContainer, true);
                        }
                    }
                }
            });
    }

    private void renderComment(com.example.appdraw.model.Comment comment, android.widget.LinearLayout container, boolean isReply) {
        View commentView = getLayoutInflater().inflate(R.layout.item_comment, container, false);
        
        if (isReply) {
            android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            );
            int marginPx = (int) (40 * getResources().getDisplayMetrics().density);
            params.setMargins(marginPx, 0, 0, 0);
            commentView.setLayoutParams(params);
        }
        
        TextView tvCommentContent = commentView.findViewById(R.id.tv_comment_content);
        if (tvCommentContent != null) tvCommentContent.setText(comment.getContent());
        
        TextView tvCommentTime = commentView.findViewById(R.id.tv_comment_time);
        if (tvCommentTime != null) {
             long time = comment.getCreatedAt();
             long now = System.currentTimeMillis();
             if (time < 1000000000000L) time *= 1000;
             long diff = now - time;
             if (diff < 60 * 1000) tvCommentTime.setText("Vừa xong");
             else if (diff < 60 * 60 * 1000) tvCommentTime.setText((diff / (60 * 1000)) + " phút trước");
             else if (diff < 24 * 60 * 60 * 1000) tvCommentTime.setText((diff / (60 * 60 * 1000)) + " giờ trước");
             else {
                 java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
                 tvCommentTime.setText(sdf.format(new java.util.Date(time)));
             }
        }
        
        FirebaseFirestore.getInstance().collection("Users").document(comment.getUid())
            .get().addOnSuccessListener(userDoc -> {
                if (userDoc.exists() && userDoc.contains("profile")) {
                    java.util.Map<String, Object> profile = (java.util.Map<String, Object>) userDoc.get("profile");
                    if (profile != null) {
                        String fullName = (String) profile.get("fullName");
                        String avatarUrl = (String) profile.get("avatarUrl");
                        TextView tvName = commentView.findViewById(R.id.tv_comment_name);
                        ImageView ivAvatar = commentView.findViewById(R.id.iv_comment_avatar);
                        ImageView ivMentorBadge = commentView.findViewById(R.id.iv_comment_mentor_badge);
                        
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
            
        TextView tvLike = commentView.findViewById(R.id.tv_comment_like);
        if (tvLike != null) {
            String currentUid = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();
            boolean isLiked = comment.getLikedBy() != null && comment.getLikedBy().contains(currentUid);
            
            tvLike.setText("Thích");
            if (isLiked) {
                tvLike.setTextColor(android.graphics.Color.parseColor("#1877F2"));
            } else {
                tvLike.setTextColor(android.graphics.Color.parseColor("#65676B"));
            }

            View llLikeCount = commentView.findViewById(R.id.ll_comment_like_count);
            TextView tvLikeCountNumber = commentView.findViewById(R.id.tv_comment_like_count_number);
            if (llLikeCount != null && tvLikeCountNumber != null) {
                if (comment.getLikesCount() > 0) {
                    llLikeCount.setVisibility(View.VISIBLE);
                    tvLikeCountNumber.setText(String.valueOf(comment.getLikesCount()));
                } else {
                    llLikeCount.setVisibility(View.GONE);
                }
            }
            
            tvLike.setOnClickListener(v -> {
                if (currentUid == null) {
                    android.widget.Toast.makeText(this, "Vui lòng đăng nhập!", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }
                com.google.firebase.firestore.DocumentReference commentRef = FirebaseFirestore.getInstance().collection("Posts").document(postId).collection("Comments").document(comment.getId());
                FirebaseFirestore.getInstance().runTransaction(transaction -> {
                    com.google.firebase.firestore.DocumentSnapshot snapshot = transaction.get(commentRef);
                    com.example.appdraw.model.Comment c = snapshot.toObject(com.example.appdraw.model.Comment.class);
                    if (c != null) {
                        if (c.getLikedBy() == null) c.setLikedBy(new java.util.ArrayList<>());
                        boolean currentlyLiked = c.getLikedBy().contains(currentUid);
                        if (currentlyLiked) {
                            c.getLikedBy().remove(currentUid);
                            c.setLikesCount(Math.max(0, c.getLikesCount() - 1));
                        } else {
                            c.getLikedBy().add(currentUid);
                            c.setLikesCount(c.getLikesCount() + 1);
                            if (c.getUid() != null && !c.getUid().equals(currentUid)) {
                                com.example.appdraw.utils.NotificationHelper.sendNotification(c.getUid(), "COMMENT_LIKE", "đã thích bình luận của bạn.", postId);
                            }
                        }
                        transaction.set(commentRef, c);
                        return !currentlyLiked;
                    }
                    return null;
                }).addOnSuccessListener(newIsLiked -> {
                    if (newIsLiked != null) {
                        if ((Boolean) newIsLiked) {
                            tvLike.setTextColor(android.graphics.Color.parseColor("#1877F2"));
                        } else {
                            tvLike.setTextColor(android.graphics.Color.parseColor("#65676B"));
                        }
                    }
                });
            });
        }

        TextView tvReply = commentView.findViewById(R.id.tv_comment_reply);
        if (tvReply != null) {
            tvReply.setOnClickListener(v -> {
                android.widget.EditText etComment = findViewById(R.id.et_comment);
                if (etComment != null) {
                    TextView tvName = commentView.findViewById(R.id.tv_comment_name);
                    String name = tvName != null ? tvName.getText().toString() : "Người dùng";
                    
                    replyingToCommentId = isReply ? comment.getParentId() : comment.getId();
                    replyingToUserName = name;
                    
                    View llReplyStatus = findViewById(R.id.ll_reply_status);
                    if (llReplyStatus != null) llReplyStatus.setVisibility(View.VISIBLE);
                    TextView tvReplyStatus = findViewById(R.id.tv_reply_status);
                    if (tvReplyStatus != null) tvReplyStatus.setText("Đang trả lời " + name);
                    
                    etComment.setHint("Viết phản hồi...");
                    etComment.setText("");
                    etComment.requestFocus();
                    android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                    if (imm != null) imm.showSoftInput(etComment, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
                }
            });
        }

        String currentUidForDelete = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();
        if (currentUidForDelete != null && currentUidForDelete.equals(comment.getUid())) {
            commentView.setOnLongClickListener(v -> {
                new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Xóa bình luận")
                    .setMessage("Bạn có chắc chắn muốn xóa bình luận này không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("Posts").document(postId).collection("Comments").document(comment.getId())
                            .delete().addOnSuccessListener(aVoid -> {
                                db.collection("Posts").document(postId).update("commentsCount", com.google.firebase.firestore.FieldValue.increment(-1));
                                android.widget.Toast.makeText(this, "Đã xóa bình luận", android.widget.Toast.LENGTH_SHORT).show();
                            });
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
                return true;
            });
        }

        container.addView(commentView);
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

                            // Xử lý nút Theo dõi
                            TextView tvFollowStatus = includedPost.findViewById(R.id.tv_follow_status);
                            String currentUid = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();
                            if (tvFollowStatus != null && currentUid != null) {
                                if (post.getUid() != null && post.getUid().equals(currentUid)) {
                                    tvFollowStatus.setVisibility(View.GONE);
                                } else {
                                    tvFollowStatus.setVisibility(View.VISIBLE);
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    com.google.firebase.firestore.DocumentReference followRef = db.collection("Follows").document(currentUid + "_" + post.getUid());
                                    followRef.addSnapshotListener((d, e) -> {
                                        if (e != null) return;
                                        if (d != null && d.exists()) {
                                            tvFollowStatus.setText("Đang theo dõi");
                                            tvFollowStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#EFEFEF")));
                                            tvFollowStatus.setTextColor(android.graphics.Color.parseColor("#1A1A1A"));
                                            tvFollowStatus.setTag(true);
                                        } else {
                                            tvFollowStatus.setText("Theo dõi");
                                            tvFollowStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4272D0")));
                                            tvFollowStatus.setTextColor(android.graphics.Color.WHITE);
                                            tvFollowStatus.setTag(false);
                                        }
                                        tvFollowStatus.setEnabled(true);
                                    });

                                    tvFollowStatus.setOnClickListener(v -> {
                                        tvFollowStatus.setEnabled(false);
                                        boolean isFollowing = tvFollowStatus.getTag() != null && (boolean)tvFollowStatus.getTag();
                                        if (!isFollowing) {
                                            java.util.Map<String, Object> data = new java.util.HashMap<>();
                                            data.put("follower", currentUid);
                                            data.put("following", post.getUid());
                                            data.put("timestamp", System.currentTimeMillis());
                                            followRef.set(data).addOnSuccessListener(aVoid -> {
                                                db.collection("Users").document(post.getUid()).update("followersCount", com.google.firebase.firestore.FieldValue.increment(1));
                                                db.collection("Users").document(currentUid).update("followingCount", com.google.firebase.firestore.FieldValue.increment(1));
                                                android.widget.Toast.makeText(this, "Đã theo dõi", android.widget.Toast.LENGTH_SHORT).show();
                                                com.example.appdraw.utils.NotificationHelper.sendNotification(post.getUid(), "FOLLOW", "đánh giá cao bài viết và đã bắt đầu theo dõi bạn.", currentUid);
                                            });
                                        } else {
                                            followRef.delete().addOnSuccessListener(aVoid -> {
                                                db.collection("Users").document(post.getUid()).update("followersCount", com.google.firebase.firestore.FieldValue.increment(-1));
                                                db.collection("Users").document(currentUid).update("followingCount", com.google.firebase.firestore.FieldValue.increment(-1));
                                                android.widget.Toast.makeText(this, "Bỏ theo dõi", android.widget.Toast.LENGTH_SHORT).show();
                                            });
                                        }
                                    });
                                }
                            }

                            // Like Logic
                            View llLike = includedPost.findViewById(R.id.ll_like);
                            ImageView ivLike = includedPost.findViewById(R.id.iv_like);
                            if (llLike != null && ivLike != null && tvLikeCount != null) {
                                boolean isLiked = post.getLikedBy() != null && post.getLikedBy().contains(currentUid);
                                if (isLiked) {
                                    ivLike.setImageResource(R.drawable.ic_heart);
                                    ivLike.setColorFilter(android.graphics.Color.parseColor("#E91E63"));
                                } else {
                                    ivLike.setImageResource(R.drawable.ic_heart);
                                    ivLike.setColorFilter(android.graphics.Color.parseColor("#888888"));
                                }

                                llLike.setOnClickListener(v -> {
                                    if (currentUid == null) {
                                        android.widget.Toast.makeText(this, "Vui lòng đăng nhập!", android.widget.Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    com.google.firebase.firestore.DocumentReference postRef = doc.getReference();
                                    FirebaseFirestore.getInstance().runTransaction(transaction -> {
                                        com.google.firebase.firestore.DocumentSnapshot snapshot = transaction.get(postRef);
                                        Post p = snapshot.toObject(Post.class);
                                        if (p != null) {
                                            if (p.getLikedBy() == null) p.setLikedBy(new java.util.ArrayList<>());
                                            boolean currentlyLiked = p.getLikedBy().contains(currentUid);
                                            if (currentlyLiked) {
                                                p.getLikedBy().remove(currentUid);
                                                p.setLikesCount(Math.max(0, p.getLikesCount() - 1));
                                            } else {
                                                p.getLikedBy().add(currentUid);
                                                p.setLikesCount(p.getLikesCount() + 1);
                                                if (!p.getUid().equals(currentUid)) {
                                                    com.example.appdraw.utils.NotificationHelper.sendNotification(p.getUid(), "LIKE", "đã thích bài viết của bạn.", p.getId());
                                                }
                                            }
                                            transaction.set(postRef, p);
                                            return !currentlyLiked;
                                        }
                                        return null;
                                    }).addOnSuccessListener(newIsLiked -> {
                                        if (newIsLiked != null) {
                                            if ((Boolean) newIsLiked) {
                                                ivLike.setImageResource(R.drawable.ic_heart);
                                                ivLike.setColorFilter(android.graphics.Color.parseColor("#E91E63"));
                                                int currentCount = Integer.parseInt(tvLikeCount.getText().toString());
                                                tvLikeCount.setText(String.valueOf(currentCount + 1));
                                            } else {
                                                ivLike.setColorFilter(android.graphics.Color.parseColor("#888888"));
                                                int currentCount = Integer.parseInt(tvLikeCount.getText().toString());
                                                tvLikeCount.setText(String.valueOf(Math.max(0, currentCount - 1)));
                                            }
                                        }
                                    }).addOnFailureListener(e -> android.widget.Toast.makeText(this, "Lỗi mạng", android.widget.Toast.LENGTH_SHORT).show());
                                });
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
