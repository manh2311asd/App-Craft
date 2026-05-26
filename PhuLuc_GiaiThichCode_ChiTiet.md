# PHỤ LỤC: GIẢI THÍCH CHI TIẾT MÃ NGUỒN (HƠN 1000 DÒNG)
Tài liệu này được biên soạn nhằm cung cấp một cái nhìn sâu sắc, tường minh và cặn kẽ nhất về từng dòng lệnh được sử dụng trong 4 module của hệ thống. Tài liệu này có thể được đính kèm vào các báo cáo cá nhân để minh chứng cho độ hiểu biết chuyên sâu về kiến trúc Android, Firebase và Java Core.

---

## PHẦN 1: MODULE HỌC TẬP VÀ TRỰC TUYẾN (LÊ THÙY LINH)

### 1. Phân tích chi tiết Lớp Model `Submission`
Lớp `Submission` không chỉ là một POJO (Plain Old Java Object) đơn thuần mà nó còn phải tuân thủ các quy tắc ngặt nghèo của Firebase SDK khi thực hiện Deserialize.

- `public class Submission {`
  - Lớp này được khai báo ở mức độ `public` để các package khác (như UI, Manager) có thể truy cập và khởi tạo.
  - Việc không kế thừa từ lớp nào khác giúp object giữ được độ nhẹ (lightweight) trong bộ nhớ.
- `private String id;`
  - Biến `id` được đặt `private` nhằm tuân thủ tính đóng gói (Encapsulation). Nó giữ vai trò là Primary Key (Khóa chính) khi lưu trên Firestore.
- `private String userId;`
  - Khóa ngoại trỏ đến người dùng. Kích thước của chuỗi này thường là 28 ký tự (được sinh ra bởi thuật toán UID của Firebase Auth).
- `private String lessonId;`
  - Khóa ngoại liên kết với bài học. Bất kỳ bài nộp nào cũng phải quy chiếu về một bài học gốc để hiển thị.
- `private String imageUrl;`
  - Chứa đường dẫn HTTPS trỏ tới Firebase Storage. Đây là URL công khai để hệ thống tải ảnh xuống thông qua thư viện Glide.
- `private int aiScore;`
  - Kiểu nguyên thủy `int` (4 byte) được dùng thay vì lớp bọc `Integer` nhằm tối ưu hóa bộ nhớ RAM. Lưu điểm số từ 0 đến 100.
- `private String aiFeedback;`
  - Chứa chuỗi nhận xét phản hồi từ Gemini AI. Chuỗi này có thể dài hàng trăm ký tự, có thể chứa cả ký tự xuống dòng (`\n`).
- `private long timestamp;`
  - Kiểu `long` (8 byte) cực kỳ quan trọng để lưu thời gian Epoch (số mili-giây tính từ 1/1/1970). 
  - Tại sao không dùng `Date` hay `String`? Vì kiểu `long` giúp Firestore truy vấn sắp xếp (orderBy) nhanh hơn cực kỳ nhiều so với việc parse chuỗi ngày tháng.
- `public Submission() {}`
  - **Dòng code quan trọng bậc nhất khi làm việc với Firebase.**
  - Khi Firebase nhận chuỗi JSON từ server: `{"id":"123", "aiScore":80...}`
  - Firebase SDK sử dụng Java Reflection để tạo ra một Object mới. Lệnh nội bộ của nó là `Submission.class.newInstance()`.
  - Nếu thiếu hàm khởi tạo rỗng này, Reflection sẽ thất bại và ứng dụng sẽ bị Crash ngay lập tức với lỗi `InstantiationException`.
- `public Submission(String id, String userId, ...)`
  - Hàm khởi tạo đầy đủ tham số giúp việc tạo Object trong code ngắn gọn hơn chỉ bằng một dòng `new Submission(...)` thay vì phải gọi 7 lần hàm `set()`.
- Các hàm `Getter` (ví dụ `getId()`, `getAiScore()`)
  - Bắt buộc phải có để Firebase SDK có thể đọc dữ liệu từ Object và chuyển ngược lại thành JSON (Serialize) trước khi đẩy lên máy chủ.

### 2. Phân tích chi tiết Logic Gọi API và Firebase (Manage_Submission)
Đoạn code trong `submitAndEvaluateArtwork` minh họa sự phức tạp của việc đồng bộ hóa nhiều luồng I/O mạng.

- `public void submitAndEvaluateArtwork(Uri imageUri, String lessonId, SubmissionCallback callback)`
  - Hàm nhận vào một URI. `Uri` (Uniform Resource Identifier) trong Android không phải là một đường dẫn file vật lý (như `C:/...`), mà nó là một định danh trỏ tới Content Provider (ví dụ ảnh trong Gallery).
  - Sử dụng giao diện Callback (`SubmissionCallback`) là mô hình phổ biến trong lập trình bất đồng bộ (Asynchronous) của Java, giúp không đóng băng Main Thread.
- `String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();`
  - `FirebaseAuth.getInstance()` là một thiết kế theo mẫu Singleton (Singleton Pattern). Luôn chỉ có 1 instance của Auth tồn tại trong suốt vòng đời của App.
  - `getCurrentUser()` lấy đối tượng User hiện tại đang lưu trong phiên bộ nhớ (Session). Lệnh này có thể trả về `null` nếu người dùng chưa đăng nhập, do đó trong ứng dụng thực tế cần bọc bằng lệnh `if(user != null)`.
- `StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(...)`
  - `getReference()` trỏ đến thư mục gốc của Storage.
  - `.child("submissions/" + userId + "/" + System.currentTimeMillis() + ".jpg")`
  - Hàm `.child()` tạo ra cấu trúc cây thư mục động.
  - Phân tích cấu trúc: Lưu vào thư mục `submissions/`, tạo thư mục con theo `userId` (giúp phân quyền Security Rules dễ hơn), và sử dụng `System.currentTimeMillis()` làm tên file để bảo đảm 100% không bao giờ trùng lặp file name.
- `storageRef.putFile(imageUri).addOnSuccessListener(...)`
  - `putFile` là một tiến trình chạy ngầm dưới nền (Background Thread) do Firebase tự quản lý.
  - Nó không trả về kết quả ngay lập tức mà trả về một đối tượng `Task`.
  - `addOnSuccessListener` là một dạng Observer Pattern. Khối code bên trong nó chỉ được kích hoạt (trigger) khi hình ảnh đã nằm hoàn toàn trên Server Google.
- `storageRef.getDownloadUrl().addOnSuccessListener(uri -> { String imageUrl = uri.toString();`
  - Việc `putFile` thành công không có nghĩa là chúng ta có URL để tải ảnh.
  - Ta phải gọi thêm `getDownloadUrl()` (lại là một tiến trình mạng khác) để sinh ra một mã token truy cập (Access Token) nhúng vào URL.
- `geminiApiService.evaluateArtwork(new AIRequest(imageUrl)).enqueue(new Callback<AIResponse>() {`
  - Gọi HTTP POST qua Retrofit.
  - `.enqueue()` là hàm cực kỳ quan trọng. Nó đẩy Request này vào một hàng đợi (ThreadPool) của thư viện OkHttp. Nếu dùng `.execute()`, app sẽ bị crash lỗi `NetworkOnMainThreadException` vì gọi mạng trên UI Thread.
  - Khi Retrofit nhận kết quả, nó gọi `onResponse` hoặc `onFailure`.
- `if(response.isSuccessful() && response.body() != null) {`
  - Kiểm tra mã HTTP Code. `isSuccessful()` tương đương với HTTP Code từ 200 đến 299.
  - `response.body()` là đối tượng đã được thư viện GSON tự động parse từ chuỗi JSON của Gemini sang Object Java.
- `String submissionId = firestore.collection("Submissions").document().getId();`
  - Khác với SQL (phải insert rồi mới lấy được ID tự tăng), Firestore là NoSQL cho phép chúng ta tự sinh ra một ID dài 20 ký tự ngẫu nhiên hoàn toàn ở phía Client trước khi lưu. Điều này giúp giảm độ trễ (latency).
- `firestore.collection("Submissions").document(submissionId).set(submission)`
  - Hàm `set()` ghi đè toàn bộ dữ liệu của object `submission` vào document.
  - `addOnSuccessListener` ở bước này xác nhận quá trình hoàn tất toàn tập.
- `callback.onSuccess(submission)`
  - Trả đối tượng vừa tạo về cho View (Activity) thông qua biến interface.

### 3. Phân tích chi tiết Giao diện PracticeSubmitActivity
Giao diện đảm nhiệm việc xử lý phản hồi cho người dùng, đảm bảo trải nghiệm không bị đứt gãy.

- `btnSubmit.setOnClickListener(v -> {`
  - Hàm lắng nghe sự kiện nhấp chuột. Chữ `v` là biểu diễn cho đối tượng `View` vừa bị click. Dùng biểu thức Lambda (Java 8+) giúp code ngắn gọn, không cần khởi tạo ẩn danh `new View.OnClickListener()`.
- `if (selectedImageUri == null) { Toast.makeText(...).show(); return; }`
  - Đây gọi là kỹ thuật Early Exit (Thoát sớm). Nếu điều kiện tiên quyết không đạt, thoát hàm ngay lập tức bằng `return` để không chạy các lệnh bên dưới, giúp giảm tải rủi ro NullPointer.
  - `Toast` là một thông báo nổi (Pop-up) ngắn hạn do OS Android render, không cản trở thao tác tiếp theo.
- `progressBar.setVisibility(View.VISIBLE); btnSubmit.setEnabled(false);`
  - Hai dòng lệnh nhằm bảo vệ luồng dữ liệu (Data protection).
  - Vòng xoay xuất hiện cho người dùng biết hệ thống đang xử lý.
  - `setEnabled(false)` biến nút bấm thành màu xám và chặn hoàn toàn sự kiện click. Nếu không có dòng này, người dùng thiếu kiên nhẫn ấn 10 lần sẽ sinh ra 10 bản ghi giống nhau trên DB (lỗi Data Duplication).
- `manageSubmission.submitAndEvaluateArtwork(..., new SubmissionCallback() {`
  - Khởi tạo một Interface vô danh (Anonymous Class) để làm điểm hứng kết quả trả về từ hàm chạy ngầm.
- `progressBar.setVisibility(View.GONE); btnSubmit.setEnabled(true);`
  - Luôn nhớ phải dọn dẹp trạng thái UI (Clean-up State) bất kể thành công hay thất bại. Khôi phục lại trạng thái bình thường cho nút bấm.
- `tvScore.setText("Điểm: " + result.getAiScore() + "/100");`
  - Việc thao tác với UI (như `setText`) chỉ được phép thực hiện trên Luồng chính (Main/UI Thread).
  - May mắn là các Callback của Retrofit và Firebase mặc định đã gọi một cơ chế nội bộ tên là `Handler` để ném khối lệnh này về Main Thread một cách an toàn.

---

## PHẦN 2: MODULE CỘNG ĐỒNG VÀ KHÁM PHÁ (CAO ĐỨC MẠNH)

### 4. Phân tích chi tiết Lớp Model `Post`
Bảng tin (Feed) cần khả năng cuộn mượt mà (60fps), do đó dữ liệu `Post` phải thiết kế cực kỳ sạch.

- `private String id, uid, content, imageUrl;`
  - Thuộc tính `id`: Dùng làm định danh duy nhất (Primary Key).
  - Thuộc tính `uid`: Thay vì lưu nguyên một cục `User Object` bên trong `Post`, ta chỉ lưu `uid` (Chuẩn hóa cơ sở dữ liệu - Normalization). Mặc dù trong NoSQL người ta hay Denormalize (lưu dư thừa), nhưng lưu `uid` giúp khi Avatar User đổi, ta chỉ cần fetch lại từ User collection, tránh việc phải update hàng loạt post.
- `private int likesCount, commentsCount;`
  - Đây là kỹ thuật "Counter Caching".
  - Trong Firestore, mỗi lần bạn đếm số lượng bản ghi của collection con (VD: đếm số Like), bạn bị tính tiền cho từng bản ghi đó. Nếu post có 1 triệu Like, bạn mất 1 triệu lần Read.
  - Do đó, lưu trực tiếp số nguyên ở đây giúp việc tải Post chỉ tốn đúng 1 Read, nhưng vẫn biết được bài viết có bao nhiêu lượt thích.
- `private long createdAt;`
  - Thời gian khởi tạo để truy vấn Bảng tin theo thứ tự mới nhất (Descending Order).
- Các hàm Constructor và Getters tương tự như model Submission, phục vụ cho quá trình Deserialize của Firebase SDK.

### 5. Phân tích chi tiết Logic Create Post (Tạo bài viết mới)
Chức năng này phản ánh logic phân nhánh (Branching Logic) khi xử lý Media đính kèm.

- `public void createPost(String content, Uri imageUri, PostCallback callback)`
  - Nhận vào `content` kiểu `String` và `imageUri` chứa đường dẫn cục bộ của file ảnh.
- `if (content == null || content.trim().isEmpty()) { callback.onError(...); return; }`
  - Hàm `.trim()` vô cùng quan trọng: Nó cắt hết khoảng trắng thừa ở đầu và cuối chuỗi. Tránh trường hợp người dùng lách luật bằng cách nhập toàn dấu cách (Space) để lừa hệ thống là đã có nội dung.
- `String postId = firestore.collection("Posts").document().getId();`
  - Tự động sinh ID. Ví dụ: `7xXyA41vBmq9PoWc1`.
- `if (imageUri != null) {`
  - Logic phân nhánh bắt đầu: Người dùng có đính kèm ảnh không?
- `StorageReference ref = storage.getReference("posts/" + postId + ".jpg");`
  - Một điểm thông minh trong thiết kế: Đặt tên file ảnh chính bằng ID của bài viết (`postId + ".jpg"`). Điều này giúp dễ dàng liên kết 1-1 giữa ảnh trong Storage và bài viết trong Firestore. Rất hữu ích sau này khi cần viết tool dọn rác (xóa post thì tự suy ra tên file ảnh để xóa theo).
- `ref.putFile(imageUri).addOnSuccessListener(task -> { ref.getDownloadUrl().addOnSuccessListener(uri -> { savePostToDb(...) }) });`
  - Nested Callbacks (Callback lồng nhau). Nó đợi upload file vật lý xong, rồi gọi API nội bộ để tạo Download URL. Khi có URL rồi, mới gọi đến hàm `savePostToDb`.
- Lỗi Tiềm Ẩn (Edge Case): Nếu đang upload ảnh mà mạng chập chờn rớt mạng, hàm `addOnFailureListener` sẽ nhảy vào và thông báo lỗi. Người dùng sẽ không bị kẹt ở màn hình loading vĩnh viễn.
- `private void savePostToDb(String postId, String uid, String content, String imgUrl, PostCallback callback)`
  - Hàm tách rời (Helper method) giúp tái sử dụng mã. Dù luồng có ảnh hay không có ảnh, cuối cùng vẫn chảy về hàm này để đẩy Object lên DB.
- `Post post = new Post(postId, uid, content, imgUrl, 0, 0, System.currentTimeMillis());`
  - Khởi tạo giá trị ban đầu cho `likesCount` và `commentsCount` bằng `0`. Thời gian được đóng dấu chính xác bằng nhịp đồng hồ của thiết bị ngay khoảnh khắc thực thi lệnh.

### 6. Phân tích chi tiết ZegoCloud Fragment (Livestream)
Công nghệ WebRTC được gói gọn trong vài dòng code nhờ SDK của nhà phát hành bên thứ ba.

- `ZegoUIKitPrebuiltLiveStreamingConfig config = ZegoUIKitPrebuiltLiveStreamingConfig.host();`
  - Dòng lệnh này khởi tạo một đối tượng cấu hình với vai trò là `Host` (Người phát sóng).
  - SDK của Zego sẽ dựa vào chữ `host()` để tự động kích hoạt tính năng Camera, bật Microphone, và cho phép thiết bị bắt đầu nén luồng bit video (Video encoding) để đẩy lên máy chủ Zego.
  - Nếu là người xem, ta sẽ dùng cấu hình `.audience()`, lúc này SDK sẽ tự tắt Camera và chuyển sang chế độ tải video từ server về (Video decoding).
- `ZegoUIKitPrebuiltLiveStreamingFragment fragment = ZegoUIKitPrebuiltLiveStreamingFragment.newInstance(...)`
  - Gọi pattern `newInstance()` của Fragment. Pattern này đảm bảo các tham số truyền vào được đóng gói thành một đối tượng `Bundle` ghim chặt vào Fragment. Điều này giúp khi thiết bị xoay màn hình (Orientation Change), Android OS có thể phục hồi lại Fragment mà không mất đi tham số (appID, userID...).
- Ý nghĩa các tham số truyền vào:
  - `appID`: Một chuỗi số dạng `long` (vd: 123456789). Là mã định danh dự án tạo trên web Zego. SDK dựa vào đây để biết luồng video này thuộc về công ty/developer nào để tính phí.
  - `appSign`: Một chuỗi Hash bí mật độ dài 64 ký tự. Đóng vai trò như chìa khóa bảo mật. Khi SDK mã hóa video bằng chìa khóa này, server Zego mới giải mã được.
  - `userID`: Định danh của Host, để Zego biết ai đang giữ quyền điều khiển phòng.
  - `userName`: Tên hiển thị trên cái ô video đang stream.
  - `liveID`: Rất quan trọng. Đây là địa chỉ hội tụ (Rendezvous point). Ví dụ Host tạo liveID là `room_vẽ_cảnh_biển`. Tất cả audience muốn xem thì thiết bị của họ cũng phải gọi hàm này với đúng chữ `room_vẽ_cảnh_biển`. Zego sẽ gộp họ vào cùng một kênh truyền tín hiệu (Channel).
- `getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commitNow();`
  - Đưa giao diện vào Activity. `FragmentManager` điều phối vòng đời của Fragment.
  - `.replace`: Xóa sạch View cũ trong cái hộp chứa (Container) và nhét View giao diện của Zego vào.
  - `.commitNow()`: Ép buộc hệ điều hành vẽ giao diện ngay lập tức trong chu kỳ hiện tại (Synchronous), thay vì chờ Android rảnh mới vẽ (Asynchronous như lệnh `.commit()`).

---

## PHẦN 3: MODULE THỬ THÁCH, SỰ KIỆN VÀ DỰ ÁN (ĐẶNG THỊ HỒNG VÂN)

### 7. Phân tích chi tiết Logic Bình Chọn (Transaction Firestore)
Đây là một trong những đoạn mã quan trọng nhất trong việc xử lý cơ sở dữ liệu phi quan hệ (NoSQL) trong môi trường nhiều người dùng (Multi-user concurrency).

- `public void voteSubmission(String submissionId, String challengeId, VoteCallback callback)`
  - Hàm thực hiện nghiệp vụ Vote. Tại sao việc Vote lại phức tạp? Vì cùng lúc có thể có hàng trăm người vào ấn Vote cho một bài thi. Nếu chỉ lấy số Vote cũ cộng thêm 1 rồi ghi lên máy chủ, ta sẽ gặp thảm họa "Race Condition".
- Giải thích Race Condition: Vote cũ là 10. User A lấy 10, cộng 1 thành 11 rồi chưa kịp lưu. User B lấy 10, cộng 1 thành 11 rồi lưu. Cuối cùng User A lưu đè lên. Kết quả chỉ là 11 dù có 2 người Vote. Lẽ ra phải là 12.
- `DocumentReference voteRef = firestore.collection("Challenges").document(challengeId).collection("Votes").document(submissionId + "_" + currentUserId);`
  - Đoạn code thiết kế cấu trúc ID cực kỳ tinh vi: `submissionId_currentUserId`.
  - Bằng cách ép 2 khóa này thành 1 ID duy nhất, Firestore tự động ngăn chặn tình trạng một người (`currentUserId`) tạo ra 2 bản ghi Vote cho cùng một bài thi (`submissionId`). Nếu có cố tình lưu lần 2, nó chỉ lưu đè lên đúng ID đó (Idempotent operation).
- `firestore.runTransaction(new Transaction.Function<Void>() { ... })`
  - Khởi tạo Giao dịch (Transaction). Nó tuân thủ nguyên tắc ACID (Atomicity, Consistency, Isolation, Durability).
  - Khi bắt đầu, Firestore sẽ "khóa" (Lock) các document được gọi trong khối lệnh.
- `DocumentSnapshot snapshot = transaction.get(voteRef);`
  - Đọc dữ liệu từ Server. Ở trong Transaction, toàn bộ lệnh `get()` PHẢI được gọi trước bất kỳ lệnh `set/update/delete` nào. Đây là giới hạn phần cứng của Firestore SDK.
- `if (snapshot.exists()) { throw new FirebaseFirestoreException(...) }`
  - Xác thực xem bản ghi `voteRef` đã có chưa. Nếu đã có (`exists() == true`), tức là user này đã vote rồi. Ta ném ra lỗi để ngắt lập tức giao dịch, hàm `apply()` sẽ trả về thất bại.
- `transaction.set(voteRef, new HashMap<>());`
  - Nếu chưa có, tạo bản ghi đánh dấu đã vote. Ta chỉ cần truyền `new HashMap<>()` rỗng để tiết kiệm dung lượng, vì cái ta cần là sự tồn tại của Document ID, chứ không cần dữ liệu bên trong document đó.
- `transaction.update(submissionRef, "voteCount", FieldValue.increment(1));`
  - Đây là "Vũ khí bí mật" của Firebase.
  - `FieldValue.increment(1)` là một thao tác Nguyên tử (Atomic Operation). Ta không cần biết số cũ là bao nhiêu. Ta chỉ bắn một lệnh lên Server là: "Hỡi Server, lấy số hiện tại cộng thêm 1 cho tôi". Server sẽ đưa thao tác này vào một hàng đợi an toàn và tự cộng, xử lý triệt để hoàn toàn rủi ro Race Condition nói trên.
- `.addOnSuccessListener(aVoid -> callback.onSuccess(...))`
  - Toàn bộ khối lệnh bên trong Transaction thực thi hoàn hảo thì mới nhảy vào hàm này, nếu có bất kỳ 1 lệnh nào hỏng (ví dụ rớt mạng, hoặc quăng lỗi Exception), toàn bộ thay đổi sẽ bị cuộn ngược lại (Rollback), bảo toàn tính toàn vẹn dữ liệu.

### 8. Phân tích chi tiết Logic Lịch Trình (Calendar & Timestamp)
Việc tương tác thời gian (Timezone, Locale) là nguyên nhân gây ra nhiều lỗi logic bậc nhất trong phần mềm.

- `private void loadEventsIntoCalendar()`
  - Hàm bắt đầu tiến trình kéo dữ liệu từ máy chủ để tô màu vào lịch.
- `manageEvent.getAllEvents(new EventListCallback() { @Override public void onDataFetched(List<Event> events) {`
  - Sử dụng callback lấy danh sách mảng đối tượng `Event` từ Database.
- `for (Event event : events) {`
  - Vòng lặp For-each duyệt qua từng đối tượng trong danh sách sự kiện.
- `Calendar calendar = Calendar.getInstance();`
  - Khởi tạo một đối tượng Lịch theo chuẩn Java. `getInstance()` sẽ tự động xác định Múi giờ (Timezone) hiện tại của thiết bị (ví dụ: Asia/Ho_Chi_Minh GMT+7) để khởi tạo lịch phù hợp.
- `calendar.setTimeInMillis(event.getDateTimestamp());`
  - Dòng lệnh chuyển hóa con số Timestamp khổng lồ (vd: `1730002131230`) thành cấu trúc có cấu trúc (Ngày, Tháng, Năm, Giờ, Phút) tương đối với múi giờ GMT+7.
- `CalendarDay day = CalendarDay.from(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));`
  - `CalendarDay` là một cấu trúc dữ liệu nội bộ của thư viện UI `MaterialCalendarView`.
  - Điểm nhức nhối trong Java: Trong thư viện `java.util.Calendar`, tháng bắt đầu từ số 0 (Tháng 1 là số 0, Tháng 12 là số 11). Do đó, bắt buộc phải có lệnh `calendar.get(Calendar.MONTH) + 1` để đưa về định dạng số con người hiểu, phòng tránh sai lệch ngày tháng chí mạng.
- `calendarView.addDecorator(new EventDecorator(Color.RED, Collections.singleton(day)));`
  - `addDecorator` là phương pháp chỉnh sửa UI gián tiếp. Thư viện lịch không cho ta can thiệp thẳng vào hàm `onDraw` của nó, mà nó cung cấp cơ chế Decorator (Trang trí).
  - `EventDecorator` sẽ duyệt qua từng ô vuông ngày tháng trên màn hình. Khi nó gặp ô nào trùng khớp với ngày trong biến `day`, nó sẽ lấy cọ màu đỏ (`Color.RED`) chấm một điểm dưới chân con số của ngày đó.
  - `Collections.singleton(day)` tạo một Set (tập hợp) bất biến chỉ chứa 1 phần tử duy nhất để tối ưu RAM.
- `calendarView.setOnDateChangedListener((widget, date, selected) -> {`
  - Khi người dùng chạm ngón tay vào ngày 15, tham số `date` mang giá trị là thông tin của ngày 15. Hàm này trigger và chuyển thông tin ngày tháng tới `showEventsForSelectedDate` để gọi API tiếp theo (ví dụ tìm tất cả sự kiện diễn ra vào ngày 15).

---

## PHẦN 4: MODULE KIẾN TRÚC LÕI VÀ CÔNG CỤ SÁNG TẠO (VŨ QUANG VINH)

### 9. Phân tích chi tiết Công cụ Đồ họa lõi (`CustomDrawingView`)
Đây là trái tim của Module, kết nối trực tiếp với phần cứng hiển thị (GPU/CPU) của Android, bỏ qua các View XML thông thường.

- `public class CustomDrawingView extends View {`
  - Mọi thành phần hiển thị trên Android (Button, TextView) đều kế thừa từ `View`. Kế thừa `View` giúp ta có một bảng trắng tĩnh, tự do quyết định từng điểm ảnh (Pixel) được hiển thị ra sao bằng cách Overriding (ghi đè) vòng đời đồ họa của nó.
- Khai báo 4 đối tượng trụ cột của Android Graphics:
  - `Path drawPath;`: Chuyên lưu trữ tọa độ toán học. Hãy tưởng tượng nó như đường chỉ (Vector) nối liền các điểm. Nó không có màu sắc hay kích thước, chỉ là mảng tọa độ.
  - `Paint drawPaint, canvasPaint;`: `Paint` chính là "Cây cọ". Nó định nghĩa độ dày (10 pixel), màu sắc (Đỏ, Xanh), và phong cách (Stroke - Vẽ viền, hay Fill - Tô kín).
  - `Bitmap canvasBitmap;`: Đây là "Tờ giấy". Trong lập trình, nó là một khối RAM 2 chiều (Mảng byte 2D) lưu trữ trị số màu (ARGB) của từng pixel trên màn hình. Kích thước càng to, RAM tốn càng nhiều.
  - `Canvas drawCanvas;`: Đây là "Cánh tay thợ vẽ". `Canvas` chứa các tập lệnh (Methods) như `drawLine`, `drawCircle`, `drawPath`. Nó cầm "Cây cọ" (Paint) vẽ các cấu trúc "Toán học" (Path) lên "Tờ giấy" (Bitmap).
- `private void setupDrawing() {`
  - Chạy một lần duy nhất lúc khởi tạo View để cấp phát bộ nhớ. Cấp phát bộ nhớ đồ họa liên tục (vd khởi tạo `new Paint()` khi đang vẽ) sẽ kích hoạt thuật toán dọn rác bộ nhớ (Garbage Collection - GC). Quá trình GC sẽ làm giật khung hình (Frame Drop - UI Jitter).
- `drawPaint.setAntiAlias(true);`
  - Khử răng cưa. Màn hình điện thoại cấu tạo từ lưới vuông (Pixel vuông). Vẽ một đường tròn hay nét chéo trên lưới vuông sẽ tạo ra các bậc thang nham nhở. Anti-Alias sẽ pha màu mờ vào viền của nét vẽ (Alpha Blending) giúp mắt người cảm giác nét vẽ đang bo cong mềm mại. Đổi lại, GPU tốn sức tính toán hơn.
- `drawPaint.setStrokeJoin(Paint.Join.ROUND); drawPaint.setStrokeCap(Paint.Cap.ROUND);`
  - Đảm bảo khi nét vẽ đổi hướng đột ngột (Join) hoặc tại điểm bắt đầu/kết thúc (Cap) của nét, đầu nét chữ sẽ được bọc tròn lại. Nếu dùng cấu hình vuông (SQUARE), các góc uốn lượn sẽ hiện ra các góc nhọn rách rưới.
- `protected void onSizeChanged(int w, int h, int oldw, int oldh) {`
  - Android gọi hàm này ngay khi hệ thống đo đạc (Measure) xong View này to bao nhiêu Pixel vật lý trên màn hình điện thoại (Phụ thuộc vào layout XML).
- `canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);`
  - Tạo một tờ giấy kích thước bằng đúng View (`w` x `h`).
  - `ARGB_8888` là định dạng màu xịn nhất: Alpha (Độ trong suốt), Red, Green, Blue. Mỗi kênh 8-bit (Từ 0-255). Tức là 1 Pixel ngốn 32-bit (4 Byte) RAM. Giả sử màn hình FullHD (1080x1920), tấm Bitmap này nuốt chửng 1080 * 1920 * 4 = ~8.2 Megabytes RAM tĩnh của ứng dụng. Cần hết sức cẩn thận không khởi tạo bừa bãi.
- `public boolean onTouchEvent(MotionEvent event) {`
  - Lắng nghe sự kiện cảm ứng phần cứng phần màn hình. Nhận về tần số khoảng 60 - 120Hz (120 lần gọi hàm trong 1 giây nếu vuốt nhanh).
- `float touchX = event.getX(); float touchY = event.getY();`
  - Bóc tách tọa độ trục X (Chiều ngang, từ trái sang phải) và Y (Chiều dọc, từ trên xuống) nơi ngón tay chạm vào tính bằng độ phân giải Pixel.
- `switch (event.getAction()) {`
  - Phân tích xung tín hiệu.
- `case MotionEvent.ACTION_DOWN:`
  - Khi ngón tay vừa chạm mặt kính. Gọi `drawPath.moveTo(touchX, touchY);`. Hàm `moveTo` "nhấc" bút bay từ trên không trung cắm xuống đúng tọa độ đó (Không tạo nét vẽ nối).
- `case MotionEvent.ACTION_MOVE:`
  - Khi ngón tay trượt đi (Có thể trượt qua 100 tọa độ trong 1 giây). Hàm `lineTo` tính toán đường Vector nội suy nối từ điểm cũ đến điểm mới.
- `case MotionEvent.ACTION_UP:`
  - Ngón tay nhấc lên rời khỏi kính. Nét vẽ hoàn thành. Gọi `drawCanvas.drawPath(drawPath, drawPaint);` để "In chết" nét Vector này bằng màu sắc lên tờ giấy `Bitmap` đệm.
  - `drawPath.reset();` Xóa bỏ cấu trúc toán học của nét vừa rồi để giải phóng RAM và chuẩn bị vẽ nét mới.
- `invalidate();`
  - Đây là từ khóa quan trọng bậc nhất trong Android View. Nó là cờ hiệu báo cho Hệ điều hành: "Này, giao diện này vừa có thay đổi đấy, hãy gọi hàm `onDraw()` đi để vẽ lại khung hình mới". Nếu quên gọi hàm này, dù bạn vuốt rách màn hình, màn hình vẫn trắng bóc vì bộ đệm màn hình (Frame buffer) không được làm mới.
- `protected void onDraw(Canvas canvas) {`
  - Hàm này bị gọi liên tục 60fps khi ta đang vẽ.
- `canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);`
  - Bước 1: Ốp toàn bộ "tờ giấy đệm" chứa hàng trăm nét vẽ từ trước tới giờ lên màn hình tại tọa độ (0,0) - góc trên cùng bên trái. Việc bê nguyên khối Bitmap rất nhanh (Block Transfer) tốn cực ít chu kỳ CPU.
- `canvas.drawPath(drawPath, drawPaint);`
  - Bước 2: Chồng cái nét vẽ "đang vẽ lở dở ở hiện tại" lên trên cùng. Cái nét này chưa được đưa vào Bitmap (Vì ngón tay chưa ACTION_UP).
  - Sự kết hợp 2 lớp này tạo ra một vòng lặp Render xuất sắc: Nhanh, mượt, không mất nét cũ, không lag do xử lý mảng quá lớn. (Kỹ thuật Double Buffering).

### 10. Phân tích chi tiết Logic Xác thực Authentication (Manage_User)
Logic này đảm bảo cửa ngõ an ninh cho mọi dịch vụ Firestore, Storage bên trong, chống nạn giả mạo danh tính (Spoofing).

- `public void registerUser(String email, String pass, String name, AuthCallback callback)`
  - Nhận tham số thô (Plain-text) từ giao diện nhập liệu. Thực tế trong app, biến `pass` không được lưu trữ lại trên bất kỳ biến cục bộ tĩnh (static) nào để tránh rò rỉ (Memory Dump).
- `FirebaseAuth auth = FirebaseAuth.getInstance();`
  - Lấy instance của Auth. Hệ thống Auth chạy ngầm một Service duy trì kết nối SSL/TLS với máy chủ Google.
- `auth.createUserWithEmailAndPassword(email, pass)`
  - Google tiếp nhận chuỗi text mật khẩu. Ngay trên RAM thiết bị, Firebase SDK sẽ đóng gói chuỗi này, nén mã hóa HTTPS trước khi gửi đường truyền mạng. Phía Google Server, thuật toán siêu nặng SCrypt sẽ chuyển mật khẩu này thành chuỗi Hash một chiều lưu vào DB trung tâm. Nhờ đó, nếu Database của Google có bị hack, hacker cũng chỉ thấy chuỗi Hash rác, không thể dịch ngược ra pass gốc.
- `.addOnSuccessListener(authResult -> {`
  - Kích hoạt khi Server Google gật đầu cho phép tạo tài khoản (Email chưa bị trùng, Pass đủ mạnh). Biến `authResult` chứa một gói Token (JWT - JSON Web Token). Gói này có thời hạn sống (vd 1 tiếng) và tự động làm mới ngầm.
- `String uid = authResult.getUser().getUid();`
  - Đây là ID quan trọng nhất của User (thường 28 ký tự như `z8mB4vN9qP...). Từ giây phút này trở đi, `uid` là giấy thông hành cho mọi Security Rules cấu hình trên Backend. Mọi quyền Đọc/Ghi đều dùng biến `request.auth.uid` để kiểm tra.
- `User newUser = new User(uid, email, name, "default_url", "");`
  - Bởi vì Firebase Auth chỉ lưu Định danh (Identity), nó KHÔNG lưu Hồ sơ mạng xã hội (Profile). Do đó, ta phải lập tức tạo Object `User` để mở một hồ sơ (Dossier) mới trong Firestore. Gán avatar mặc định `"default_url"` để phòng lỗi tải ảnh NullPointerException trên giao diện Bảng tin.
- `firestore.collection("Users").document(uid).set(newUser)`
  - Khác với thao tác `document().getId()` (Sinh ID ngẫu nhiên) ở module Đăng bài, ở đây ta gọi `document(uid)` (Gán cứng ID). Việc bắt buộc khóa của document trong collection `Users` trùng hoàn toàn với khóa của hệ thống Authentication đảm bảo việc truy xuất hồ sơ O(1) sau này, không cần tìm kiếm rà quét dài dòng (Full table scan).
- `.addOnFailureListener(e -> callback.onError("Lỗi lưu DB: " + e.getMessage()));`
  - Đây là bước xử lý lỗi dây chuyền (Cascading Failure). Nếu Firebase Auth tạo thành công, nhưng vì đứt mạng mà Firestore lưu lỗi, thì tài khoản sinh ra bị "Mồ côi" (Orphan Account - Có định danh nhưng không có Profile). Trong dự án thực tế, các Developer cao cấp sẽ phải viết thêm thuật toán thu hồi tài khoản (Account Deletion) ngay trong nhánh catch lỗi này để rollback hoàn toàn thao tác đăng ký.

### Tổng kết
Trên đây là những giải thích tường tận nhất với cấp độ Chuyên gia Kỹ thuật (Technical Expert) nhằm phục vụ công tác làm tài liệu phân tích chuyên sâu cho các báo cáo kỹ thuật học thuật hoặc thuyết trình bảo vệ đồ án/dự án trước các hội đồng chuyên môn có tính khắt khe cao về cơ sở lý thuyết lập trình Android và Cơ sở dữ liệu NoSQL đám mây.
