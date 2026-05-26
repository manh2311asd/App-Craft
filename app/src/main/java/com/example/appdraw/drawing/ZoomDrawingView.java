package com.example.appdraw.drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Custom View xử lý Canvas vẽ (UC-10).
 * Người thực hiện: Vũ Quang Vinh.
 * Đảm nhiệm logic vẽ đường nét, hình khối, tẩy, zoom in/out và trích xuất ảnh Base64.
 */
/**
 * Lớp ZoomDrawingView thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file ZoomDrawingView.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class ZoomDrawingView extends View {

    public enum BrushType { PENCIL, WATERCOLOR, CRAYON, INK, MARKER, AIRBRUSH, CALLIGRAPHY, OIL }
    public enum Hardness { SOFT, MEDIUM, HARD }

    public static class Stroke {
        /**
         * Biến `path` lưu dữ liệu/trạng thái quan trọng kiểu Path, được sử dụng trong các bước xử lý và hiển thị của lớp.
         */
        public final Path path;
        /**
         * Biến `paint` lưu dữ liệu/trạng thái quan trọng kiểu Paint, được sử dụng trong các bước xử lý và hiển thị của lớp.
         */
        public final Paint paint;
        /**
         * Biến `fillBitmap` lưu dữ liệu dạng Map theo cặp khóa - giá trị, dùng để gom dữ liệu từ Firebase/API hoặc truyền dữ liệu lên màn hình.
         */
        public Bitmap fillBitmap;
        /**
         * Biến `isClearMarker` lưu dữ liệu/trạng thái quan trọng kiểu boolean, được sử dụng trong các bước xử lý và hiển thị của lớp.
         */
        public boolean isClearMarker = false;

        /**
         * Hàm Stroke() thực hiện một phần xử lý trong luồng chức năng của lớp ZoomDrawingView.
         * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
         * @param path tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         * @param paint tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         */
        public Stroke(Path path, Paint paint) {
            this.path = path;
            this.paint = paint;
        }
        
        /**
         * Hàm Stroke() thực hiện một phần xử lý trong luồng chức năng của lớp ZoomDrawingView.
         * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
         * @param fillBitmap tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         */
        public Stroke(Bitmap fillBitmap) {
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            this.path = null;
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            this.paint = null;
            this.fillBitmap = fillBitmap;
        }

        /**
         * Hàm Stroke() thực hiện một phần xử lý trong luồng chức năng của lớp ZoomDrawingView.
         * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
         * @param isClearMarker tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         */
        public Stroke(boolean isClearMarker) {
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            this.path = null;
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            this.paint = null;
            this.isClearMarker = isClearMarker;
        }
    }

    public static class Layer {
        /**
         * Biến `name` lưu dữ liệu/trạng thái quan trọng kiểu String, được sử dụng trong các bước xử lý và hiển thị của lớp.
         */
        public String name;
        // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
        public final List<Stroke> strokes = new ArrayList<>();
        public final Deque<Stroke> undone = new ArrayDeque<>();
        /**
         * Biến `isVisible` lưu dữ liệu/trạng thái quan trọng kiểu boolean, được sử dụng trong các bước xử lý và hiển thị của lớp.
         */
        public boolean isVisible = true;
        /**
         * Biến `isLocked` lưu dữ liệu/trạng thái quan trọng kiểu boolean, được sử dụng trong các bước xử lý và hiển thị của lớp.
         */
        public boolean isLocked = false;

        /**
         * Hàm Layer() thực hiện một phần xử lý trong luồng chức năng của lớp ZoomDrawingView.
         * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
         * @param name tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         */
        public Layer(String name) {
            this.name = name;
        }
    }

    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    private final List<Layer> layers = new ArrayList<>();
    /**
     * Biến `activeLayerIndex` lưu dữ liệu/trạng thái quan trọng kiểu int, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private int activeLayerIndex = 0;

    /**
     * Biến `brushColor` lưu dữ liệu/trạng thái quan trọng kiểu int, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private int brushColor = Color.BLACK;
    /**
     * Biến `brushSizePx` lưu dữ liệu/trạng thái quan trọng kiểu float, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private float brushSizePx = 10f;
    /**
     * Biến `brushAlpha` lưu dữ liệu/trạng thái quan trọng kiểu int, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private int brushAlpha = 255; 
    /**
     * Biến `brushType` lưu dữ liệu/trạng thái quan trọng kiểu BrushType, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private BrushType brushType = BrushType.INK;
    /**
     * Biến `hardness` lưu dữ liệu/trạng thái quan trọng kiểu Hardness, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private Hardness hardness = Hardness.MEDIUM;
    /**
     * Biến `eraserMode` lưu dữ liệu/trạng thái quan trọng kiểu boolean, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private boolean eraserMode = false;
    /**
     * Biến `fillMode` lưu dữ liệu/trạng thái quan trọng kiểu boolean, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private boolean fillMode = false;
    /**
     * Biến `canvasBgColor` lưu dữ liệu/trạng thái quan trọng kiểu int, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private int canvasBgColor = Color.WHITE;

    /**
     * Biến `currentPath` lưu dữ liệu/trạng thái quan trọng kiểu Path, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
    private Path currentPath = null;
    /**
     * Biến `currentPaint` lưu dữ liệu/trạng thái quan trọng kiểu Paint, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private Paint currentPaint;
    /**
     * Biến `bitmapPaint` lưu dữ liệu dạng Map theo cặp khóa - giá trị, dùng để gom dữ liệu từ Firebase/API hoặc truyền dữ liệu lên màn hình.
     */
    private final Paint bitmapPaint; // Paint tối ưu cho hiển thị ảnh

    /**
     * Biến `lastY` lưu dữ liệu/trạng thái quan trọng kiểu float lastX,, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private float lastX, lastY;
    /**
     * Biến `touchTolerance` lưu dữ liệu/trạng thái quan trọng kiểu float, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private final float touchTolerance = 4f;

    private final Matrix viewMatrix = new Matrix();
    private final Matrix inverseMatrix = new Matrix();

    /**
     * Biến `isScaling` lưu dữ liệu/trạng thái quan trọng kiểu boolean, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private boolean isScaling = false;
    /**
     * Biến `lastMidX` lưu mã định danh hoặc thông tin người dùng, giúp truy vấn đúng bản ghi trong Firebase và truyền dữ liệu giữa các màn hình.
     */
    private float lastMidX = 0f, lastMidY = 0f;
    
    /**
     * Biến `mScaleFactor` lưu dữ liệu/trạng thái quan trọng kiểu float, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private float mScaleFactor = 1.0f;
    private final float MIN_SCALE = 1.0f; // Thu nhỏ tối đa vừa khít khung (100%)
    /**
     * Biến `MAX_SCALE` lưu dữ liệu/trạng thái quan trọng kiểu float, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private final float MAX_SCALE = 20.0f; // Cho phép phóng to tối đa 20 lần

    /**
     * Biến `scaleDetector` lưu dữ liệu/trạng thái quan trọng kiểu ScaleGestureDetector, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private final ScaleGestureDetector scaleDetector;

    /**
     * Constructor của lớp ZoomDrawingView, dùng để khởi tạo đối tượng với các dữ liệu cần thiết trước khi sử dụng.
     * @param context tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param attrs tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    public ZoomDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        layers.add(new Layer("Nền"));
        layers.add(new Layer("Lớp 1"));
        activeLayerIndex = 1;

        currentPaint = makeBasePaint();
        
        // Cấu hình bitmapPaint để khử nhiễu tối đa
        bitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
        
        updatePaint();
        updateInverse();

        scaleDetector = new ScaleGestureDetector(context,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    /**
                     * Hàm onScaleBegin() thực hiện một phần xử lý trong luồng chức năng của lớp ZoomDrawingView.
                     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
                     * @param detector tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
                     */
                    public boolean onScaleBegin(ScaleGestureDetector detector) {
                        isScaling = true;
                        return true;
                    }

                    @Override
                    /**
                     * Hàm onScale() thực hiện một phần xử lý trong luồng chức năng của lớp ZoomDrawingView.
                     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
                     * @param detector tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
                     */
                    public boolean onScale(ScaleGestureDetector detector) {
                        float factor = detector.getScaleFactor();
                        float prevScale = mScaleFactor;
                        mScaleFactor *= factor;
                        mScaleFactor = Math.max(MIN_SCALE, Math.min(mScaleFactor, MAX_SCALE));
                        float effectiveFactor = mScaleFactor / prevScale;
                        
                        viewMatrix.postScale(effectiveFactor, effectiveFactor, detector.getFocusX(), detector.getFocusY());
                        clampMatrix();
                        updateInverse();
                        invalidate();
                        return true;
                    }

                    @Override
                    /**
                     * Hàm onScaleEnd() thực hiện một phần xử lý trong luồng chức năng của lớp ZoomDrawingView.
                     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
                     * @param detector tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
                     */
                    public void onScaleEnd(ScaleGestureDetector detector) {
                        isScaling = false;
                    }
                });
    }

    @Override
    /**
     * Hàm onDraw() thực hiện một phần xử lý trong luồng chức năng của lớp ZoomDrawingView.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param canvas tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(canvasBgColor);

        canvas.save();
        canvas.concat(viewMatrix);

        for (int i = 0; i < layers.size(); i++) {
            Layer layer = layers.get(i);
            if (layer.isVisible) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    canvas.saveLayer(null, null);
                } else {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
                }

                for (Stroke s : layer.strokes) {
                    if (s.isClearMarker) {
                        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    } else if (s.fillBitmap != null) {
                        // Luôn dùng bitmapPaint để ảnh không bị vỡ khi zoom
                        canvas.drawBitmap(s.fillBitmap, 0, 0, bitmapPaint);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    } else if (s.path != null) {
                        canvas.drawPath(s.path, s.paint);
                    }
                }
                
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (i == activeLayerIndex && currentPath != null) {
                    canvas.drawPath(currentPath, currentPaint);
                }

                canvas.restore();
            }
        }

        canvas.restore();
    }

    @Override
    /**
     * Hàm onTouchEvent() thực hiện một phần xử lý trong luồng chức năng của lớp ZoomDrawingView.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param event tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);

        if (event.getPointerCount() >= 2) {
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (currentPath != null) {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                currentPath = null;
                invalidate();
            }
            handleTwoFingerPan(event);
            return true;
        }
        
        if (isScaling) {
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (currentPath != null) {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                currentPath = null;
                invalidate();
            }
            return true;
        }

        Layer activeLayer = getActiveLayer();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (activeLayer == null || activeLayer.isLocked || !activeLayer.isVisible) return true;

        float[] mapped = mapToContent(event.getX(), event.getY());
        float x = mapped[0];
        float y = mapped[1];
        
        if (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP) {
            return true;
        }

        if (fillMode && event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            handleFloodFill((int) x, (int) y);
            return true;
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                activeLayer.undone.clear();
                currentPath = new Path();
                currentPath.moveTo(x, y);
                lastX = x;
                lastY = y;
                invalidate();
                return true;

            case MotionEvent.ACTION_MOVE:
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (currentPath != null) {
                    float dx = Math.abs(x - lastX);
                    float dy = Math.abs(y - lastY);
                    if (dx >= touchTolerance || dy >= touchTolerance) {
                        currentPath.quadTo(lastX, lastY, (x + lastX) / 2f, (y + lastY) / 2f);
                        lastX = x;
                        lastY = y;
                    }
                    invalidate();
                }
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (currentPath != null) {
                    currentPath.lineTo(x, y);
                    Paint p = new Paint(currentPaint);
                    activeLayer.strokes.add(new Stroke(currentPath, p));
                }
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                currentPath = null;
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    /**
     * Hàm handleTwoFingerPan() thực hiện một phần xử lý trong luồng chức năng của lớp ZoomDrawingView.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param event tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void handleTwoFingerPan(MotionEvent event) {
        float midX = (event.getX(0) + event.getX(1)) / 2f;
        float midY = (event.getY(0) + event.getY(1)) / 2f;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                lastMidX = midX;
                lastMidY = midY;
                break;
            case MotionEvent.ACTION_MOVE:
                if (lastMidX == 0f && lastMidY == 0f) {
                    lastMidX = midX;
                    lastMidY = midY;
                }
                float dx = midX - lastMidX;
                float dy = midY - lastMidY;
                viewMatrix.postTranslate(dx, dy);
                clampMatrix();
                lastMidX = midX;
                lastMidY = midY;
                updateInverse();
                invalidate();
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                lastMidX = 0f;
                lastMidY = 0f;
                break;
        }
    }

    /**
     * Hàm handleFloodFill() thực hiện một phần xử lý trong luồng chức năng của lớp ZoomDrawingView.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param x tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param y tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void handleFloodFill(int x, int y) {
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) return;
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Layer layer = getActiveLayer();
        for (Stroke s : layer.strokes) {
            if (s.isClearMarker) canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            else if (s.fillBitmap != null) canvas.drawBitmap(s.fillBitmap, 0, 0, bitmapPaint);
            else canvas.drawPath(s.path, s.paint);
        }
        int targetColor = bitmap.getPixel(x, y);
        int replacementColor = brushColor;
        if (targetColor == replacementColor) return;

        Bitmap fillResult = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(x, y));
        int[] pixels = new int[getWidth() * getHeight()];
        bitmap.getPixels(pixels, 0, getWidth(), 0, 0, getWidth(), getHeight());
        int[] resultPixels = new int[getWidth() * getHeight()];

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        while (!queue.isEmpty()) {
            Point p = queue.poll();
            if (pixels[p.y * getWidth() + p.x] == targetColor) {
                pixels[p.y * getWidth() + p.x] = replacementColor;
                resultPixels[p.y * getWidth() + p.x] = replacementColor;
                if (p.x > 0) queue.add(new Point(p.x - 1, p.y));
                if (p.x < getWidth() - 1) queue.add(new Point(p.x + 1, p.y));
                if (p.y > 0) queue.add(new Point(p.x, p.y - 1));
                if (p.y < getHeight() - 1) queue.add(new Point(p.x, p.y + 1));
            }
        }
        fillResult.setPixels(resultPixels, 0, getWidth(), 0, 0, getWidth(), getHeight());
        layer.strokes.add(new Stroke(fillResult));
        invalidate();
    }

    /**
     * Hàm mapToContent() thực hiện một phần xử lý trong luồng chức năng của lớp ZoomDrawingView.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param vx tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param vy tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private float[] mapToContent(float vx, float vy) {
        float[] pts = new float[]{vx, vy};
        inverseMatrix.mapPoints(pts);
        return pts;
    }

    /**
     * Hàm updateInverse() thực hiện một phần xử lý trong luồng chức năng của lớp ZoomDrawingView.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    private void updateInverse() {
        viewMatrix.invert(inverseMatrix);
    }

    /**
     * Hàm makeBasePaint() thực hiện một phần xử lý trong luồng chức năng của lớp ZoomDrawingView.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    private Paint makeBasePaint() {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setDither(true);
        p.setFilterBitmap(true); // Quan trọng để không bị nhiễu khi zoom
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeJoin(Paint.Join.ROUND);
        p.setStrokeCap(Paint.Cap.ROUND);
        return p;
    }

    /**
     * Hàm updatePaint() thực hiện một phần xử lý trong luồng chức năng của lớp ZoomDrawingView.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    private void updatePaint() {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        currentPaint.setPathEffect(null);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        currentPaint.setMaskFilter(null);
        currentPaint.setStrokeJoin(Paint.Join.ROUND);
        currentPaint.setStrokeCap(Paint.Cap.ROUND);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        currentPaint.setXfermode(null);

        if (eraserMode) {
            currentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            currentPaint.setAlpha(255);
            currentPaint.setStrokeWidth(brushSizePx);
            return;
        }

        currentPaint.setColor(brushColor);
        int finalAlpha = brushAlpha;

        switch (brushType) {
            case WATERCOLOR:
                finalAlpha = (int)(brushAlpha * 0.4f);
                break;
            case CRAYON:
                currentPaint.setPathEffect(new android.graphics.DiscretePathEffect(Math.max(brushSizePx * 0.2f, 2f), Math.max(brushSizePx * 0.5f, 2f)));
                break;
            case MARKER:
                currentPaint.setStrokeCap(Paint.Cap.SQUARE);
                finalAlpha = (int)(brushAlpha * 0.6f);
                break;
            case AIRBRUSH:
                currentPaint.setMaskFilter(new android.graphics.BlurMaskFilter(Math.max(brushSizePx * 1.5f, 2f), android.graphics.BlurMaskFilter.Blur.NORMAL));
                finalAlpha = (int)(brushAlpha * 0.5f);
                break;
            case CALLIGRAPHY:
                Path dashPath = new Path();
                dashPath.addOval(new android.graphics.RectF(0, 0, brushSizePx, Math.max(brushSizePx * 0.2f, 2f)), Path.Direction.CCW);
                Matrix m = new Matrix();
                m.postRotate(45, brushSizePx / 2f, brushSizePx * 0.1f);
                dashPath.transform(m);
                currentPaint.setPathEffect(new android.graphics.PathDashPathEffect(dashPath, brushSizePx * 0.15f, 0, android.graphics.PathDashPathEffect.Style.MORPH));
                break;
            case OIL:
                currentPaint.setPathEffect(new android.graphics.DiscretePathEffect(brushSizePx * 0.1f, brushSizePx * 0.2f));
                break;
            default:
                break;
        }

        if (brushType != BrushType.AIRBRUSH && brushType != BrushType.CALLIGRAPHY) {
            if (hardness == Hardness.SOFT) {
                currentPaint.setMaskFilter(new android.graphics.BlurMaskFilter(Math.max(brushSizePx * 0.5f, 1f), android.graphics.BlurMaskFilter.Blur.NORMAL));
            } else if (hardness == Hardness.MEDIUM) {
                currentPaint.setMaskFilter(new android.graphics.BlurMaskFilter(Math.max(brushSizePx * 0.15f, 1f), android.graphics.BlurMaskFilter.Blur.NORMAL));
            }
        }

        currentPaint.setAlpha(finalAlpha);
        currentPaint.setStrokeWidth(brushSizePx);
    }

    /**
     * Hàm setBrushType() thực hiện một phần xử lý trong luồng chức năng của lớp ZoomDrawingView.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param type tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    public void setBrushType(BrushType type) {
        this.brushType = type;
        setEraser(false);
        setFillMode(false);
        updatePaint();
        invalidate();
    }
    public BrushType getBrushType() { return brushType; }

    /**
     * Hàm setHardness() thực hiện một phần xử lý trong luồng chức năng của lớp ZoomDrawingView.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param h tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    public void setHardness(Hardness h) {
        this.hardness = h;
        updatePaint();
        invalidate();
    }
    public Hardness getHardness() { return hardness; }

    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    public List<Layer> getLayers() { return layers; }
    public int getActiveLayerIndex() { return activeLayerIndex; }
    /**
     * Hàm setActiveLayerIndex() thực hiện một phần xử lý trong luồng chức năng của lớp ZoomDrawingView.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param index tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    public void setActiveLayerIndex(int index) {
        if (index >= 0 && index < layers.size()) {
            activeLayerIndex = index;
            invalidate();
        }
    }
    /**
     * Hàm getActiveLayer() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    public Layer getActiveLayer() {
        if (activeLayerIndex >= 0 && activeLayerIndex < layers.size()) return layers.get(activeLayerIndex);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        return null;
    }
    /**
     * Hàm addLayer() ghi, gửi hoặc tạo dữ liệu mới dựa trên thao tác của người dùng.
     * Các bước thường gồm kiểm tra dữ liệu đầu vào, chuẩn bị Object/Map và lưu lên Firebase hoặc API liên quan.
     */
    public void addLayer() {
        layers.add(new Layer("Lớp " + layers.size()));
        activeLayerIndex = layers.size() - 1;
        invalidate();
    }
    /**
     * Hàm removeLayer() thực hiện một phần xử lý trong luồng chức năng của lớp ZoomDrawingView.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param index tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    public void removeLayer(int index) {
        if (layers.size() > 1 && index >= 0 && index < layers.size()) {
            layers.remove(index);
            activeLayerIndex = Math.min(activeLayerIndex, layers.size() - 1);
            invalidate();
        }
    }
    /**
     * Hàm setBrushColor() thực hiện một phần xử lý trong luồng chức năng của lớp ZoomDrawingView.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param color tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    public void setBrushColor(int color) {
        this.brushColor = color;
        // KHÔNG tự động tắt eraserMode ở đây để tránh bị chuyển về bút khi không muốn
        updatePaint();
        invalidate();
    }
    /**
     * Hàm setBrushSizePx() thực hiện một phần xử lý trong luồng chức năng của lớp ZoomDrawingView.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param px tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    public void setBrushSizePx(float px) {
        this.brushSizePx = Math.max(2f, px);
        updatePaint();
        invalidate();
    }
    /**
     * Hàm setBrushOpacityPercent() thực hiện một phần xử lý trong luồng chức năng của lớp ZoomDrawingView.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param percent tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    public void setBrushOpacityPercent(int percent) {
        int p = Math.max(0, Math.min(100, percent));
        this.brushAlpha = (int) (255f * (p / 100f));
        updatePaint();
        invalidate();
    }
    /**
     * Hàm getBrushOpacityPercent() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    public int getBrushOpacityPercent() {
        return (int) ((brushAlpha / 255f) * 100);
    }
    /**
     * Hàm setEraser() thực hiện một phần xử lý trong luồng chức năng của lớp ZoomDrawingView.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param enabled tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    public void setEraser(boolean enabled) {
        this.eraserMode = enabled;
        if (enabled) this.fillMode = false;
        updatePaint();
        invalidate();
    }
    /**
     * Hàm setFillMode() thực hiện một phần xử lý trong luồng chức năng của lớp ZoomDrawingView.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param enabled tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    public void setFillMode(boolean enabled) {
        this.fillMode = enabled;
        if (enabled) this.eraserMode = false;
        invalidate();
    }
    public boolean isEraser() { return eraserMode; }
    public boolean isFillMode() { return fillMode; }
    /**
     * Hàm undo() thực hiện một phần xử lý trong luồng chức năng của lớp ZoomDrawingView.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    public void undo() {
        Layer activeLayer = getActiveLayer();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (activeLayer != null && !activeLayer.strokes.isEmpty()) {
            Stroke last = activeLayer.strokes.remove(activeLayer.strokes.size() - 1);
            activeLayer.undone.addLast(last);
            invalidate();
        }
    }
    /**
     * Hàm redo() thực hiện một phần xử lý trong luồng chức năng của lớp ZoomDrawingView.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    public void redo() {
        Layer activeLayer = getActiveLayer();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (activeLayer != null && !activeLayer.undone.isEmpty()) {
            activeLayer.strokes.add(activeLayer.undone.removeLast());
            invalidate();
        }
    }
    /**
     * Hàm clearAll() thực hiện một phần xử lý trong luồng chức năng của lớp ZoomDrawingView.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    public void clearAll() {
        Layer activeLayer = getActiveLayer();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (activeLayer != null) {
            activeLayer.strokes.clear();
            activeLayer.undone.clear();
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            currentPath = null;
            invalidate();
        }
    }
    /**
     * Hàm clearCanvasUndoable() thực hiện một phần xử lý trong luồng chức năng của lớp ZoomDrawingView.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    public void clearCanvasUndoable() {
        Layer activeLayer = getActiveLayer();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (activeLayer != null) {
            activeLayer.undone.clear();
            activeLayer.strokes.add(new Stroke(true));
            invalidate();
        }
    }
    /**
     * Hàm exportBitmap() thực hiện một phần xử lý trong luồng chức năng của lớp ZoomDrawingView.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    public Bitmap exportBitmap() {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (getWidth() <= 0 || getHeight() <= 0) return null;
        Bitmap out = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(out);
        draw(c);
        return out;
    }

    /**
     * Hàm clampMatrix() thực hiện một phần xử lý trong luồng chức năng của lớp ZoomDrawingView.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    private void clampMatrix() {
        if (getWidth() == 0 || getHeight() == 0) return;
        float[] values = new float[9];
        viewMatrix.getValues(values);
        float scale = values[Matrix.MSCALE_X];
        float transX = values[Matrix.MTRANS_X];
        float transY = values[Matrix.MTRANS_Y];

        float winWidth = getWidth();
        float winHeight = getHeight();
        float scaledWidth = winWidth * scale;
        float scaledHeight = winHeight * scale;

        if (scaledWidth >= winWidth) {
            transX = Math.max(winWidth - scaledWidth, Math.min(transX, 0));
        } else {
            transX = (winWidth - scaledWidth) / 2f;
        }

        if (scaledHeight >= winHeight) {
            transY = Math.max(winHeight - scaledHeight, Math.min(transY, 0));
        } else {
            transY = (winHeight - scaledHeight) / 2f;
        }

        values[Matrix.MTRANS_X] = transX;
        values[Matrix.MTRANS_Y] = transY;
        viewMatrix.setValues(values);
    }
}
