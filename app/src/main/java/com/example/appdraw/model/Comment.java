package com.example.appdraw.model;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Vũ Quang Vinh
 * @version 1.0
 */
public class Comment {
    private String id;
    private String uid;
    private String content;
    private long createdAt;
    private int likesCount = 0;
    private java.util.List<String> likedBy = new java.util.ArrayList<>();
    private String parentId = null;

    public Comment() {}

    public Comment(String id, String uid, String content, long createdAt) {
        this.id = id;
        this.uid = uid;
        this.content = content;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public int getLikesCount() { return likesCount; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }
    public java.util.List<String> getLikedBy() { return likedBy; }
    public void setLikedBy(java.util.List<String> likedBy) { this.likedBy = likedBy; }
    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }
}
