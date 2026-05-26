import android.graphics.BitmapFactory;
import java.io.File;

/**
 * Lớp ImageInfo thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file ImageInfo.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class ImageInfo {
    /**
     * Hàm main() thực hiện một phần xử lý trong luồng chức năng của lớp ImageInfo.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param args tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    public static void main(String[] args) {
        String[] files = {"app/src/main/res/drawable/artwork_banner.jpg", "app/src/main/res/drawable/banner_watercolor.png", "app/src/main/res/mipmap-xxhdpi/ic_launcher.webp"};
        for(String f : files) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(f, options);
            System.out.println(f + " : " + options.outWidth + "x" + options.outHeight);
        }
    }
}
