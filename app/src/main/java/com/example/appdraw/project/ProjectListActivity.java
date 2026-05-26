package com.example.appdraw.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.appdraw.project.CreateProjectActivity;
import com.example.appdraw.R;
import com.example.appdraw.model.Project;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Màn hình Danh sách Dự án (UC-12).
 * Người thực hiện: Vũ Quang Vinh.
 * Liệt kê các dự án nghệ thuật cá nhân đang thực hiện hoặc đã hoàn thành.
 */
/**
 * Lớp ProjectListActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file ProjectListActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class ProjectListActivity extends AppCompatActivity {

    /**
     * Biến `rvProjects` lưu dữ liệu/trạng thái quan trọng kiểu RecyclerView, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
    private RecyclerView rvProjects;
    /**
     * Biến `adapter` quản lý danh sách hiển thị bằng RecyclerView/Adapter, giúp ánh xạ dữ liệu nguồn lên từng item trên giao diện.
     */
    private ProjectAdapter adapter;
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    private List<Project> projectList = new ArrayList<>();
    /**
     * Biến `db` lưu dữ liệu/trạng thái quan trọng kiểu FirebaseFirestore, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
    private FirebaseFirestore db;

    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        db = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar_projects);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        FloatingActionButton fab = findViewById(R.id.fab_add_project);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (fab != null) {
            fab.setOnClickListener(v -> {
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                startActivity(new Intent(this, CreateProjectActivity.class));
            });
        }

        rvProjects = findViewById(R.id.rv_projects);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (rvProjects != null) {
            // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
            rvProjects.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            adapter = new ProjectAdapter();
            // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
            rvProjects.setAdapter(adapter);
            loadProjects();
        }
    }

    /**
     * Hàm loadProjects() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    private void loadProjects() {
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        String uid = FirebaseAuth.getInstance().getUid();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (uid == null) return;
        
        db.collection("Projects").whereEqualTo("uid", uid)
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            .addSnapshotListener((value, error) -> {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (error != null) {
                    Toast.makeText(this, "Lỗi tải dự án: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                projectList.clear();
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (value != null) {
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    for (QueryDocumentSnapshot doc : value) {
                        try {
                            Project p = doc.toObject(Project.class);
                            projectList.add(p);
                        } catch (Exception ignored) {}
                    }
                    
                    // Sắp xếp local để tránh lỗi thiếu Index trên Firestore
                    java.util.Collections.sort(projectList, (p1, p2) -> Long.compare(p2.getCreatedAt(), p1.getCreatedAt()));
                }
                adapter.notifyDataSetChanged();
            });
    }

    // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
    private class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        @NonNull
        @Override
        /**
         * Hàm onCreateViewHolder() tạo ViewHolder cho RecyclerView bằng cách inflate layout item.
         * ViewHolder giúp tái sử dụng view, giảm chi phí tạo giao diện khi danh sách cuộn nhiều phần tử.
         * @param parent tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         * @param viewType tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         */
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project_card, parent, false);
            return new ViewHolder(view);
        }

        @Override
        /**
         * Hàm onBindViewHolder() gắn dữ liệu tại một vị trí cụ thể vào item của RecyclerView.
         * Dữ liệu thường lấy từ List/Map/Object đã tải từ Firebase hoặc được truyền từ màn hình trước.
         * @param holder tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         * @param position tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         */
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Project project = projectList.get(position);
            holder.tvName.setText(project.getName());
            
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (project.getDescription() != null && !project.getDescription().isEmpty()) {
                holder.tvDescription.setText(project.getDescription());
                holder.tvDescription.setVisibility(View.VISIBLE);
            } else {
                holder.tvDescription.setVisibility(View.GONE);
            }

            holder.tvArtworkCount.setText(project.getArtworkCount() + " tác phẩm");

            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (project.getCoverImageUrl() != null && !project.getCoverImageUrl().isEmpty()) {
                if (project.getCoverImageUrl().startsWith("data:image")) {
                    byte[] b = android.util.Base64.decode(project.getCoverImageUrl().split(",")[1], android.util.Base64.DEFAULT);
                    // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                    Glide.with(ProjectListActivity.this).load(b).centerCrop().into(holder.ivThumb);
                } else {
                    // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                    Glide.with(ProjectListActivity.this).load(project.getCoverImageUrl()).centerCrop().into(holder.ivThumb);
                }
            } else {
                // Background màu trơn tao nhã dựa trên tên dự án
                // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                Glide.with(ProjectListActivity.this).clear(holder.ivThumb);
                String[] colorHexes = {"#4272D0", "#E75A7C", "#F0B259", "#70C1B3", "#2C363F", "#8E44AD", "#34495E", "#D35400"};
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                int colorIndex = Math.abs((project.getName() != null ? project.getName() : "").hashCode()) % colorHexes.length;
                holder.ivThumb.setImageDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.parseColor(colorHexes[colorIndex])));
            }

            holder.itemView.setOnClickListener(v -> {
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                Intent intent = new Intent(ProjectListActivity.this, ProjectDetailActivity.class);
                intent.putExtra("PROJECT_ID", project.getId());
                intent.putExtra("PROJECT_NAME", project.getName());
                intent.putExtra("PROJECT_DESC", project.getDescription());
                intent.putExtra("PROJECT_COVER", project.getCoverImageUrl());
                startActivity(intent);
            });

            holder.itemView.setOnLongClickListener(v -> {
                showProjectOptionsDialog(project);
                return true;
            });

            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (holder.btnOptions != null) {
                holder.btnOptions.setOnClickListener(v -> showProjectOptionsDialog(project));
            }
        }

        @Override
        /**
         * Hàm getItemCount() trả về số lượng phần tử hiện có trong danh sách của RecyclerView.
         * Giá trị này giúp RecyclerView biết cần tạo và hiển thị bao nhiêu item trên màn hình.
         */
        public int getItemCount() {
            return projectList.size();
        }

/**
 * Lớp ViewHolder thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file ProjectListActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
        // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivThumb, btnOptions;
            TextView tvName, tvDescription, tvArtworkCount;

            /**
             * Constructor của lớp ViewHolder, dùng để khởi tạo đối tượng với các dữ liệu cần thiết trước khi sử dụng.
             * @param itemView tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
             */
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivThumb = itemView.findViewById(R.id.iv_project_thumb);
                btnOptions = itemView.findViewById(R.id.btn_project_options);
                tvName = itemView.findViewById(R.id.tv_project_name);
                tvDescription = itemView.findViewById(R.id.tv_project_description);
                tvArtworkCount = itemView.findViewById(R.id.tv_artwork_count);
            }
        }
    }

    /**
     * Hàm showProjectOptionsDialog() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     * @param project tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void showProjectOptionsDialog(Project project) {
        String[] options = {"✏️ Đổi tên dự án", "🗑️ Xóa dự án"};
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Tùy chọn Dự án")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showRenameProjectDialog(project);
                    } else if (which == 1) {
                        showDeleteProjectConfirmDialog(project);
                    }
                })
                .show();
    }

    /**
     * Hàm showRenameProjectDialog() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     * @param project tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void showRenameProjectDialog(Project project) {
        android.widget.EditText input = new android.widget.EditText(this);
        input.setText(project.getName());
        input.setSingleLine(true);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        input.setPadding(padding, padding, padding, padding);

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Đổi tên dự án")
                .setView(input)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (!newName.isEmpty()) {
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        db.collection("Projects").document(project.getId())
                                .update("name", newName)
                                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Đã đổi tên", Toast.LENGTH_SHORT).show());
                    }
                })
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                .setNegativeButton("Hủy", null)
                .show();
    }

    /**
     * Hàm showDeleteProjectConfirmDialog() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     * @param project tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void showDeleteProjectConfirmDialog(Project project) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xóa dự án")
                .setMessage("Bạn có chắc chắn muốn xóa dự án này? Toàn bộ tác phẩm bên trong cũng sẽ bị xóa vĩnh viễn.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    // Xóa tất cả tác phẩm trong dự án
                    db.collection("Artworks").whereEqualTo("projectId", project.getId()).get()
                            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                                    db.collection("Artworks").document(doc.getId()).delete();
                                }
                                // Sau đó xóa dự án
                                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                                db.collection("Projects").document(project.getId()).delete()
                                        .addOnSuccessListener(aVoid -> Toast.makeText(this, "Đã xóa dự án", Toast.LENGTH_SHORT).show());
                            });
                })
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                .setNegativeButton("Hủy", null)
                .show();
    }
}
