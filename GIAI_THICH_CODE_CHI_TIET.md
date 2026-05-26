# GIẢI THÍCH CHI TIẾT CỐ CODE - APP-DRAW

---

## MỤC LỤC

1. **Module 1: Học tập & Trực tuyến (Lê Thùy Linh)**
2. **Module 2: Cộng đồng & Khám phá (Cao Đức Mạnh)**
3. **Module 3: Thử thách, Sự kiện & Dự án (Đặng Thị Hồng Vân)**
4. **Module 4: Kiến trúc Lõi & Công cụ Sáng tạo (Vũ Quang Vinh)**

---

# MODULE 1: HỌC TẬP & TRỰC TUYẾN (LÊ THÙY LINH)

## A. DANH SÁCH CÁC LỚP (Classes)

### 1. **Lớp Lesson** (POJO - Plain Old Java Object)

**Mục đích:** Đại diện một bài học trong hệ thống

**Các thuộc tính (Properties):**
- `lessonId` (String): ID duy nhất để xác định bài học trong Firestore
- `title` (String): Tiêu đề bài học (ví dụ: "Vẽ chân dung cơ bản")
- `description` (String): Mô tả chi tiết về bài học
- `category` (String): Danh mục (Cơ bản, Nâng cao, Kỹ thuật, v.v.)
- `difficultyLevel` (String): Mức độ khó (Beginner, Intermediate, Advanced)
- `tutorialImages` (List<String>): Danh sách URL hình ảnh hướng dẫn
- `videoUrl` (String): URL video tutorial
- `steps` (List<Step>): Danh sách các bước vẽ
- `createdAt` (long): Thời gian tạo (milliseconds)
- `authorId` (String): ID mentor tạo bài học
- `likes` (int): Số lượt thích
- `rating` (double): Đánh giá trung bình

**Ví dụ dữ liệu thực:**
```
{
  "lessonId": "lesson_portrait_001",
  "title": "Vẽ Chân Dung Cơ Bản",
  "description": "Bài học này hướng dẫn bạn vẽ chân dung từ cơ bản...",
  "category": "Portrait",
  "difficultyLevel": "Beginner",
  "videoUrl": "https://...",
  "createdAt": 1704067200000,
  "likes": 1250,
  "rating": 4.8
}
```

### 2. **Lớp Step** (POJO)

**Mục đích:** Đại diện một bước trong bài hướng dẫn

**Các thuộc tính:**
- `stepNumber` (int): Thứ tự bước (1, 2, 3, ...)
- `description` (String): Mô tả chi tiết bước này
- `imageBase64` (String): Ảnh hướng dẫn mã hóa Base64
- `tips` (String): Mẹo để thực hiện bước tốt hơn
- `duration` (int): Thời gian ước tính (giây)

**Giải thích:**
- Mỗi bài học gồm nhiều Step
- Step 1 có thể là "Vẽ đường nét mặt", Step 2 là "Vẽ mắt", v.v.
- `imageBase64` là hình ảnh được mã hóa để lưu trực tiếp trong Firestore

### 3. **Lớp Submission** (POJO)

**Mục đích:** Lưu kết quả bài tập của người dùng

**Các thuộc tính:**
- `submissionId` (String): ID duy nhất
- `userId` (String): ID người nộp bài
- `lessonId` (String): ID bài học mà người này nộp
- `imageBase64` (String): Tác phẩm của người dùng (ảnh vẽ)
- `submittedAt` (long): Thời gian nộp
- `status` (String): Trạng thái (Pending = chưa chấm, Evaluated = đã chấm)
- `score` (double): Điểm từ AI (0-100)
- `feedback` (String): Phản hồi từ Chatbot AI

**Ví dụ flow:**
1. Người dùng hoàn thành bài tập → click Submit
2. Hệ thống lưu Submission với status = "Pending"
3. Trigger Chatbot AI → chấm điểm
4. Cập nhật status = "Evaluated" + score + feedback

### 4. **Lớp ChatbotFeedback** (POJO)

**Mục đích:** Lưu phản hồi chi tiết từ AI

**Các thuộc tính:**
- `feedbackId` (String): ID duy nhất
- `submissionId` (String): ID bài nộp được đánh giá
- `score` (double): Điểm (0-100)
- `evaluation` (String): Đánh giá chi tiết (150-300 từ)
- `suggestions` (List<String>): 3-5 gợi ý cải thiện
- `generatedAt` (long): Thời gian AI tạo phản hồi

**Ví dụ:**
```
{
  "feedbackId": "fb_001",
  "submissionId": "sub_001",
  "score": 78.5,
  "evaluation": "Bạn đã vẽ rất tốt chân dung. Tỷ lệ khuôn mặt chính xác 90%...",
  "suggestions": [
    "Cần che phủ bóng tối ở góc hàm",
    "Tăng độ chi tiết ở vùng mắt",
    "Mịn hơn các nét line bên cạnh"
  ]
}
```

### 5. **Lớp SearchQuery** (POJO)

**Mục đích:** Hỗ trợ tìm kiếm và lọc dữ liệu

**Các thuộc tính:**
- `keyword` (String): Từ khóa tìm kiếm ("portrait", "landscape", "beginner")
- `filterType` (String): Loại bộ lọc (Lesson, Artwork, User)
- `category` (String): Danh mục cụ thể
- `sortBy` (String): Sắp xếp theo (Rating, Date, Popularity)

**Ví dụ:**
```
SearchQuery query = new SearchQuery();
query.setKeyword("chân dung");
query.setFilterType("Lesson");
query.setCategory("Portrait");
query.setSortBy("Rating");
// Tìm các bài học về chân dung, sắp xếp theo rating cao nhất
```

### 6. **Lớp TrendingArtwork** (POJO)

**Mục đích:** Đại diện tác phẩm nổi bật

**Các thuộc tính:**
- `artworkId` (String): ID tác phẩm
- `userId` (String): ID tác giả
- `imageBase64` (String): Ảnh tác phẩm
- `title` (String): Tiêu đề
- `likes` (int): Số thích
- `comments` (int): Số bình luận
- `trendScore` (double): Điểm xu hướng (tính toán từ likes, comments, thời gian)
- `uploadedAt` (long): Thời gian đăng

**Tính trendScore:**
```
trendScore = (likes * 2 + comments * 1.5) / (hiện tại - uploadedAt)
```

---

## B. DANH SÁCH CÁC VIEWMODEL (Business Logic)

### 1. **SearchViewModel** - Quản lý tìm kiếm

**Mục đích chính:** Xử lý logic tìm kiếm và lọc

**Các hàm/Method chính:**

#### `searchLessons(String keyword, String category)`
```java
public void searchLessons(String keyword, String category) {
    // Tìm bài học có:
    // 1. Title hoặc Description chứa keyword
    // 2. Category khớp với bộ lọc
    
    db.collection("Lessons")
        .whereArrayContains("keywords", keyword)
        .whereEqualTo("category", category)
        .get()
        .addOnSuccessListener(querySnapshot -> {
            // Xử lý kết quả
        });
}
```
**Giải thích:**
- `whereArrayContains("keywords", keyword)`: Tìm trong mảng keywords đã được tách từ title
- `whereEqualTo("category", category)`: Lọc theo danh mục chính xác
- Kết quả được trả về thông qua LiveData observers

#### `filterByDifficulty(String level)`
```java
public void filterByDifficulty(String level) {
    // Lọc bài học theo mức độ (Beginner, Intermediate, Advanced)
    db.collection("Lessons")
        .whereEqualTo("difficultyLevel", level)
        .get();
}
```

### 2. **LessonViewModel** - Quản lý bài học

**Mục đích:** Tải và hiển thị chi tiết bài học

#### `loadLesson(String lessonId)`
```java
public void loadLesson(String lessonId) {
    db.collection("Lessons")
        .document(lessonId)
        .get()
        .addOnSuccessListener(documentSnapshot -> {
            Lesson lesson = documentSnapshot.toObject(Lesson.class);
            currentLesson.setValue(lesson);
            // UI tự động cập nhật qua LiveData Observer
        });
}
```
**Giải thích:**
- Tải bài học từ Firestore bằng lessonId
- Chuyển đổi DocumentSnapshot thành Lesson object
- Cập nhật LiveData → UI tự động refresh

### 3. **SubmitViewModel** - Quản lý nộp bài

**Mục đích:** Xử lý logic nộp bài tập

#### `submitArtwork(String userId, String lessonId, String imageBase64)`
```java
public void submitArtwork(String userId, String lessonId, String imageBase64) {
    Submission submission = new Submission();
    submission.setSubmissionId(UUID.randomUUID().toString()); // ID duy nhất
    submission.setUserId(userId);
    submission.setLessonId(lessonId);
    submission.setImageBase64(imageBase64); // Ảnh vẽ của user
    submission.setSubmittedAt(System.currentTimeMillis()); // Thời gian hiện tại
    submission.setStatus("Pending"); // Chưa có điểm
    
    // Lưu vào Firestore
    db.collection("Submissions")
        .add(submission)
        .addOnSuccessListener(documentReference -> {
            // Sau khi lưu, gọi Chatbot để chấm
            sendToChatbot(submission);
            submitStatus.setValue("Success");
        });
}
```
**Flow chi tiết:**
1. Tạo object Submission mới
2. Điền thông tin từ user (userId, lessonId, imageBase64)
3. Lưu vào Firestore collection "Submissions"
4. Nếu thành công → trigger ChatbotViewModel.evaluateSubmission()

### 4. **ChatbotViewModel** - Quản lý AI chấm điểm

**Mục đích:** Gọi Gemini AI API để chấm bài

#### `evaluateSubmission(Submission submission)`
```java
public void evaluateSubmission(Submission submission) {
    // Gọi Gemini AI API với ảnh Base64
    apiService.evaluateArtwork(submission.getImageBase64())
        .enqueue(new Callback<ChatbotFeedback>() {
            @Override
            public void onResponse(Call<ChatbotFeedback> call, 
                                  Response<ChatbotFeedback> response) {
                ChatbotFeedback feedback = response.body();
                
                // Cập nhật Submission với score
                db.collection("Submissions")
                    .document(submission.getSubmissionId())
                    .update(
                        "status", "Evaluated",
                        "score", feedback.getScore(),
                        "feedback", feedback.getEvaluation()
                    );
                
                feedbackResult.setValue(feedback);
            }
        });
}
```
**Giải thích chi tiết:**
- `apiService.evaluateArtwork()`: Gọi API Gemini
- Gửi ảnh Base64 của user
- API trả về ChatbotFeedback (score + comments)
- Cập nhật Submission trong Firestore
- Notify UI thông qua LiveData

### 5. **TrendingViewModel** - Quản lý tác phẩm xu hướng

**Mục đích:** Tải những tác phẩm nổi bật/trending

#### `loadTrendingArtworks()`
```java
public void loadTrendingArtworks() {
    // Query tác phẩm có trendScore cao nhất (top 20)
    db.collection("Artworks")
        .orderBy("trendScore", Query.Direction.DESCENDING)
        .limit(20)
        .get()
        .addOnSuccessListener(querySnapshot -> {
            List<TrendingArtwork> artworks = new ArrayList<>();
            
            for (DocumentSnapshot doc : querySnapshot) {
                TrendingArtwork artwork = doc.toObject(TrendingArtwork.class);
                artworks.add(artwork);
            }
            
            trendingArtworks.setValue(artworks);
        });
}
```
**Giải thích:**
- Sắp xếp theo trendScore giảm dần (cao nhất trước)
- Chỉ lấy 20 tác phẩm hàng đầu
- Hiển thị dạng grid 2 cột trên UI

---

## C. DANH SÁCH CÁC REPOSITORY (Data Access Layer)

### 1. **LessonRepository**

**Mục đích:** Truy vấn Firestore cho bài học

```java
public class LessonRepository {
    private FirebaseFirestore db;
    
    // Lấy bài học theo ID
    public Task<Lesson> getLessonById(String lessonId) {
        return db.collection("Lessons")
            .document(lessonId)
            .get()
            .continueWith(task -> task.getResult().toObject(Lesson.class));
    }
    
    // Tìm kiếm theo từ khóa
    public Task<QuerySnapshot> searchLessons(String keyword) {
        return db.collection("Lessons")
            .whereArrayContains("keywords", keyword)
            .get();
    }
    
    // Lọc theo danh mục
    public Task<QuerySnapshot> getLessonsByCategory(String category) {
        return db.collection("Lessons")
            .whereEqualTo("category", category)
            .get();
    }
}
```

### 2. **SubmissionRepository**

**Mục đích:** Quản lý bài nộp trong Firestore

```java
public class SubmissionRepository {
    private FirebaseFirestore db;
    
    // Lưu bài nộp
    public Task<DocumentReference> saveSubmission(Submission submission) {
        return db.collection("Submissions").add(submission);
    }
    
    // Lấy các bài nộp của user
    public Task<QuerySnapshot> getUserSubmissions(String userId) {
        return db.collection("Submissions")
            .whereEqualTo("userId", userId)
            .get();
    }
}
```

### 3. **ChatbotRepository**

**Mục đích:** Gọi Gemini AI và lưu kết quả

```java
public class ChatbotRepository {
    private RetrofitAPI apiService;
    private FirebaseFirestore db;
    
    // Gọi API Gemini
    public Task<ChatbotFeedback> evaluateSubmission(Submission submission) {
        return Tasks.call(executor, () -> {
            Response<ChatbotFeedback> response = 
                apiService.evaluateArtwork(submission.getImageBase64()).execute();
            
            ChatbotFeedback feedback = response.body();
            
            // Lưu feedback vào Firestore
            db.collection("ChatbotFeedbacks").add(feedback);
            
            return feedback;
        });
    }
}
```

---

## D. DANH SÁCH CÁC COLLECTIONS TRONG FIRESTORE

### 1. **Collection: Lessons**
```firestore
/Lessons/{lessonId}
{
  "lessonId": "lesson_001",
  "title": "Vẽ Chân Dung Cơ Bản",
  "description": "Hướng dẫn vẽ chân dung...",
  "category": "Portrait",
  "difficultyLevel": "Beginner",
  "tutorialImages": [
    "data:image/jpeg;base64,...",
    "data:image/jpeg;base64,..."
  ],
  "videoUrl": "https://youtube.com/watch?v=...",
  "steps": [
    {
      "stepNumber": 1,
      "description": "Vẽ hình oval mặt",
      "imageBase64": "data:image/jpeg;base64,...",
      "tips": "Hình oval nên hơi dài từ trên xuống",
      "duration": 120
    },
    {
      "stepNumber": 2,
      "description": "Chia các đường nét",
      "imageBase64": "data:image/jpeg;base64,...",
      "tips": "Dùng các đường nhẹ",
      "duration": 180
    }
  ],
  "createdAt": 1704067200000,
  "authorId": "mentor_001",
  "likes": 1250,
  "rating": 4.8
}
```

### 2. **Collection: Submissions**
```firestore
/Submissions/{submissionId}
{
  "submissionId": "sub_001",
  "userId": "user_123",
  "lessonId": "lesson_001",
  "imageBase64": "data:image/png;base64,...(ảnh user vẽ)",
  "submittedAt": 1704153600000,
  "status": "Evaluated",
  "score": 78.5,
  "feedback": "Bạn đã vẽ tốt. Chân dung rất giống..."
}
```

### 3. **Collection: ChatbotFeedbacks**
```firestore
/ChatbotFeedbacks/{feedbackId}
{
  "feedbackId": "fb_001",
  "submissionId": "sub_001",
  "score": 78.5,
  "evaluation": "Bạn đã vẽ rất tốt. Tỷ lệ khuôn mặt chính xác 90%...",
  "suggestions": [
    "Cần che phủ bóng tối ở góc hàm",
    "Tăng độ chi tiết ở vùng mắt",
    "Mịn hơn các nét line"
  ],
  "generatedAt": 1704153600000
}
```

### 4. **Collection: Artworks**
```firestore
/Artworks/{artworkId}
{
  "artworkId": "art_001",
  "userId": "user_123",
  "imageBase64": "data:image/png;base64,...",
  "title": "Tác phẩm yêu thích của tôi",
  "likes": 450,
  "comments": 23,
  "trendScore": 156.8,
  "uploadedAt": 1704067200000
}
```

---

## E. CÁC API GỌI NGOÀI

### 1. **Gemini AI API** (Chấm điểm tác phẩm)

**Endpoint:** `POST /v1/generateContent` (Google Gemini API)

**Request Body:**
```json
{
  "contents": [{
    "parts": [{
      "text": "Analyze this artwork and provide feedback",
      "inline_data": {
        "mime_type": "image/jpeg",
        "data": "base64_encoded_image"
      }
    }]
  }],
  "generationConfig": {
    "temperature": 0.7,
    "max_output_tokens": 1000
  }
}
```

**Response:**
```json
{
  "score": 78.5,
  "evaluation": "Bạn đã vẽ rất tốt...",
  "suggestions": [
    "Cần che phủ bóng tối",
    "Tăng độ chi tiết mắt"
  ]
}
```

**Trong code:**
```java
apiService.evaluateArtwork(submission.getImageBase64())
    .enqueue(new Callback<ChatbotFeedback>() {
        @Override
        public void onResponse(Call<ChatbotFeedback> call, Response<ChatbotFeedback> response) {
            // Xử lý response
        }
    });
```

### 2. **Firebase Cloud Functions** (Push Notifications)

**Function:** `notifyUserOfFeedback()`
```javascript
exports.notifyUserOfFeedback = functions.firestore
    .document('Submissions/{submissionId}')
    .onUpdate(async (change, context) => {
        const newData = change.after.data();
        
        if (newData.status === 'Evaluated') {
            // Gửi push notification cho user
            await admin.messaging().send({
                token: userToken,
                notification: {
                    title: 'Bài tập đã được chấm',
                    body: `Điểm: ${newData.score}/100`
                },
                data: {
                    submissionId: context.params.submissionId
                }
            });
        }
    });
```

---

# MODULE 2: CỘNG ĐỒNG & KHÁM PHÁ (CAO ĐỨC MẠNH)

## A. DANH SÁCH CÁC LỚP (Classes)

### 1. **Lớp Post** (POJO)

**Mục đích:** Đại diện một bài viết trên feed

**Các thuộc tính:**
- `postId` (String): ID duy nhất
- `userId` (String): ID tác giả
- `imageBase64` (String): Ảnh tác phẩm (Base64)
- `caption` (String): Chú thích/mô tả bài viết
- `hashtags` (List<String>): Hashtag (#painting, #portrait, ...)
- `createdAt` (long): Thời gian tạo
- `likes` (int): Số lượt thích
- `commentCount` (int): Số bình luận
- `shares` (int): Số lần chia sẻ
- `status` (String): (Active / Deleted)

**Ví dụ:**
```
{
  "postId": "post_001",
  "userId": "user_456",
  "caption": "Tác phẩm vẽ lần đầu! 🎨 #art #sketch",
  "hashtags": ["art", "sketch", "beginner"],
  "createdAt": 1704067200000,
  "likes": 234,
  "commentCount": 12,
  "shares": 5
}
```

### 2. **Lớp Comment** (POJO)

**Mục đích:** Bình luận trên bài viết

**Các thuộc tính:**
- `commentId` (String): ID duy nhất
- `postId` (String): ID bài viết cha
- `userId` (String): ID người bình luận
- `content` (String): Nội dung bình luận
- `createdAt` (long): Thời gian
- `likes` (int): Số lượt thích comment
- `replies` (List<Reply>): Danh sách reply

**Ví dụ:**
```
{
  "commentId": "cmt_001",
  "postId": "post_001",
  "userId": "user_123",
  "content": "Tác phẩm đẹp lắm! Bạn dùng công cụ gì vẽ?",
  "createdAt": 1704153600000,
  "likes": 5
}
```

### 3. **Lớp Livestream** (POJO)

**Mục đích:** Thông tin phát trực tiếp

**Các thuộc tính:**
- `livestreamId` (String): ID duy nhất
- `mentorId` (String): ID mentor phát
- `title` (String): Tiêu đề livestream
- `description` (String): Mô tả
- `startTime` (long): Thời gian bắt đầu
- `endTime` (long): Thời gian kết thúc
- `zegoRoomId` (String): Room ID trong ZegoCloud RTC
- `zegoToken` (String): Token để vào phòng
- `viewerCount` (int): Số người xem hiện tại
- `status` (String): (Scheduled / Live / Ended)
- `tags` (List<String>): Tags (#tutorial, #advanced)

**Ví dụ:**
```
{
  "livestreamId": "ls_001",
  "mentorId": "mentor_001",
  "title": "Live Tutorial: Vẽ Cảnh Phong Cảnh",
  "startTime": 1704240000000,
  "zegoRoomId": "room_123456",
  "viewerCount": 234,
  "status": "Live"
}
```

### 4. **Lớp LiveComment** (POJO)

**Mục đích:** Bình luận realtime trong livestream

**Các thuộc tính:**
- `commentId` (String): ID duy nhất
- `livestreamId` (String): ID livestream
- `userId` (String): ID người comment
- `message` (String): Nội dung comment
- `timestamp` (long): Thời gian

**Ví dụ:**
```
{
  "commentId": "lcmt_001",
  "livestreamId": "ls_001",
  "userId": "user_789",
  "message": "Bạn vẽ tuyệt quá! Làm lại bước đó được không?",
  "timestamp": 1704240120000
}
```

### 5. **Lớp Like** (POJO)

**Mục đích:** Quản lý hệ thống like

**Các thuộc tính:**
- `likeId` (String): ID duy nhất
- `userId` (String): ID người like
- `targetId` (String): ID object được like (postId hoặc commentId)
- `targetType` (String): (Post / Comment / Livestream)
- `createdAt` (long): Thời gian

**Ví dụ:**
```
{
  "likeId": "like_001",
  "userId": "user_123",
  "targetId": "post_001",
  "targetType": "Post",
  "createdAt": 1704153600000
}
```

---

## B. CÁC VIEWMODEL (Business Logic)

### 1. **FeedViewModel** - Quản lý bảng tin

**Mục đích:** Tải và quản lý feed posts

#### `loadFeed()`
```java
public void loadFeed() {
    loadingStatus.setValue("Loading");
    
    db.collection("Posts")
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .limit(PAGE_SIZE) // PAGE_SIZE = 10
        .get()
        .addOnSuccessListener(querySnapshot -> {
            List<Post> posts = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot) {
                Post post = doc.toObject(Post.class);
                posts.add(post);
            }
            
            // Lưu lastDocument để phục vụ pagination
            if (!querySnapshot.isEmpty()) {
                lastDocument = querySnapshot.getDocuments()
                    .get(querySnapshot.size() - 1);
            }
            
            feedPosts.setValue(posts);
            loadingStatus.setValue("Success");
        });
}
```
**Giải thích:**
- Tải 10 bài viết mới nhất trước
- `lastDocument` dùng để load thêm khi scroll
- LiveData tự động notify UI khi có dữ liệu

#### `loadMore()`
```java
public void loadMore() {
    if (lastDocument == null) return; // Không có dữ liệu trước
    
    db.collection("Posts")
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .startAfter(lastDocument) // Bắt đầu sau document trước
        .limit(PAGE_SIZE)
        .get()
        .addOnSuccessListener(querySnapshot -> {
            List<Post> posts = new ArrayList<>(feedPosts.getValue());
            
            // Thêm vào cuối danh sách hiện tại
            for (DocumentSnapshot doc : querySnapshot) {
                Post post = doc.toObject(Post.class);
                posts.add(post);
            }
            
            // Cập nhật lastDocument
            if (!querySnapshot.isEmpty()) {
                lastDocument = querySnapshot.getDocuments()
                    .get(querySnapshot.size() - 1);
            }
            
            feedPosts.setValue(posts);
        });
}
```
**Giải thích - Pagination:**
- User scroll xuống → gọi loadMore()
- `startAfter(lastDocument)`: Bắt đầu từ sau document cuối của load trước
- Vậy k bị load lại bài cũ

### 2. **PostViewModel** - Tạo bài viết

**Mục đích:** Xử lý logic đăng bài mới

#### `createPost(String imageBase64, String caption, List<String> hashtags)`
```java
public void createPost(String imageBase64, String caption, List<String> hashtags) {
    postStatus.setValue("Posting...");
    
    Post post = new Post();
    post.setPostId(UUID.randomUUID().toString()); // Tạo ID duy nhất
    post.setUserId(auth.getCurrentUser().getUid()); // User hiện tại
    post.setImageBase64(imageBase64); // Ảnh đã chuyển Base64
    post.setCaption(caption); // Chú thích
    post.setHashtags(hashtags); // Hashtags
    post.setCreatedAt(System.currentTimeMillis()); // Thời gian hiện tại
    post.setLikes(0);
    post.setCommentCount(0);
    post.setShares(0);
    post.setStatus("Active");
    
    // Lưu vào Firestore
    db.collection("Posts")
        .document(post.getPostId())
        .set(post)
        .addOnSuccessListener(aVoid -> {
            postStatus.setValue("Success");
            // Trigger Cloud Function để tính trendScore
        })
        .addOnFailureListener(e -> {
            postStatus.setValue("Error: " + e.getMessage());
        });
}
```
**Flow:**
1. User chọn ảnh + viết caption
2. Nhấn Post button
3. ViewModel tạo Post object
4. Lưu vào Firestore "Posts" collection
5. Cloud Function auto tính trendScore
6. Post xuất hiện trên feed của mọi người

### 3. **CommentViewModel** - Quản lý bình luận

#### `loadComments(String postId)`
```java
public void loadComments(String postId) {
    db.collection("Posts")
        .document(postId)
        .collection("Comments") // Sub-collection
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .get()
        .addOnSuccessListener(querySnapshot -> {
            List<Comment> commentList = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot) {
                Comment comment = doc.toObject(Comment.class);
                commentList.add(comment);
            }
            comments.setValue(commentList);
        });
}
```
**Giải thích - Sub-collection:**
- Comments được lưu dưới `/Posts/{postId}/Comments/`
- Tách riêng để dễ quản lý
- `addSnapshotListener()` để realtime updates

#### `addComment(String postId, String content)`
```java
public void addComment(String postId, String content) {
    Comment comment = new Comment();
    comment.setCommentId(UUID.randomUUID().toString());
    comment.setPostId(postId);
    comment.setUserId(auth.getCurrentUser().getUid());
    comment.setContent(content);
    comment.setCreatedAt(System.currentTimeMillis());
    comment.setLikes(0);
    comment.setReplies(new ArrayList<>());
    
    // Lưu vào sub-collection Comments
    db.collection("Posts")
        .document(postId)
        .collection("Comments")
        .document(comment.getCommentId())
        .set(comment)
        .addOnSuccessListener(aVoid -> {
            // Tăng commentCount trên Post
            db.collection("Posts")
                .document(postId)
                .update("commentCount", FieldValue.increment(1));
        });
}
```

### 4. **LiveStreamViewModel** - Xem livestream

#### `loadUpcomingStreams()`
```java
public void loadUpcomingStreams() {
    db.collection("Livestreams")
        .whereIn("status", Arrays.asList("Scheduled", "Live"))
        .orderBy("startTime", Query.Direction.ASCENDING)
        .get()
        .addOnSuccessListener(querySnapshot -> {
            List<Livestream> streams = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot) {
                Livestream stream = doc.toObject(Livestream.class);
                streams.add(stream);
            }
            upcomingStreams.setValue(streams);
        });
}
```
**Giải thích:**
- Tìm livestream có status = "Scheduled" hoặc "Live"
- Sắp xếp theo thời gian sắp tới
- Hiển thị danh sách để user chọn

#### `joinLivestream(String livestreamId)`
```java
public void joinLivestream(String livestreamId) {
    db.collection("Livestreams")
        .document(livestreamId)
        .get()
        .addOnSuccessListener(documentSnapshot -> {
            Livestream stream = documentSnapshot.toObject(Livestream.class);
            currentStream.setValue(stream);
            
            // Tăng viewerCount
            db.collection("Livestreams")
                .document(livestreamId)
                .update("viewerCount", FieldValue.increment(1));
            
            // Kết nối ZegoCloud bằng stream.getZegoRoomId() + token
        });
}
```

### 5. **BroadcastViewModel** - Phát livestream

#### `startBroadcast(String title, String description, String zegoRoomId)`
```java
public void startBroadcast(String title, String description, String zegoRoomId) {
    Livestream broadcast = new Livestream();
    broadcast.setLiveStreamId(UUID.randomUUID().toString());
    broadcast.setMentorId(auth.getCurrentUser().getUid());
    broadcast.setTitle(title);
    broadcast.setDescription(description);
    broadcast.setStartTime(System.currentTimeMillis());
    broadcast.setZegoRoomId(zegoRoomId);
    broadcast.setZegoToken(generateZegoToken()); // Token từ ZegoServer
    broadcast.setViewerCount(0);
    broadcast.setStatus("Live");
    
    db.collection("Livestreams")
        .document(broadcast.getLiveStreamId())
        .set(broadcast)
        .addOnSuccessListener(aVoid -> {
            broadcastStatus.setValue("BroadcastStarted");
            
            // Kết nối ZegoCloud để phát stream
            zegoClient.login(zegoRoomId, broadcast.getZegoToken());
            zegoClient.startPublishing();
        });
}
```
**Flow:**
1. Mentor nhấn "Start Broadcast"
2. App tạo Livestream document
3. Kết nối ZegoCloud RTC
4. Bắt đầu phát video + audio
5. Viewers có thể join và xem realtime

---

## C. DATABASE COLLECTIONS

### 1. **Collection: Posts**
```firestore
/Posts/{postId}
{
  "postId": "post_001",
  "userId": "user_456",
  "imageBase64": "data:image/png;base64,...",
  "caption": "Tác phẩm đầu tiên! #art #sketch",
  "hashtags": ["art", "sketch", "beginner"],
  "createdAt": 1704067200000,
  "likes": 234,
  "commentCount": 12,
  "shares": 5,
  "status": "Active"
}
```

### 2. **Sub-collection: Posts/{postId}/Comments**
```firestore
/Posts/{postId}/Comments/{commentId}
{
  "commentId": "cmt_001",
  "postId": "post_001",
  "userId": "user_123",
  "content": "Tác phẩm đẹp lắm!",
  "createdAt": 1704153600000,
  "likes": 5,
  "replies": []
}
```

### 3. **Collection: Livestreams**
```firestore
/Livestreams/{livestreamId}
{
  "livestreamId": "ls_001",
  "mentorId": "mentor_001",
  "title": "Live Tutorial: Vẽ Cảnh Phong Cảnh",
  "description": "Hôm nay tôi sẽ hướng dẫn...",
  "startTime": 1704240000000,
  "endTime": 1704243600000,
  "zegoRoomId": "room_123456",
  "zegoToken": "token_...",
  "viewerCount": 234,
  "status": "Live",
  "tags": ["tutorial", "landscape"]
}
```

### 4. **Collection: Likes**
```firestore
/Likes/{likeId}
{
  "likeId": "like_001",
  "userId": "user_123",
  "targetId": "post_001",
  "targetType": "Post",
  "createdAt": 1704153600000
}
```

---

## D. EXTERNAL SERVICES

### 1. **ZegoCloud RTC SDK** (Video Streaming)

**Mục đích:** Phát và xem video livestream

**Initialization:**
```java
// Cấu hình ZegoCloud
ZegoUIKitPrebuiltLiveStreamingConfig config = 
    new ZegoUIKitPrebuiltLiveStreamingConfig();

ZegoUIKitPrebuiltLiveStreaming.addListener(new ZegoUIKitEventListener() {
    @Override
    public void onUserJoined(ZegoUIKitUser user) {
        // Khi viewer join
        Log.d("Livestream", user.userName + " joined");
    }
});
```

**Phát livestream:**
```java
zegoClient.login(roomId, token);
zegoClient.startPublishing(); // Bắt đầu phát
zegoClient.stopPublishing();  // Kết thúc phát
```

**Xem livestream:**
```java
zegoClient.login(roomId, token);
zegoClient.startPlaying(streamId); // Xem stream
```

### 2. **Firebase Cloud Functions** (Realtime Notifications)

**Function:** `onPostLiked()`
```javascript
exports.onPostLiked = functions.firestore
    .document('Likes/{likeId}')
    .onCreate(async (snap, context) => {
        const like = snap.data();
        const postId = like.targetId;
        
        // Lấy thông tin post
        const post = await admin.firestore()
            .collection('Posts')
            .document(postId)
            .get();
        
        const postData = post.data();
        
        // Gửi notification cho tác giả post
        await admin.messaging().sendToDevice(authorToken, {
            notification: {
                title: 'Bạn nhận được lượt thích',
                body: `${userName} thích bài viết của bạn`
            },
            data: {
                postId: postId
            }
        });
    });
```

---

# MODULE 3: THỬ THÁCH, SỰ KIỆN & DỰ ÁN (ĐẶNG THỊ HỒNG VÂN)

## A. DANH SÁCH CÁC LỚP (Classes)

### 1. **Lớp Project** (POJO)

**Mục đích:** Dự án/collection tác phẩm của user

**Các thuộc tính:**
- `projectId` (String): ID duy nhất
- `userId` (String): ID chủ sở hữu
- `name` (String): Tên project (ví dụ: "Chân dung gia đình")
- `description` (String): Mô tả project
- `thumbnailBase64` (String): Ảnh cover (Base64)
- `artworkIds` (List<String>): Danh sách ID tác phẩm trong project
- `createdAt` (long): Thời gian tạo
- `updatedAt` (long): Lần cuối cập nhật
- `status` (String): (Active / Archived)

### 2. **Lớp Challenge** (POJO)

**Mục đích:** Cuộc thi vẽ

**Các thuộc tính:**
- `challengeId` (String): ID duy nhất
- `title` (String): Tiêu đề thử thách
- `description` (String): Mô tả chi tiết
- `topic` (String): Chủ đề (Portrait, Landscape, ...)
- `mentorId` (String): ID mentor tạo
- `startDate` (long): Ngày bắt đầu
- `endDate` (long): Ngày kết thúc
- `submissionIds` (List<String>): Danh sách ID bài nộp
- `status` (String): (Upcoming / Active / Closed)
- `votes` (int): Tổng số vote

**Ví dụ:**
```
{
  "challengeId": "ch_001",
  "title": "Thử thách: Vẽ khuôn mặt trong mưa",
  "topic": "Portrait",
  "mentorId": "mentor_001",
  "startDate": 1704067200000,
  "endDate": 1704499200000,
  "status": "Active"
}
```

### 3. **Lớp ChallengeSubmission** (POJO)

**Mục đích:** Bài nộp cuộc thi

**Các thuộc tính:**
- `submissionId` (String): ID duy nhất
- `challengeId` (String): ID cuộc thi
- `userId` (String): ID người nộp
- `artworkBase64` (String): Tác phẩm nộp (Base64)
- `description` (String): Mô tả về bài nộp
- `submittedAt` (long): Thời gian nộp
- `votes` (int): Số lượt bình chọn

### 4. **Lớp Vote** (POJO)

**Mục đích:** Bình chọn bài tham gia thử thách

**Các thuộc tính:**
- `voteId` (String): ID duy nhất
- `submissionId` (String): ID bài nộp được vote
- `voterId` (String): ID người vote
- `votedAt` (long): Thời gian vote

### 5. **Lớp Event** (POJO)

**Mục đích:** Sự kiện (Livestream, Workshop, Hội thảo)

**Các thuộc tính:**
- `eventId` (String): ID duy nhất
- `title` (String): Tiêu đề sự kiện
- `description` (String): Mô tả
- `eventType` (String): (Livestream / Workshop / Webinar)
- `mentorId` (String): ID tổ chức
- `startTime` (long): Thời gian bắt đầu
- `endTime` (long): Thời gian kết thúc
- `location` (String): Địa điểm hoặc URL zoom
- `attendeeCount` (int): Số người tham dự
- `status` (String): (Scheduled / Live / Ended)

### 6. **Lớp EventAttendee** (POJO)

**Mục đích:** Người tham dự sự kiện

**Các thuộc tính:**
- `attendeeId` (String): ID duy nhất
- `eventId` (String): ID sự kiện
- `userId` (String): ID người tham dự
- `registeredAt` (long): Thời gian đăng ký

---

## B. CÁC VIEWMODEL

### 1. **ProjectViewModel** - Quản lý dự án

#### `loadUserProjects()`
```java
public void loadUserProjects() {
    String userId = auth.getCurrentUser().getUid();
    
    db.collection("Projects")
        .whereEqualTo("userId", userId)
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .get()
        .addOnSuccessListener(querySnapshot -> {
            List<Project> projects = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot) {
                Project project = doc.toObject(Project.class);
                projects.add(project);
            }
            userProjects.setValue(projects);
        });
}
```
**Giải thích:**
- Tìm tất cả project của user hiện tại
- Sắp xếp theo thời gian tạo mới nhất
- Hiển thị dưới dạng danh sách hoặc grid

#### `createProject(String name, String description, String thumbnailBase64)`
```java
public void createProject(String name, String description, String thumbnailBase64) {
    Project project = new Project();
    project.setProjectId(UUID.randomUUID().toString());
    project.setUserId(auth.getCurrentUser().getUid());
    project.setName(name);
    project.setDescription(description);
    project.setThumbnailBase64(thumbnailBase64);
    project.setArtworkIds(new ArrayList<>());
    project.setCreatedAt(System.currentTimeMillis());
    project.setStatus("Active");
    
    db.collection("Projects")
        .document(project.getProjectId())
        .set(project)
        .addOnSuccessListener(aVoid -> {
            operationStatus.setValue("ProjectCreated");
            loadUserProjects(); // Reload danh sách
        });
}
```

### 2. **ChallengeViewModel** - Quản lý thử thách

#### `loadActiveChallenges()`
```java
public void loadActiveChallenges() {
    db.collection("Challenges")
        .whereIn("status", Arrays.asList("Upcoming", "Active"))
        .orderBy("endDate", Query.Direction.ASCENDING)
        .get()
        .addOnSuccessListener(querySnapshot -> {
            List<Challenge> challengeList = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot) {
                Challenge challenge = doc.toObject(Challenge.class);
                challengeList.add(challenge);
            }
            challenges.setValue(challengeList);
        });
}
```

#### `submitToChallenge(String challengeId, String artworkBase64, String description)`
```java
public void submitToChallenge(String challengeId, String artworkBase64, String description) {
    ChallengeSubmission submission = new ChallengeSubmission();
    submission.setSubmissionId(UUID.randomUUID().toString());
    submission.setChallengeId(challengeId);
    submission.setUserId(auth.getCurrentUser().getUid());
    submission.setArtworkBase64(artworkBase64);
    submission.setDescription(description);
    submission.setSubmittedAt(System.currentTimeMillis());
    submission.setVotes(0);
    
    // Lưu bài nộp
    db.collection("ChallengeSubmissions")
        .document(submission.getSubmissionId())
        .set(submission)
        .addOnSuccessListener(aVoid -> {
            // Thêm ID vào danh sách submissionIds của challenge
            db.collection("Challenges")
                .document(challengeId)
                .update("submissionIds", 
                    FieldValue.arrayUnion(submission.getSubmissionId()));
            
            operationStatus.setValue("SubmissionSuccess");
        });
}
```
**Giải thích:**
- Tạo ChallengeSubmission mới
- Lưu vào "ChallengeSubmissions" collection
- Thêm ID vào mảng `submissionIds` của Challenge
- Vậy ta có thể truy vấn nhanh tất cả bài nộp của một challenge

#### `voteSubmission(String submissionId)`
```java
public void voteSubmission(String submissionId) {
    String voterId = auth.getCurrentUser().getUid();
    Vote vote = new Vote();
    vote.setVoteId(UUID.randomUUID().toString());
    vote.setSubmissionId(submissionId);
    vote.setVoterId(voterId);
    vote.setVotedAt(System.currentTimeMillis());
    
    db.collection("Votes")
        .document(vote.getVoteId())
        .set(vote)
        .addOnSuccessListener(aVoid -> {
            // Tăng vote count trên submission
            db.collection("ChallengeSubmissions")
                .document(submissionId)
                .update("votes", FieldValue.increment(1));
        });
}
```

### 3. **CalendarViewModel** - Quản lý sự kiện

#### `loadUpcomingEvents()`
```java
public void loadUpcomingEvents() {
    long currentTime = System.currentTimeMillis();
    
    db.collection("Events")
        .whereGreaterThanOrEqualTo("startTime", currentTime)
        .whereIn("status", Arrays.asList("Scheduled", "Live"))
        .orderBy("startTime", Query.Direction.ASCENDING)
        .get()
        .addOnSuccessListener(querySnapshot -> {
            List<Event> events = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot) {
                Event event = doc.toObject(Event.class);
                events.add(event);
            }
            upcomingEvents.setValue(events);
        });
}
```
**Giải thích:**
- Tìm sự kiện với startTime >= hiện tại
- Status phải là "Scheduled" hoặc "Live"
- Sắp xếp theo thời gian sắp tới
- Hiển thị trên calendar hoặc danh sách

#### `registerEvent(String eventId)`
```java
public void registerEvent(String eventId) {
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    
    EventAttendee attendee = new EventAttendee();
    attendee.setAttendeeId(UUID.randomUUID().toString());
    attendee.setEventId(eventId);
    attendee.setUserId(userId);
    attendee.setRegisteredAt(System.currentTimeMillis());
    
    // Lưu attendee registration
    db.collection("EventAttendees")
        .document(attendee.getAttendeeId())
        .set(attendee)
        .addOnSuccessListener(aVoid -> {
            // Tăng attendeeCount trên Event
            db.collection("Events")
                .document(eventId)
                .update("attendeeCount", FieldValue.increment(1));
        });
}
```

---

## C. DATABASE COLLECTIONS

### 1. **Collection: Projects**
```firestore
/Projects/{projectId}
{
  "projectId": "proj_001",
  "userId": "user_123",
  "name": "Chân dung gia đình",
  "description": "Bộ sưu tập các bức chân dung...",
  "thumbnailBase64": "data:image/png;base64,...",
  "artworkIds": ["art_001", "art_002", "art_003"],
  "createdAt": 1704067200000,
  "updatedAt": 1704153600000,
  "status": "Active"
}
```

### 2. **Collection: Challenges**
```firestore
/Challenges/{challengeId}
{
  "challengeId": "ch_001",
  "title": "Thử thách: Vẽ khuôn mặt trong mưa",
  "description": "Nộp tác phẩm về chủ đề...",
  "topic": "Portrait",
  "mentorId": "mentor_001",
  "startDate": 1704067200000,
  "endDate": 1704499200000,
  "submissionIds": ["sub_001", "sub_002", "sub_003"],
  "status": "Active",
  "votes": 1250
}
```

### 3. **Collection: ChallengeSubmissions**
```firestore
/ChallengeSubmissions/{submissionId}
{
  "submissionId": "sub_001",
  "challengeId": "ch_001",
  "userId": "user_456",
  "artworkBase64": "data:image/png;base64,...",
  "description": "Mình đã cố gắng theo hướng dẫn...",
  "submittedAt": 1704240000000,
  "votes": 345
}
```

### 4. **Collection: Events**
```firestore
/Events/{eventId}
{
  "eventId": "evt_001",
  "title": "Workshop: Kỹ thuật vẽ nước",
  "description": "Hôm nay chúng ta sẽ học...",
  "eventType": "Workshop",
  "mentorId": "mentor_001",
  "startTime": 1704326400000,
  "endTime": 1704330000000,
  "location": "https://zoom.us/j/...",
  "attendeeCount": 127,
  "status": "Scheduled"
}
```

---

# MODULE 4: KIẾN TRÚC LỐI & CÔNG CỤ SÁNG TẠO (VŨ QUANG VINH)

## A. DANH SÁCH CÁC LỚP (Classes)

### 1. **Lớp User** (POJO)

**Mục đích:** Thông tin người dùng toàn hệ thống

**Các thuộc tính:**
- `userId` (String): ID Firebase
- `email` (String): Email đăng nhập
- `username` (String): Tên hiển thị
- `avatarBase64` (String): Ảnh đại diện (Base64)
- `bio` (String): Tiểu sử ngắn
- `followers` (List<String>): Danh sách ID người theo dõi
- `following` (List<String>): Danh sách ID người đang theo dõi
- `followerCount` (int): Số người theo dõi
- `followingCount` (int): Số người đang theo dõi
- `role` (String): (User / Mentor / Admin)
- `createdAt` (long): Thời gian tạo account
- `updatedAt` (long): Lần cuối cập nhật

### 2. **Lớp Notification** (POJO)

**Mục đích:** Thông báo realtime cho người dùng

**Các thuộc tính:**
- `notificationId` (String): ID duy nhất
- `userId` (String): ID người nhận
- `actorId` (String): ID người tạo hành động
- `type` (String): (Like / Comment / Follow / Submission)
- `targetId` (String): ID object liên quan (postId, commentId, userId)
- `message` (String): Nội dung thông báo
- `read` (boolean): Đã đọc?
- `createdAt` (long): Thời gian tạo

**Ví dụ:**
```
{
  "notificationId": "notif_001",
  "userId": "user_123",
  "actorId": "user_456",
  "type": "Like",
  "targetId": "post_789",
  "message": "user_456 thích bài viết của bạn",
  "read": false,
  "createdAt": 1704153600000
}
```

### 3. **Lớp DrawingAction** (POJO)

**Mục đích:** Lưu từng hành động vẽ (cho redo/undo)

**Các thuộc tính:**
- `actionId` (String): ID duy nhất
- `type` (String): (Brush / Eraser / Fill / Selection)
- `startX, startY` (float): Điểm bắt đầu
- `endX, endY` (float): Điểm kết thúc
- `color` (int): Màu ARGB (Alpha, Red, Green, Blue)
- `brushSize` (float): Kích thước bút
- `timestamp` (long): Thời gian

### 4. **Lớp Layer** (POJO)

**Mục đích:** Lớp vẽ (hỗ trợ vẽ nhiều lớp)

**Các thuộc tính:**
- `layerId` (String): ID duy nhất
- `artworkId` (String): ID tác phẩm
- `name` (String): Tên lớp ("Background", "Character", ...)
- `bitmap` (Bitmap): Dữ liệu hình ảnh
- `opacity` (float): Độ mờ (0.0 - 1.0)
- `visible` (boolean): Lớp có hiển thị?
- `blendMode` (int): Chế độ pha trộn
- `index` (int): Thứ tự lớp (0 = dưới cùng)

### 5. **Lớp Artwork** (POJO)

**Mục đích:** Tác phẩm vẽ được lưu lại

**Các thuộc tính:**
- `artworkId` (String): ID duy nhất
- `userId` (String): ID tác giả
- `title` (String): Tiêu đề
- `description` (String): Mô tả
- `imageBase64` (String): Ảnh final (PNG Base64)
- `layers` (List<Layer>): Danh sách layers
- `actions` (List<DrawingAction>): Lịch sử hành động
- `createdAt` (long): Thời gian tạo
- `updatedAt` (long): Lần cuối chỉnh sửa
- `width, height` (int): Kích thước canvas

---

## B. CÁC VIEWMODEL

### 1. **LoginViewModel** - Xác thực người dùng

#### `login(String email, String password)`
```java
public void login(String email, String password) {
    loginStatus.setValue("Logging in...");
    
    // Gọi Firebase Auth
    auth.signInWithEmailAndPassword(email, password)
        .addOnSuccessListener(authResult -> {
            // Đăng nhập thành công
            String userId = authResult.getUser().getUid();
            
            // Tải profile từ Firestore
            loadUserProfile(userId);
            loginStatus.setValue("Success");
        })
        .addOnFailureListener(e -> {
            // Đăng nhập thất bại
            loginStatus.setValue("Error: " + e.getMessage());
        });
}
```
**Giải thích - Firebase Auth Flow:**
1. User nhập email + password
2. Firebase xác thực với database Firebase Auth
3. Nếu đúng → trả về AuthResult
4. Lấy userID từ AuthResult
5. Tải thêm profile từ Firestore

#### `signup(String email, String password, String username)`
```java
public void signup(String email, String password, String username) {
    loginStatus.setValue("Creating account...");
    
    auth.createUserWithEmailAndPassword(email, password)
        .addOnSuccessListener(authResult -> {
            // Account tạo thành công
            String userId = authResult.getUser().getUid();
            
            // Tạo User document trong Firestore
            User user = new User();
            user.setUserId(userId);
            user.setEmail(email);
            user.setUsername(username);
            user.setCreatedAt(System.currentTimeMillis());
            user.setRole("User");
            user.setFollowers(new ArrayList<>());
            user.setFollowing(new ArrayList<>());
            user.setFollowerCount(0);
            user.setFollowingCount(0);
            
            db.collection("Users")
                .document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    loginStatus.setValue("Success");
                });
        })
        .addOnFailureListener(e -> {
            loginStatus.setValue("Error: " + e.getMessage());
        });
}
```

### 2. **ProfileViewModel** - Quản lý profile

#### `loadProfile(String userId)`
```java
public void loadProfile(String userId) {
    // Lắng nghe thay đổi realtime
    db.collection("Users")
        .document(userId)
        .addSnapshotListener((value, error) -> {
            if (value != null && value.exists()) {
                User user = value.toObject(User.class);
                userProfile.setValue(user);
                // UI tự động update khi profile thay đổi
            }
        });
}
```

#### `updateProfile(String userId, String username, String bio, String avatarBase64)`
```java
public void updateProfile(String userId, String username, String bio, String avatarBase64) {
    Map<String, Object> updates = new HashMap<>();
    updates.put("username", username);
    updates.put("bio", bio);
    if (avatarBase64 != null) {
        updates.put("avatarBase64", avatarBase64);
    }
    updates.put("updatedAt", System.currentTimeMillis());
    
    db.collection("Users")
        .document(userId)
        .update(updates)
        .addOnSuccessListener(aVoid -> {
            updateStatus.setValue("ProfileUpdated");
            // UI sẽ tự update vì có listener
        });
}
```

#### `followUser(String currentUserId, String targetUserId)`
```java
public void followUser(String currentUserId, String targetUserId) {
    // Thêm targetUserId vào following list của current user
    db.collection("Users")
        .document(currentUserId)
        .update(
            "following", FieldValue.arrayUnion(targetUserId),
            "followingCount", FieldValue.increment(1)
        );
    
    // Thêm currentUserId vào followers list của target user
    db.collection("Users")
        .document(targetUserId)
        .update(
            "followers", FieldValue.arrayUnion(currentUserId),
            "followerCount", FieldValue.increment(1)
        );
}
```
**Giải thích - arrayUnion:**
- `FieldValue.arrayUnion(id)`: Thêm ID vào mảng (nếu chưa có)
- `FieldValue.increment(1)`: Tăng số count lên 1

### 3. **NotificationViewModel** - Thông báo

#### `loadNotifications()`
```java
public void loadNotifications() {
    String userId = auth.getCurrentUser().getUid();
    
    db.collection("Users")
        .document(userId)
        .collection("Notifications")
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .addSnapshotListener((value, error) -> {
            if (value != null) {
                List<Notification> notifList = new ArrayList<>();
                for (DocumentSnapshot doc : value) {
                    Notification notif = doc.toObject(Notification.class);
                    notifList.add(notif);
                }
                notifications.setValue(notifList);
                // UI realtime update
            }
        });
}
```
**addSnapshotListener vs get:**
- `addSnapshotListener`: Realtime updates (listener, tự động update khi dữ liệu thay đổi)
- `get`: One-time fetch (chỉ lấy dữ liệu 1 lần)

### 4. **CanvasViewModel** - Lưu tác phẩm

#### `saveArtwork(String title, String description, String imageBase64, int width, int height)`
```java
public void saveArtwork(String title, String description, 
                       String imageBase64, int width, int height) {
    Artwork artwork = new Artwork();
    artwork.setArtworkId(UUID.randomUUID().toString());
    artwork.setUserId(auth.getCurrentUser().getUid());
    artwork.setTitle(title);
    artwork.setDescription(description);
    artwork.setImageBase64(imageBase64); // PNG Base64 từ Canvas
    artwork.setCreatedAt(System.currentTimeMillis());
    artwork.setUpdatedAt(System.currentTimeMillis());
    artwork.setWidth(width);
    artwork.setHeight(height);
    
    db.collection("Artworks")
        .document(artwork.getArtworkId())
        .set(artwork)
        .addOnSuccessListener(aVoid -> {
            saveStatus.setValue("ArtworkSaved");
            // Có thể publish tác phẩm này lên feed
        });
}
```

---

## C. CUSTOM VIEW: DrawingView

### Cấu trúc DrawingView

```java
public class DrawingView extends View {
    // Dữ liệu
    private Canvas canvas;              // Canvas để vẽ
    private Bitmap bitmap;              // Hình ảnh lưu trữ
    private Paint brushPaint;           // Paint cho bút vẽ
    private Paint eraserPaint;          // Paint cho tẩy
    private float brushSize = 5f;       // Kích thước bút
    private int brushColor = Color.BLACK;
    private List<DrawingPath> paths;    // Danh sách tất cả stroke
    private List<DrawingPath> undoStack; // Stack cho Undo
    private DrawingMode currentMode;    // Chế độ hiện tại
    
    public enum DrawingMode {
        BRUSH, ERASER, FILL, SELECTION
    }
}
```

### Phương thức chính

#### `onSizeChanged()` - Khởi tạo canvas
```java
@Override
protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    
    // Tạo bitmap trắng
    bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    canvas = new Canvas(bitmap); // Canvas vẽ lên bitmap
    canvas.drawColor(Color.WHITE); // Nền trắng
}
```
**Giải thích:**
- View khi được layout (thay đổi kích thước) → gọi onSizeChanged
- Tạo Bitmap đích có kích thước (w, h)
- Canvas sẽ vẽ lên Bitmap này
- ARGB_8888 = 4 bytes/pixel (Alpha, Red, Green, Blue)

#### `onDraw()` - Hiển thị hình ảnh
```java
@Override
protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    
    // Vẽ bitmap (kết quả vẽ trước)
    canvas.drawBitmap(bitmap, 0, 0, null);
    
    // Vẽ tất cả paths
    for (DrawingPath path : paths) {
        if (path.mode == DrawingMode.BRUSH) {
            canvas.drawPath(path.path, path.paint);
        } else if (path.mode == DrawingMode.ERASER) {
            canvas.drawPath(path.path, eraserPaint);
        }
    }
}
```

#### `onTouchEvent()` - Xử lý chạm tay
```java
@Override
public boolean onTouchEvent(MotionEvent event) {
    float x = event.getX();
    float y = event.getY();
    
    switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            // Bắt đầu vẽ
            startPath(x, y);
            return true;
            
        case MotionEvent.ACTION_MOVE:
            // Đang vẽ
            updatePath(x, y);
            invalidate(); // Yêu cầu redraw
            return true;
            
        case MotionEvent.ACTION_UP:
            // Kết thúc vẽ
            endPath();
            invalidate();
            return true;
    }
    
    return false;
}
```

#### `startPath(float x, float y)` - Bắt đầu stroke
```java
private void startPath(float x, float y) {
    lastX = x;
    lastY = y;
    
    // Tạo Path mới
    Path path = new Path();
    path.moveTo(x, y); // Đặt điểm bắt đầu
    
    // Chọn paint tùy chế độ
    Paint paint = currentMode == DrawingMode.BRUSH ? 
        brushPaint : eraserPaint;
    
    // Thêm vào danh sách
    DrawingPath drawingPath = new DrawingPath(path, paint, currentMode);
    paths.add(drawingPath);
}
```

#### `updatePath(float x, float y)` - Tiếp tục vẽ
```java
private void updatePath(float x, float y) {
    if (!paths.isEmpty()) {
        DrawingPath currentPath = paths.get(paths.size() - 1);
        
        // Vẽ quadratic curve từ lastPos đến (x,y)
        currentPath.path.quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2);
        
        lastX = x;
        lastY = y;
    }
}
```
**Giải thích - quadTo:**
- `quadTo(x1, y1, x2, y2)`: Vẽ Quadratic Bezier curve
- (x1, y1) = control point
- (x2, y2) = end point
- Tạo ra nét mượt hơn line thẳng

#### `endPath()` - Kết thúc stroke
```java
private void endPath() {
    if (!paths.isEmpty()) {
        DrawingPath currentPath = paths.get(paths.size() - 1);
        currentPath.path.lineTo(lastX, lastY);
        
        // Vẽ lên bitmap
        Paint paint = currentPath.mode == DrawingMode.BRUSH ? 
            currentPath.paint : eraserPaint;
        canvas.drawPath(currentPath.path, paint);
        
        // Xóa undo stack khi có stroke mới
        undoStack.clear();
    }
}
```

#### `undo()` - Quay lại lần vẽ trước
```java
public void undo() {
    if (!paths.isEmpty()) {
        // Lấy stroke cuối cùng
        DrawingPath removedPath = paths.remove(paths.size() - 1);
        
        // Thêm vào undo stack
        undoStack.add(removedPath);
        
        // Vẽ lại bitmap từ đầu (không có stroke này)
        redrawBitmap();
        
        // Yêu cầu UI update
        invalidate();
    }
}
```

#### `redo()` - Làm lại stroke đã undo
```java
public void redo() {
    if (!undoStack.isEmpty()) {
        // Lấy stroke từ undo stack
        DrawingPath restoredPath = undoStack.remove(undoStack.size() - 1);
        paths.add(restoredPath);
        
        // Vẽ stroke này lên bitmap
        Paint paint = restoredPath.mode == DrawingMode.BRUSH ? 
            restoredPath.paint : eraserPaint;
        canvas.drawPath(restoredPath.path, paint);
        
        invalidate();
    }
}
```

#### `redrawBitmap()` - Vẽ lại từ đầu
```java
private void redrawBitmap() {
    // Xóa bitmap
    canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
    canvas.drawColor(Color.WHITE);
    
    // Vẽ lại tất cả paths hiện tại
    for (DrawingPath path : paths) {
        Paint paint = path.mode == DrawingMode.BRUSH ? 
            path.paint : eraserPaint;
        canvas.drawPath(path.path, paint);
    }
}
```

#### `getDrawingAsBase64()` - Export ảnh
```java
public String getDrawingAsBase64() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    
    // Nén Bitmap thành PNG
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
    
    // Convert bytes thành Base64
    byte[] imageBytes = outputStream.toByteArray();
    return Base64.encodeToString(imageBytes, Base64.DEFAULT);
}
```
**Ý nghĩa:**
- Bitmap → PNG bytes → Base64 string
- Base64 có thể lưu trực tiếp trong Firestore
- Kích thước nhỏ hơn binary gốc

---

## D. DATABASE COLLECTIONS

### 1. **Collection: Users**
```firestore
/Users/{userId}
{
  "userId": "user_123",
  "email": "user@example.com",
  "username": "John Doe",
  "avatarBase64": "data:image/jpeg;base64,...",
  "bio": "Art enthusiast & digital artist",
  "followers": ["user_456", "user_789"],
  "following": ["mentor_001", "user_555"],
  "followerCount": 245,
  "followingCount": 87,
  "role": "User",
  "createdAt": 1704067200000,
  "updatedAt": 1704153600000
}
```

### 2. **Sub-collection: Users/{userId}/Notifications**
```firestore
/Users/{userId}/Notifications/{notificationId}
{
  "notificationId": "notif_001",
  "userId": "user_123",
  "actorId": "user_456",
  "type": "Like",
  "targetId": "post_789",
  "message": "user_456 thích bài viết của bạn",
  "read": false,
  "createdAt": 1704153600000
}
```

### 3. **Collection: Artworks**
```firestore
/Artworks/{artworkId}
{
  "artworkId": "art_001",
  "userId": "user_123",
  "title": "Landscape Study",
  "description": "A beautiful landscape...",
  "imageBase64": "data:image/png;base64,...",
  "createdAt": 1704067200000,
  "updatedAt": 1704153600000,
  "width": 800,
  "height": 600
}
```

---

## E. EXTERNAL SERVICES

### 1. **Firebase Authentication**

**Login:**
```java
auth.signInWithEmailAndPassword(email, password)
    .addOnSuccessListener(authResult -> {
        // User đăng nhập thành công
        FirebaseUser user = authResult.getUser();
        String uid = user.getUid();
    });
```

**Signup:**
```java
auth.createUserWithEmailAndPassword(email, password)
    .addOnSuccessListener(authResult -> {
        // Account tạo thành công
        String uid = authResult.getUser().getUid();
    });
```

### 2. **Firebase Cloud Functions** (Notifications)

**Function:** `onNotificationTrigger()`
```javascript
exports.onLikePost = functions.firestore
    .document('Likes/{likeId}')
    .onCreate(async (snap, context) => {
        const like = snap.data();
        const postId = like.targetId;
        
        // Tạo notification
        const notification = {
            notificationId: uuid(),
            userId: postData.userId, // Tác giả post
            actorId: like.userId,    // Người like
            type: 'Like',
            targetId: postId,
            message: `${actorName} đã thích bài viết của bạn`,
            read: false,
            createdAt: Date.now()
        };
        
        // Lưu notification
        await db.collection('Users')
            .document(postData.userId)
            .collection('Notifications')
            .add(notification);
        
        // Gửi push notification
        await admin.messaging().sendToDevice(userToken, {
            notification: {
                title: 'Bạn nhận được lượt thích',
                body: message
            }
        });
    });
```

---

# TỔNG KẾT

## Kiến trúc MVVM Chung

```
Tất cả 4 Module đều sử dụng kiến trúc MVVM:

┌──────────────────┐
│  UI Layer        │ ← Fragment/Activity hiển thị
├──────────────────┤
│  ViewModel       │ ← Xử lý logic, hold LiveData
├──────────────────┤
│  Repository      │ ← Truy vấn dữ liệu
├──────────────────┤
│  Firestore/API   │ ← Backend
└──────────────────┘
```

## Firestore Collections Mapping

| Collection | Module | Mục đích |
|---|---|---|
| Users | Core | Thông tin người dùng |
| Lessons | Linh | Bài học |
| Submissions | Linh | Bài nộp bài học |
| ChatbotFeedbacks | Linh | Phản hồi AI |
| Artworks | Vinh | Tác phẩm vẽ |
| Posts | Mạnh | Bài viết feed |
| Livestreams | Mạnh | Phát trực tiếp |
| Challenges | Vân | Thử thách |
| ChallengeSubmissions | Vân | Bài nộp thử thách |
| Events | Vân | Sự kiện |
| Projects | Vân | Dự án người dùng |

