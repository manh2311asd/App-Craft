# App-Draw

Ứng dụng mạng xã hội dành cho người yêu thích vẽ tranh, chia sẻ tác phẩm, tham gia sự kiện và thử thách vẽ.

## Cấu trúc thư mục dự án (Project Structure)

Dự án này là một ứng dụng Android sử dụng Java. Dưới đây là cấu trúc thư mục chính của dự án để hỗ trợ AI (và các lập trình viên) định hướng và tìm kiếm file nhanh chóng:

```text
App-Draw/
├── app/
│   ├── build.gradle.kts       # File cấu hình Gradle cho module app
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml  # File khai báo các Activity, quyền (permissions) của ứng dụng
│           ├── java/com/example/appdraw/
│           │   ├── auth/            # [Vũ Quang Vinh] Chứa các Activity/Class liên quan đến xác thực (Đăng nhập, Đăng ký, Quên mật khẩu)
│           │   ├── challenge/       # [Cao Đức Mạnh & Đặng Thị Hồng Vân] Chứa các tính năng liên quan đến Thử thách (Danh sách, Chấm điểm, Tạo thử thách)
│           │   ├── community/       # [Cao Đức Mạnh] Chứa các tính năng Cộng đồng (Bài viết, Bình luận)
│           │   ├── drawing/         # [Vũ Quang Vinh] Chứa logic cho màn hình Vẽ (ZoomDrawingView, công cụ vẽ)
│           │   ├── event/           # [Đặng Thị Hồng Vân] Chứa các tính năng Sự kiện (Đăng ký sự kiện, Mua vé)
│           │   ├── explore/         # [Lê Thùy Linh] Chứa tính năng Khám phá (Tìm kiếm, Khám phá, Bài học vẽ)
│           │   ├── live/            # [Đặng Thị Hồng Vân] Chứa tính năng Livestream / Vẽ trực tiếp
│           │   ├── main/            # Chứa các thành phần giao diện chính (Trang chủ, Điều hướng)
│           │   ├── model/           # Chứa các lớp Data Models (Ví dụ: User, Post, Comment, Project...)
│           │   ├── project/         # [Vũ Quang Vinh] Chứa các tính năng Quản lý Dự án vẽ của người dùng (Tạo mới, Chi tiết dự án)
│           │   ├── utils/           # Chứa các lớp tiện ích (Helpers, Constants, Utils)
│           │   └── ...              # Các Activity độc lập khác như MainActivity, ProfileActivity, SplashActivity...
│           └── res/
│               ├── drawable/        # Chứa các icon, hình ảnh, file vector và shape (XML)
│               ├── layout/          # Chứa các file giao diện (XML) của các Activity/Fragment/Item
│               ├── menu/            # Chứa các file menu (Navigation, Options)
│               ├── mipmap/          # Chứa các icon launcher của ứng dụng
│               └── values/          # Chứa các file cấu hình giá trị (colors.xml, strings.xml, themes.xml...)
├── build.gradle.kts           # File cấu hình Gradle cấp Project
├── settings.gradle.kts        # File cấu hình quản lý các module trong project
├── BaoCao_BTL_AppDraw.md      # Báo cáo chi tiết của dự án (kiến trúc, thiết kế, Use case, DB)
└── README.md                  # File giới thiệu và hướng dẫn cấu trúc dự án (File này)
```

## Các file cấu hình và tài liệu quan trọng
- **`BaoCao_BTL_AppDraw.md`**: File chứa thông tin thiết kế phần mềm, sơ đồ Use Case, sơ đồ Activity, sơ đồ Class, và kiến trúc cơ sở dữ liệu (Firebase Firestore). AI nên tham khảo file này khi cần hiểu logic nghiệp vụ và kiến trúc hệ thống.
- **`app/src/main/AndroidManifest.xml`**: Xem file này để biết danh sách các Activity đã được đăng ký và các quyền ứng dụng yêu cầu.

## Công nghệ sử dụng
- **Ngôn ngữ**: Java (Android SDK)
- **Cơ sở dữ liệu**: Firebase (Firestore, Authentication, Storage)
- **Build Tool**: Gradle (Kotlin DSL `build.gradle.kts`)

## Hướng dẫn cho AI
Khi thực hiện yêu cầu liên quan đến một tính năng cụ thể:
1. Xác định package tương ứng trong `app/src/main/java/com/example/appdraw/`.
2. Kiểm tra layout tương ứng trong `app/src/main/res/layout/`.
3. Nếu cần thao tác dữ liệu, kiểm tra package `model/` để lấy cấu trúc dữ liệu và xem lại báo cáo `BaoCao_BTL_AppDraw.md`.
