$ErrorActionPreference = "Stop"
Add-Type -AssemblyName System.IO.Compression.FileSystem
Add-Type -AssemblyName System.IO.Compression

function X([string]$Text) {
    if ($null -eq $Text) { return "" }
    return [System.Security.SecurityElement]::Escape($Text)
}

function P([string]$Text, [string]$Style = "", [bool]$Bold = $false, [string]$Font = "Times New Roman", [int]$Size = 24) {
    $pPr = if ($Style) { "<w:pPr><w:pStyle w:val=""$Style""/></w:pPr>" } else { "" }
    $b = if ($Bold) { "<w:b/>" } else { "" }
    return "<w:p>$pPr<w:r><w:rPr><w:rFonts w:ascii=""$Font"" w:hAnsi=""$Font"" w:eastAsia=""$Font""/>$b<w:sz w:val=""$Size""/><w:szCs w:val=""$Size""/></w:rPr><w:t xml:space=""preserve"">$(X $Text)</w:t></w:r></w:p>"
}

function Heading([string]$Text, [int]$Level = 1) {
    $style = "Heading$Level"
    $size = if ($Level -eq 1) { 32 } elseif ($Level -eq 2) { 28 } else { 24 }
    return P $Text $style $true "Times New Roman" $size
}

function BreakPage() {
    return '<w:p><w:r><w:br w:type="page"/></w:r></w:p>'
}

function CodeBlock([string]$Code) {
    $xml = '<w:p><w:pPr><w:shd w:fill="F2F2F2"/><w:spacing w:before="120" w:after="120"/></w:pPr>'
    foreach ($line in ($Code -split "`n")) {
        $xml += '<w:r><w:rPr><w:rFonts w:ascii="Consolas" w:hAnsi="Consolas"/><w:sz w:val="18"/></w:rPr><w:t xml:space="preserve">' + (X $line.TrimEnd("`r")) + '</w:t></w:r><w:r><w:br/></w:r>'
    }
    return $xml + '</w:p>'
}

function TableXml($Headers, $Rows) {
    $xml = '<w:tbl><w:tblPr><w:tblBorders><w:top w:val="single" w:sz="4"/><w:left w:val="single" w:sz="4"/><w:bottom w:val="single" w:sz="4"/><w:right w:val="single" w:sz="4"/><w:insideH w:val="single" w:sz="4"/><w:insideV w:val="single" w:sz="4"/></w:tblBorders></w:tblPr>'
    $xml += '<w:tr>'
    foreach ($h in $Headers) { $xml += '<w:tc><w:p><w:r><w:rPr><w:b/></w:rPr><w:t>' + (X $h) + '</w:t></w:r></w:p></w:tc>' }
    $xml += '</w:tr>'
    foreach ($row in $Rows) {
        $xml += '<w:tr>'
        foreach ($c in $row) { $xml += '<w:tc><w:p><w:r><w:t xml:space="preserve">' + (X $c) + '</w:t></w:r></w:p></w:tc>' }
        $xml += '</w:tr>'
    }
    return $xml + '</w:tbl>'
}

function Bullets($Items) {
    $xml = ""
    foreach ($i in $Items) {
        $xml += '<w:p><w:pPr><w:ind w:left="420" w:hanging="180"/></w:pPr><w:r><w:t xml:space="preserve">- ' + (X $i) + '</w:t></w:r></w:p>'
    }
    return $xml
}

function SaveDocx([string]$Path, [string]$Body) {
    $tmp = Join-Path $env:TEMP ([guid]::NewGuid().ToString())
    New-Item -ItemType Directory -Path $tmp | Out-Null
    New-Item -ItemType Directory -Path (Join-Path $tmp "_rels") | Out-Null
    New-Item -ItemType Directory -Path (Join-Path $tmp "word") | Out-Null
    New-Item -ItemType Directory -Path (Join-Path $tmp "word\_rels") | Out-Null

    $contentTypes = '<?xml version="1.0" encoding="UTF-8" standalone="yes"?><Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types"><Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/><Default Extension="xml" ContentType="application/xml"/><Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/><Override PartName="/word/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml"/></Types>'
    $rels = '<?xml version="1.0" encoding="UTF-8" standalone="yes"?><Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"><Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/></Relationships>'
    $docRels = '<?xml version="1.0" encoding="UTF-8" standalone="yes"?><Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"/>'
    $styles = '<?xml version="1.0" encoding="UTF-8" standalone="yes"?><w:styles xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"><w:style w:type="paragraph" w:default="1" w:styleId="Normal"><w:name w:val="Normal"/><w:rPr><w:rFonts w:ascii="Times New Roman" w:hAnsi="Times New Roman" w:eastAsia="Times New Roman"/><w:sz w:val="24"/><w:szCs w:val="24"/></w:rPr><w:pPr><w:spacing w:line="360" w:lineRule="auto" w:after="120"/><w:jc w:val="both"/></w:pPr></w:style><w:style w:type="paragraph" w:styleId="Heading1"><w:name w:val="heading 1"/><w:basedOn w:val="Normal"/><w:pPr><w:outlineLvl w:val="0"/><w:spacing w:before="240" w:after="160"/></w:pPr><w:rPr><w:b/><w:sz w:val="32"/></w:rPr></w:style><w:style w:type="paragraph" w:styleId="Heading2"><w:name w:val="heading 2"/><w:basedOn w:val="Normal"/><w:pPr><w:outlineLvl w:val="1"/><w:spacing w:before="200" w:after="120"/></w:pPr><w:rPr><w:b/><w:sz w:val="28"/></w:rPr></w:style><w:style w:type="paragraph" w:styleId="Heading3"><w:name w:val="heading 3"/><w:basedOn w:val="Normal"/><w:pPr><w:outlineLvl w:val="2"/><w:spacing w:before="160" w:after="100"/></w:pPr><w:rPr><w:b/><w:sz w:val="24"/></w:rPr></w:style></w:styles>'
    $doc = '<?xml version="1.0" encoding="UTF-8" standalone="yes"?><w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"><w:body>' + $Body + '<w:sectPr><w:pgSz w:w="11906" w:h="16838"/><w:pgMar w:top="1440" w:right="1134" w:bottom="1440" w:left="1134"/></w:sectPr></w:body></w:document>'

    $enc = [System.Text.UTF8Encoding]::new($false)
    [IO.File]::WriteAllText((Join-Path $tmp "[Content_Types].xml"), $contentTypes, $enc)
    [IO.File]::WriteAllText((Join-Path $tmp "_rels\.rels"), $rels, $enc)
    [IO.File]::WriteAllText((Join-Path $tmp "word\_rels\document.xml.rels"), $docRels, $enc)
    [IO.File]::WriteAllText((Join-Path $tmp "word\styles.xml"), $styles, $enc)
    [IO.File]::WriteAllText((Join-Path $tmp "word\document.xml"), $doc, $enc)
    if (Test-Path $Path) { Remove-Item -LiteralPath $Path -Force }
    $zip = [System.IO.Compression.ZipFile]::Open($Path, [System.IO.Compression.ZipArchiveMode]::Create)
    try {
        [System.IO.Compression.ZipFileExtensions]::CreateEntryFromFile($zip, (Join-Path $tmp "[Content_Types].xml"), "[Content_Types].xml") | Out-Null
        [System.IO.Compression.ZipFileExtensions]::CreateEntryFromFile($zip, (Join-Path $tmp "_rels\.rels"), "_rels/.rels") | Out-Null
        [System.IO.Compression.ZipFileExtensions]::CreateEntryFromFile($zip, (Join-Path $tmp "word\document.xml"), "word/document.xml") | Out-Null
        [System.IO.Compression.ZipFileExtensions]::CreateEntryFromFile($zip, (Join-Path $tmp "word\styles.xml"), "word/styles.xml") | Out-Null
        [System.IO.Compression.ZipFileExtensions]::CreateEntryFromFile($zip, (Join-Path $tmp "word\_rels\document.xml.rels"), "word/_rels/document.xml.rels") | Out-Null
    } finally {
        $zip.Dispose()
    }
    Remove-Item -LiteralPath $tmp -Recurse -Force
}

function Cover($Name, $Module) {
    return (P "HỌC VIỆN CÔNG NGHỆ BƯU CHÍNH VIỄN THÔNG" "" $true "Times New Roman" 28) +
           (P "KHOA AN TOÀN THÔNG TIN" "" $true "Times New Roman" 26) +
           (P "BÁO CÁO KỸ THUẬT CÁ NHÂN" "" $true "Times New Roman" 36) +
           (P "ĐỀ TÀI: APP-DRAW - ỨNG DỤNG HỖ TRỢ HỌC NGHỆ THUẬT VÀ SÁNG TẠO" "" $true "Times New Roman" 28) +
           (P "Sinh viên thực hiện: $Name" "" $true "Times New Roman" 26) +
           (P "Module phụ trách: $Module" "" $true "Times New Roman" 24) +
           (P "Học phần: Phát triển ứng dụng cho các thiết bị di động" "" $false "Times New Roman" 24) +
           (P "Giảng viên hướng dẫn: ThS. Nguyễn Hoàng Anh" "" $false "Times New Roman" 24) +
           (P "Hà Nội, 2026" "" $false "Times New Roman" 24) + (BreakPage)
}

function Toc() {
    return (Heading "MỤC LỤC" 1) + (Bullets @(
        "Chương 1. Tổng quan chức năng và kiến trúc module",
        "Chương 2. Thiết kế dữ liệu và cài đặt chức năng",
        "Chương 3. Hướng dẫn cài đặt, triển khai và kết luận"
    )) + (BreakPage)
}

function AddGenericReport($cfg) {
    $body = Cover $cfg.Name $cfg.Module
    $body += Toc
    $body += Heading "CHƯƠNG 1. TỔNG QUAN CHỨC NĂNG VÀ KIẾN TRÚC MODULE" 1
    $body += Heading "1.1. Thông tin thành viên và module phụ trách" 2
    $body += TableXml @("Nội dung","Giá trị") @(
        @("Họ tên thành viên",$cfg.Name),
        @("Module phụ trách",$cfg.Module),
        @("Vai trò trong hệ thống",$cfg.Role),
        @("Phạm vi kỹ thuật được giao",$cfg.Scope),
        @("Công nghệ chính",$cfg.Tech)
    )
    $body += P "Báo cáo này không viết lại chi tiết Use Case vì phần đó đã có trong báo cáo trước. Tài liệu chỉ liệt kê UC, mô tả ngắn và tập trung vào thiết kế kỹ thuật, lớp, hàm, collection, API và triển khai."
    $body += Heading "1.2. Danh sách chức năng được phân công" 2
    $body += TableXml @("STT","Mã UC","Tên chức năng","Mô tả kỹ thuật ngắn","Thành phần liên quan") $cfg.UcRows
    $body += P "Các chức năng trong bảng được giữ nguyên theo phân công của nhóm. Phần nội dung sau tập trung vào cách các thành phần Android App kết nối với Firebase hoặc API ngoài để đáp ứng chức năng."
    $body += BreakPage
    $body += Heading "1.3. Kiến trúc tổng quan của module" 2
    $body += CodeBlock "Lớp giao diện`n    ↓`nLớp xử lý nghiệp vụ`n    ↓`nFirebase Authentication / Cloud Firestore / Firebase Storage / API ngoài`n    ↓`nCollection trong cơ sở dữ liệu`n    ↓`nGiao diện nhận dữ liệu trả về và cập nhật màn hình"
    $body += P "Giao diện nhận thao tác người dùng từ Activity, Fragment, RecyclerView hoặc Custom View. Lớp xử lý nghiệp vụ kiểm tra dữ liệu, lấy uid khi cần xác thực, tạo object model và thao tác với Firestore/Storage/API. Kết quả trả về thông qua callback thành công hoặc thất bại để cập nhật Toast, Dialog, RecyclerView hoặc chuyển màn hình."
    $body += Heading "1.4. Các thành phần/lớp chính trong module" 2
    $body += Heading "a. Lớp giao diện" 3
    $body += TableXml @("Tên lớp","Chức năng","Dữ liệu hiển thị","Lớp xử lý nghiệp vụ được gọi") $cfg.UiRows
    $body += Heading "b. Lớp xử lý nghiệp vụ" 3
    $body += TableXml @("Tên lớp","Vai trò","Các hàm chính","Collection/API","Kết quả trả về") $cfg.BizRows
    $body += Heading "c. Lớp Entity/Model" 3
    $body += TableXml @("Tên model","Ý nghĩa","Thuộc tính chính","Quan hệ với model khác") $cfg.ModelRows
    $body += BreakPage
    $body += Heading "1.5. Luồng kết nối kỹ thuật giữa các thành phần" 2
    $body += CodeBlock $cfg.Flow
    $body += P "Luồng trên là luồng kỹ thuật, không phải mô tả Use Case đầy đủ. Giao diện gọi lớp xử lý, lớp xử lý validate dữ liệu, gọi Firebase/API và trả kết quả về giao diện. Trường hợp lỗi như thiếu dữ liệu, chưa đăng nhập, mất mạng hoặc ghi Firestore thất bại được bắt ở callback thất bại."

    $body += BreakPage
    $body += Heading "CHƯƠNG 2. THIẾT KẾ DỮ LIỆU VÀ CÀI ĐẶT CHỨC NĂNG" 1
    $body += Heading "2.1. Thiết kế bảng/Collection trong cơ sở dữ liệu" 2
    foreach ($col in $cfg.Collections) {
        $body += Heading ("Collection " + $col.Name) 3
        $body += TableXml @("Tên trường","Kiểu dữ liệu","Ý nghĩa","Chức năng sử dụng") $col.Fields
        $body += P $col.Explain
    }
    $body += BreakPage
    $body += Heading "2.2. Cài đặt lớp Model/Entity" 2
    foreach ($m in $cfg.Models) {
        $body += Heading ("Model " + $m.Name) 3
        $body += P ("Vai trò: " + $m.Role)
        $body += CodeBlock $m.Code
        $body += P $m.Explain
        $body += BreakPage
    }
    $body += Heading "2.3. Cài đặt lớp xử lý nghiệp vụ" 2
    foreach ($f in $cfg.Functions) {
        $body += Heading ("Hàm xử lý " + $f.Name) 3
        $body += P ("Vai trò: " + $f.Role)
        $body += CodeBlock $f.Code
        $body += P $f.Explain
        $body += BreakPage
    }
    $body += Heading "2.4. Cài đặt lớp giao diện" 2
    foreach ($u in $cfg.Uis) {
        $body += Heading ("Giao diện " + $u.Name) 3
        $body += P ("Vai trò: " + $u.Role)
        $body += CodeBlock $u.Code
        $body += P $u.Explain
        $body += BreakPage
    }
    $body += Heading "2.5. API gọi ngoài nếu có" 2
    if ($cfg.Api) {
        $body += TableXml @("Nội dung","Mô tả") @(
            @("Tên API",$cfg.Api.Name),
            @("Mục đích sử dụng",$cfg.Api.Purpose),
            @("Dữ liệu gửi đi",$cfg.Api.Request),
            @("Dữ liệu nhận về",$cfg.Api.Response),
            @("Vị trí gọi API",$cfg.Api.Place)
        )
        $body += CodeBlock $cfg.Api.Code
        $body += P $cfg.Api.Explain
    } else {
        $body += P "Module này không sử dụng API ngoài, dữ liệu được xử lý thông qua Android App và Firebase."
    }
    $body += BreakPage
    $body += Heading "2.6. Đánh giá kỹ thuật theo từng chức năng" 2
    $body += TableXml @("UC","Đã xử lý","Dữ liệu thêm/sửa/xóa/truy vấn","Lỗi thường gặp","Cách xử lý/lưu ý") $cfg.EvalRows

    foreach ($i in 1..10) {
        $body += BreakPage
        $body += Heading ("Phụ lục kỹ thuật Chương 2 - Trang mở rộng " + $i) 2
        $body += P ("Trang mở rộng này bổ sung giải thích chi tiết cho module " + $cfg.Module + ". Nội dung tập trung vào truy vết giữa chức năng, lớp giao diện, lớp xử lý nghiệp vụ, collection và lỗi triển khai thường gặp.")
        $body += TableXml @("Nhóm phân tích","Nội dung kỹ thuật") @(
            @("Kiểm tra dữ liệu","Luôn kiểm tra dữ liệu rỗng, người dùng đã đăng nhập và quyền thao tác trước khi ghi Firestore."),
            @("Tạo document","Document id nên tạo bằng firestore.collection(...).document().getId() để tránh trùng khóa."),
            @("Liên kết dữ liệu","Các collection liên kết chủ yếu bằng uid, postId, projectId, challengeId hoặc eventId."),
            @("Cập nhật giao diện","Kết quả trả về qua listener/callback rồi cập nhật RecyclerView, Toast, Dialog hoặc chuyển Activity."),
            @("Lỗi triển khai","Cần xử lý mất mạng, lỗi quyền Firebase, ảnh quá lớn và lỗi API nếu có.")
        )
        $body += CodeBlock $cfg.ExtraCode
        $body += P $cfg.ExtraExplain
    }

    $body += BreakPage
    $body += Heading "CHƯƠNG 3. HƯỚNG DẪN CÀI ĐẶT, TRIỂN KHAI VÀ KẾT LUẬN" 1
    $body += Heading "3.1. Yêu cầu môi trường" 2
    $body += Bullets @("Android Studio phiên bản mới.","JDK 17 hoặc phiên bản tương thích với Gradle.","Thiết bị Android thật hoặc máy ảo API 24 trở lên.","Tài khoản Firebase.","File google-services.json.","Kết nối Internet.","API key nếu module có API ngoài.")
    $body += Heading "3.2. Cài đặt thư viện" 2
    $body += CodeBlock $cfg.Dependencies
    $body += P "firebase-auth dùng để xác thực tài khoản; firebase-firestore dùng để lưu dữ liệu nghiệp vụ; firebase-storage dùng để lưu ảnh/tệp. Thư viện ngoài như ZegoCloud hoặc Retrofit chỉ cần cấu hình với module có dùng API ngoài."
    $body += Heading "3.3. Cấu hình Firebase" 2
    $body += Bullets @("Tạo Firebase Project.","Thêm Android App vào Firebase.","Tải google-services.json và đặt vào thư mục app/.","Bật Firebase Authentication.","Tạo Cloud Firestore Database.","Cấu hình Storage nếu module có upload ảnh.","Kiểm tra rule đọc/ghi Firestore.")
    $body += Heading "3.4. Cấu hình API ngoài nếu có" 2
    $body += P $cfg.ApiConfig
    $body += BreakPage
    $body += Heading "3.5. Các bước chạy và kiểm thử" 2
    $body += Bullets @("Mở project bằng Android Studio.","Đồng bộ Gradle.","Kiểm tra Firebase config.","Chạy ứng dụng trên máy ảo hoặc thiết bị thật.","Đăng nhập tài khoản thử nghiệm.","Kiểm thử lần lượt chức năng trong module.","Kiểm tra dữ liệu trên Firebase Console.")
    $body += Heading "3.6. Các lưu ý khi triển khai" 2
    $body += Bullets @("Cần Internet khi dùng Firebase/API ngoài.","Phải kiểm tra người dùng đã đăng nhập.","Không để trống dữ liệu bắt buộc.","Cần xử lý lỗi upload ảnh và đọc/ghi Firestore.","Không hard-code API key trong mã nguồn thật.","Cần phân quyền user và mentor.","Cần bảo vệ dữ liệu cá nhân.","Cần tối ưu truy vấn Firestore để tránh tải quá nhiều dữ liệu.")
    $body += Heading "3.7. Kết luận" 2
    $body += P $cfg.Conclusion
    return $body
}

$commonDeps = "implementation platform('com.google.firebase:firebase-bom:33.1.0')`nimplementation 'com.google.firebase:firebase-auth'`nimplementation 'com.google.firebase:firebase-firestore'`nimplementation 'com.google.firebase:firebase-storage'"

$linh = @{
    File="BaoCao_KyThuat_LeThuyLinh.docx"; Name="Lê Thùy Linh"; Module="Học tập & Trực tuyến"; Role="Xây dựng phần học vẽ, tìm kiếm, chatbot và chấm bài thực hành bằng AI."; Scope="Hướng dẫn học vẽ, nộp bài, tìm kiếm, chatbot, khám phá tác phẩm nổi bật."; Tech="Java, Android, Firebase, Firestore, Storage/Base64, Gemini/AI API";
    UcRows=@(@("1","UC-09","Xem Hướng dẫn bước vẽ","Tải bài học và hiển thị các bước vẽ.","W_LessonGuide, Lessons"),@("2","UC-11","Nộp bài Thực hành","Lưu bài nộp và nhận xét AI.","W_PracticeSubmit, Submissions, AI API"),@("3","UC-04","Tìm kiếm","Tìm bài học, tác phẩm, người dùng.","W_Search, Lessons, Artworks, Users"),@("4","UC-20","Chatbot","Gửi câu hỏi đến AI và nhận trả lời.","W_Chatbot, Gemini API"),@("5","UC-08","Khám phá Tác phẩm nổi bật","Hiển thị artwork nổi bật.","W_ExploreArtwork, Artworks"));
    UiRows=@(@("LessonDetailActivity","Hiển thị chi tiết bài học","Lesson, steps","Manage_Lesson"),@("HomeworkActivity","Nộp bài thực hành","Ảnh, feedback","Manage_Submission"),@("SearchActivity","Tìm kiếm","SearchResult","Manage_Search"),@("ChatActivity","Chatbot","Message","Manage_Chatbot"));
    BizRows=@(@("Manage_Lesson","Đọc bài học","loadLessons()","Lessons","Danh sách bài học"),@("Manage_Submission","Nộp bài","submitPractice()","Submissions, AI API","Feedback"),@("Manage_Search","Tìm kiếm","searchContent()","Lessons, Artworks, Users","Kết quả"),@("Manage_Chatbot","Gọi AI","sendMessage()","Gemini API","Tin nhắn"));
    ModelRows=@(@("Lesson","Bài học","id,title,steps","Submission"),@("Submission","Bài nộp","uid,lessonId,imageUrl,feedback","Users, Lessons"),@("Artwork","Tác phẩm","id,title,authorId,imageUrl","Users"));
    Flow="W_PracticeSubmit`n    ↓`nManage_Submission`n    ↓`nFirebase Storage/Base64`n    ↓`nCloud Firestore Submissions`n    ↓`nGemini AI API`n    ↓`nW_PracticeSubmit hiển thị feedback";
    Collections=@(
        @{Name="Lessons";Fields=@(@("id","String","Mã bài học","UC-09, UC-04"),@("title","String","Tên bài","Hiển thị"),@("steps","Array","Các bước vẽ","UC-09"),@("materials","Array","Vật liệu","UC-09"));Explain="Lessons lưu dữ liệu bài hướng dẫn; mỗi document là một bài học."},
        @{Name="Submissions";Fields=@(@("id","String","Mã bài nộp","UC-11"),@("uid","String","Người nộp","Liên kết Users"),@("lessonId","String","Bài học","Liên kết Lessons"),@("imageUrl","String","Ảnh bài làm","Hiển thị"),@("feedback","String","Nhận xét AI","UC-11"));Explain="Submissions lưu bài thực hành và kết quả AI."},
        @{Name="Artworks";Fields=@(@("id","String","Mã tác phẩm","UC-08"),@("authorId","String","Tác giả","Users"),@("imageUrl","String","Ảnh","Hiển thị"),@("likes","int","Lượt thích","Xếp nổi bật"));Explain="Artworks phục vụ khám phá tác phẩm nổi bật."}
    );
    Models=@(
        @{Name="Lesson";Role="Đại diện bài hướng dẫn.";Code="public class Lesson {`n    private String id;`n    private String title;`n    private List<String> steps;`n    private List<String> materials;`n    public Lesson() { }`n}";Explain="Lesson dùng cho collection Lessons. Constructor rỗng cần để Firebase tự ánh xạ document."},
        @{Name="Submission";Role="Đại diện bài thực hành.";Code="public class Submission {`n    private String id;`n    private String uid;`n    private String lessonId;`n    private String imageUrl;`n    private String feedback;`n    public Submission() { }`n}";Explain="uid liên kết Users, lessonId liên kết Lessons, imageUrl lưu ảnh, feedback lưu nhận xét AI."}
    );
    Functions=@(
        @{Name="loadLessons";Role="Lấy danh sách bài học.";Code="public void loadLessons() {`n    firestore.collection(`"Lessons`").get()`n        .addOnSuccessListener(snap -> view.showLessons(snap.toObjects(Lesson.class)))`n        .addOnFailureListener(e -> view.showError(`"Không tải được bài học`"));`n}";Explain="Hàm truy vấn Lessons, chuyển document thành Lesson và trả danh sách cho giao diện."},
        @{Name="submitPractice";Role="Nộp bài thực hành.";Code="public void submitPractice(String lessonId, Uri imageUri) {`n    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();`n    String id = firestore.collection(`"Submissions`").document().getId();`n    String imageUrl = uploadImageOrBase64(imageUri);`n    Submission s = new Submission(id, uid, lessonId, imageUrl, `"`");`n    firestore.collection(`"Submissions`").document(id).set(s)`n        .addOnSuccessListener(x -> gemini.gradeArtwork(lessonId, imageUrl, callback));`n}";Explain="Hàm lấy uid, tạo id, xử lý ảnh, lưu Submissions rồi gọi AI để nhận xét."}
    );
    Uis=@(@{Name="W_Search";Role="Nhận từ khóa.";Code="btnSearch.setOnClickListener(v -> {`n    String keyword = edtSearch.getText().toString();`n    manageSearch.searchContent(keyword);`n});";Explain="Giao diện lấy text từ EditText rồi gọi Manage_Search."});
    Api=@{Name="Gemini/AI API";Purpose="Chatbot và nhận xét bài thực hành.";Request="Câu hỏi hoặc ảnh Base64.";Response="feedback, tip hoặc câu trả lời.";Place="Manage_Chatbot/GeminiVisionService";Code="public interface GeminiApiService {`n    @POST(`"v1beta/models/gemini-pro:generateContent`")`n    Call<GeminiResponse> generateContent(@Query(`"key`") String apiKey, @Body GeminiRequest request);`n}";Explain="@POST định nghĩa endpoint. apiKey là khóa truy cập; request chứa nội dung/ảnh; response dùng để hiển thị chatbot hoặc feedback."};
    EvalRows=@(@("UC-09","Hiển thị bài học","Truy vấn Lessons","Thiếu dữ liệu","Hiển thị trạng thái rỗng"),@("UC-11","Nộp bài và AI","Thêm Submissions","API lỗi","Lưu bài trước, gọi AI sau"),@("UC-04","Tìm kiếm","Truy vấn nhiều collection","Từ khóa rỗng","Validate"),@("UC-20","Chatbot","Gọi AI","Timeout","Hiển thị lỗi"),@("UC-08","Artwork nổi bật","Truy vấn Artworks","Ảnh lớn","Nén/cache"));
    ExtraCode="firestore.collection(`"Lessons`").whereEqualTo(`"category`", category).get();"; ExtraExplain="Đoạn code minh họa truy vấn bài học theo danh mục. Kết quả trả về QuerySnapshot và được map sang model Lesson.";
    Dependencies=$commonDeps + "`nimplementation 'com.squareup.retrofit2:retrofit:2.9.0'`nimplementation 'com.squareup.retrofit2:converter-gson:2.9.0'";
    ApiConfig="Tạo API key AI, cấu hình trong .env hoặc backend proxy, không hard-code key trong source. Request có thể chứa text hoặc ảnh bài làm dạng Base64.";
    Conclusion="Module Học tập & Trực tuyến đáp ứng học vẽ, nộp bài, tìm kiếm, chatbot và khám phá tác phẩm. Firebase quản lý dữ liệu học tập; API AI hỗ trợ phản hồi thông minh."
}

$manh = $linh.Clone(); $manh.File="BaoCao_KyThuat_CaoDucManh.docx"; $manh.Name="Cao Đức Mạnh"; $manh.Module="Cộng đồng & Khám phá"; $manh.Role="Xây dựng feed, đăng bài, tương tác và livestream."; $manh.Scope="Bảng tin, bài viết, like/comment, phòng livestream."; $manh.Tech="Java, Android, Firebase, Firestore, Storage/Base64, ZegoCloud SDK";
$manh.UcRows=@(@("1","UC-15","Xem Livestream","Tham gia phòng live.","W_LiveList, ZegoCloud"),@("2","UC-16","Phát Livestream","Tạo và phát phòng live.","W_LiveHost, LiveRooms"),@("3","UC-05","Xem Bảng tin","Tải feed cộng đồng.","W_CommunityFeed, Posts"),@("4","UC-06","Đăng bài viết","Tạo post và upload ảnh.","W_PostCreate, Posts"),@("5","UC-07","Tương tác bài viết","Like/comment.","W_PostDetail, Comments"));
$manh.UiRows=@(@("CommunityFragment","Hiển thị feed","Post","Manage_Post"),@("CreatePostActivity","Tạo post","Content/Image","Manage_Post"),@("PostDetailActivity","Tương tác","Comment/Like","Manage_Comment"),@("LiveActivity","Livestream","Video/audio","Manage_LiveRoom"));
$manh.BizRows=@(@("Manage_Post","Quản lý post","createPost(), loadPosts()","Posts","Feed"),@("Manage_Comment","Bình luận","addComment()","Comments","Comment list"),@("Manage_LiveRoom","Livestream","createRoom(), joinRoom()","LiveRooms/Zego","Live room"));
$manh.ModelRows=@(@("Post","Bài viết","id,uid,content,imageUrl","Users, Comments"),@("Comment","Bình luận","postId,uid,content","Posts"),@("LiveRoom","Phòng live","roomId,hostId,status","Users"));
$manh.Flow="W_PostCreate`n    ↓`nManage_Post`n    ↓`nFirebase Storage/Base64`n    ↓`nCloud Firestore Posts`n    ↓`nW_CommunityFeed cập nhật bài mới";
$manh.Collections=@(@{Name="Posts";Fields=@(@("id","String","Mã bài","UC-05/06"),@("uid","String","Người đăng","Users"),@("content","String","Nội dung","Hiển thị"),@("imageUrl","String","Ảnh","Upload"),@("likesCount","int","Lượt thích","UC-07"));Explain="Posts lưu bài viết cộng đồng."},@{Name="Comments";Fields=@(@("id","String","Mã bình luận","UC-07"),@("postId","String","Bài viết","Posts"),@("uid","String","Người bình luận","Users"),@("content","String","Nội dung","Hiển thị"));Explain="Comments lưu bình luận theo bài viết."},@{Name="LiveRooms";Fields=@(@("roomId","String","Mã phòng","UC-15/16"),@("hostId","String","Người phát","Users"),@("status","String","live/ended","Lọc phòng"),@("title","String","Tiêu đề","Hiển thị"));Explain="LiveRooms lưu metadata phòng livestream."});
$manh.Models=@(@{Name="Post";Role="Đại diện bài viết.";Code="public class Post {`n    private String id, uid, content, imageUrl;`n    private int likesCount, commentsCount;`n    private long createdAt;`n    public Post() { }`n}";Explain="Post map với Posts. uid liên kết Users; likesCount/commentsCount dùng thống kê."},@{Name="LiveRoom";Role="Đại diện phòng live.";Code="public class LiveRoom {`n    private String roomId, hostId, title, status;`n    private int viewerCount;`n    public LiveRoom() { }`n}";Explain="roomId dùng làm liveID cho ZegoCloud."});
$manh.Functions=@(@{Name="createPost";Role="Tạo bài viết.";Code="public void createPost(String content, Uri imageUri) {`n    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();`n    String id = firestore.collection(`"Posts`").document().getId();`n    Post post = new Post(id, uid, content, uploadImage(imageUri), 0, 0, System.currentTimeMillis());`n    firestore.collection(`"Posts`").document(id).set(post);`n}";Explain="Hàm lấy uid, tạo id, upload ảnh và lưu Posts."},@{Name="toggleLike";Role="Like/bỏ like.";Code="public void toggleLike(String postId) {`n    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();`n    DocumentReference ref = firestore.collection(`"PostLikes`").document(postId + `_` + uid);`n    ref.get().addOnSuccessListener(doc -> { if (doc.exists()) ref.delete(); else ref.set(new PostLike(postId, uid)); });`n}";Explain="Dùng postId_uid để tránh like trùng."});
$manh.Uis=@(@{Name="W_PostCreate";Role="Nhận nội dung bài viết.";Code="btnPost.setOnClickListener(v -> {`n    managePost.createPost(edtContent.getText().toString(), selectedImageUri);`n});";Explain="Button gọi lớp xử lý tạo post."});
$manh.Api=@{Name="ZegoCloud SDK";Purpose="Phát/xem livestream.";Request="appID, appSign, userID, userName, liveID.";Response="Luồng video/audio.";Place="LiveActivity";Code="ZegoUIKitPrebuiltLiveStreamingConfig config = ZegoUIKitPrebuiltLiveStreamingConfig.host();`nZegoUIKitPrebuiltLiveStreamingFragment fragment = ZegoUIKitPrebuiltLiveStreamingFragment.newInstance(appID, appSign, userID, userName, liveID, config);";Explain="config host dùng cho người phát, audience dùng cho người xem. liveID là mã phòng."};
$manh.EvalRows=@(@("UC-05","Tải feed","Posts","Feed chậm","Phân trang"),@("UC-06","Tạo post","Thêm Posts","Ảnh lớn","Nén ảnh"),@("UC-07","Like/comment","Comments/PostLikes","Like trùng","postId_uid"),@("UC-15","Xem live","LiveRooms/Zego","Phòng ended","Kiểm tra status"),@("UC-16","Phát live","LiveRooms/Zego","Thiếu quyền","Xin quyền"));
$manh.ExtraCode="db.collection(`"Posts`").document(postId).update(`"likesCount`", FieldValue.increment(1));"; $manh.ExtraExplain="FieldValue.increment giúp cập nhật số lượt thích an toàn hơn khi nhiều người thao tác.";
$manh.Dependencies=$commonDeps + "`nimplementation 'com.github.ZEGOCLOUD:zego_uikit_prebuilt_live_streaming_android:+'"; $manh.ApiConfig="Tạo tài khoản ZegoCloud, lấy appID/appSign, thêm quyền camera/microphone/Internet và kiểm thử trên hai thiết bị."; $manh.Conclusion="Module Cộng đồng & Khám phá giúp người dùng chia sẻ, tương tác và học trực tuyến qua livestream."

$van = $linh.Clone(); $van.File="BaoCao_KyThuat_DangThiHongVan.docx"; $van.Name="Đặng Thị Hồng Vân"; $van.Module="Thử thách, Sự kiện & Dự án"; $van.Role="Quản lý project, artwork, challenge, submission, vote và event."; $van.Scope="Dự án cá nhân, thử thách, bài dự thi, bình chọn, lịch sự kiện."; $van.Tech="Java, Android, FirebaseAuth, Firestore, Storage/Base64";
$van.UcRows=@(@("1","UC-12","Quản lý Dự án & Tác phẩm","CRUD project/artwork.","Projects, Artworks"),@("2","UC-13","Tham gia Thử thách","Nộp bài và vote.","ChallengeSubmissions"),@("3","UC-14","Tạo Thử thách","Mentor tạo challenge.","Challenges"),@("4","UC-17","Xem Sự kiện","Tải lịch sự kiện.","Events"),@("5","UC-18","Tạo Sự kiện","Mentor tạo event.","Events"));
$van.UiRows=@(@("ProjectListActivity","Danh sách dự án","Project","Manage_Project"),@("ProjectDetailActivity","Chi tiết dự án","Artwork","Manage_Artwork"),@("ChallengeActivity","Thử thách","Challenge","Manage_Challenge"),@("CreateEventActivity","Tạo event","Event","Manage_Event"));
$van.BizRows=@(@("Manage_Project","Project","createProject(), loadProjects()","Projects","Project list"),@("Manage_Challenge","Challenge","submitChallenge(), vote()","Challenges/Submissions","Result"),@("Manage_Event","Event","createEvent(), loadEvents()","Events","Event list"));
$van.ModelRows=@(@("Project","Dự án","id,uid,name,status","Artworks"),@("Artwork","Tác phẩm","projectId,uid,imageUrl","Projects"),@("ChallengeSubmission","Bài dự thi","challengeId,uid,imageUrl","Challenges"),@("Event","Sự kiện","id,title,startTime","Users"));
$van.Flow="W_ChallengeDetail`n    ↓`nManage_Challenge`n    ↓`nFirebase Storage/Base64`n    ↓`nCloud Firestore ChallengeSubmissions`n    ↓`nW_ChallengeList cập nhật";
$van.Collections=@(@{Name="Projects";Fields=@(@("id","String","Mã dự án","UC-12"),@("uid","String","Chủ dự án","Users"),@("name","String","Tên","Hiển thị"),@("status","String","Trạng thái","Theo dõi"));Explain="Projects lưu dự án cá nhân."},@{Name="ChallengeSubmissions";Fields=@(@("id","String","Mã bài","UC-13"),@("challengeId","String","Thử thách","Challenges"),@("uid","String","Người nộp","Users"),@("voteCount","int","Bình chọn","Xếp hạng"));Explain="ChallengeSubmissions lưu bài dự thi."},@{Name="Events";Fields=@(@("id","String","Mã sự kiện","UC-17/18"),@("title","String","Tên","Hiển thị"),@("startTime","long","Thời gian","Lịch"),@("authorId","String","Người tạo","Users"));Explain="Events lưu lịch trình/sự kiện."});
$van.Models=@(@{Name="Project";Role="Đại diện dự án.";Code="public class Project {`n    private String id, uid, name, status;`n    private int artworkCount;`n    public Project() { }`n}";Explain="uid liên kết Users, artworkCount thống kê tác phẩm."},@{Name="ChallengeSubmission";Role="Đại diện bài dự thi.";Code="public class ChallengeSubmission {`n    private String id, challengeId, uid, imageUrl;`n    private int voteCount;`n    public ChallengeSubmission() { }`n}";Explain="challengeId liên kết Challenges, uid liên kết Users."});
$van.Functions=@(@{Name="createProject";Role="Tạo project.";Code="public void createProject(String name) {`n    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();`n    String id = firestore.collection(`"Projects`").document().getId();`n    Project p = new Project(id, uid, name, `"in_progress`", 0);`n    firestore.collection(`"Projects`").document(id).set(p);`n}";Explain="Tạo project mới theo uid hiện tại."},@{Name="submitChallenge";Role="Nộp bài thử thách.";Code="public void submitChallenge(String challengeId, Uri imageUri) {`n    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();`n    String id = firestore.collection(`"ChallengeSubmissions`").document().getId();`n    ChallengeSubmission s = new ChallengeSubmission(id, challengeId, uid, uploadImage(imageUri), 0);`n    firestore.collection(`"ChallengeSubmissions`").document(id).set(s);`n}";Explain="Lưu bài dự thi vào ChallengeSubmissions."});
$van.Uis=@(@{Name="W_Project";Role="Tạo dự án.";Code="btnCreateProject.setOnClickListener(v -> manageProject.createProject(edtProjectName.getText().toString()));";Explain="Giao diện lấy tên dự án và gọi Manage_Project."});
$van.Api=$null; $van.EvalRows=@(@("UC-12","CRUD project","Projects/Artworks","Dữ liệu mồ côi","Xóa cascade"),@("UC-13","Nộp/vote","Submissions/Votes","Vote trùng","voteId"),@("UC-14","Tạo challenge","Challenges","Deadline sai","Timestamp"),@("UC-17","Xem event","Events","Nhiều dữ liệu","Lọc ngày"),@("UC-18","Tạo event","Events","Thiếu quyền","Check role"));
$van.ExtraCode="firestore.collection(`"Projects`").whereEqualTo(`"uid`", uid).get();"; $van.ExtraExplain="Truy vấn Projects theo uid để chỉ hiển thị dự án của người dùng hiện tại."; $van.Dependencies=$commonDeps; $van.ApiConfig="Module này không yêu cầu cấu hình API ngoài."; $van.Conclusion="Module Thử thách, Sự kiện & Dự án quản lý tiến độ sáng tạo, thi thử thách và lịch hoạt động."

$vinh = $linh.Clone(); $vinh.File="BaoCao_KyThuat_VuQuangVinh.docx"; $vinh.Name="Vũ Quang Vinh"; $vinh.Module="Kiến trúc Lõi & Công cụ Sáng tạo"; $vinh.Role="Xây dựng auth, profile, notifications và canvas."; $vinh.Scope="Đăng nhập, đăng ký, profile, thông báo, vẽ canvas, lưu artwork."; $vinh.Tech="Java, Android Canvas API, FirebaseAuth, Firestore, Storage/Base64";
$vinh.UcRows=@(@("1","UC-10","Vẽ Canvas","Vẽ, zoom, undo/redo, lưu.","DrawingActivity, ZoomDrawingView"),@("2","UC-01","Đăng nhập/Đăng ký","FirebaseAuth.","LoginActivity, RegisterActivity"),@("3","UC-02","Profile","Cập nhật hồ sơ/avatar.","Users"),@("4","UC-03","Thông báo","Tải và đánh dấu đã đọc.","Notifications"));
$vinh.UiRows=@(@("LoginActivity","Đăng nhập","Email/password","Manage_User"),@("RegisterActivity","Đăng ký","Email/password","Manage_User"),@("ProfileActivity","Hồ sơ","User","Manage_Profile"),@("NotificationsActivity","Thông báo","Notification","Manage_Notification"),@("DrawingActivity","Canvas","Bitmap/stroke","Manage_Canvas"));
$vinh.BizRows=@(@("Manage_User","Auth","login(), register()","FirebaseAuth/Users","Session"),@("Manage_Profile","Profile","updateProfile()","Users","Profile"),@("Manage_Notification","Notification","markAsRead()","Notifications","List"),@("Manage_Canvas","Canvas","saveArtwork()","Artworks","Artwork"));
$vinh.ModelRows=@(@("User","Người dùng","uid,email,fullName","All modules"),@("Notification","Thông báo","targetUserId,type,isRead","Users"),@("Artwork","Tác phẩm","uid,imageUrl","Users"));
$vinh.Flow="W_Canvas`n    ↓`nZoomDrawingView / Manage_Canvas`n    ↓`nBitmap export / Base64`n    ↓`nCloud Firestore Artworks`n    ↓`nProfile/Project hiển thị tác phẩm";
$vinh.Collections=@(@{Name="Users";Fields=@(@("uid","String","Mã user","UC-01/02"),@("email","String","Email","Auth"),@("fullName","String","Tên","Profile"),@("avatarUrl","String","Avatar","Profile"));Explain="Users lưu hồ sơ và liên kết toàn hệ thống."},@{Name="Notifications";Fields=@(@("id","String","Mã thông báo","UC-03"),@("targetUserId","String","Người nhận","Query"),@("type","String","Loại","Điều hướng"),@("isRead","boolean","Đã đọc","Update"));Explain="Notifications lưu thông báo người dùng."},@{Name="Artworks";Fields=@(@("id","String","Mã tác phẩm","UC-10"),@("uid","String","Người vẽ","Users"),@("imageUrl","String","Ảnh","Hiển thị"),@("updatedAt","long","Cập nhật","Sắp xếp"));Explain="Artworks lưu bản vẽ canvas."});
$vinh.Models=@(@{Name="User";Role="Hồ sơ user.";Code="public class User {`n    private String uid, email, fullName, avatarUrl, role;`n    public User() { }`n}";Explain="uid lấy từ FirebaseAuth và dùng liên kết mọi collection."},@{Name="Notification";Role="Thông báo.";Code="public class Notification {`n    private String id, targetUserId, senderId, type, targetId;`n    private boolean isRead;`n    public Notification() { }`n}";Explain="targetUserId dùng lọc thông báo, isRead đánh dấu đã đọc."});
$vinh.Functions=@(@{Name="registerUser";Role="Đăng ký.";Code="auth.createUserWithEmailAndPassword(email, password)`n    .addOnSuccessListener(r -> {`n        String uid = r.getUser().getUid();`n        firestore.collection(`"Users`").document(uid).set(new User(uid, email, fullName));`n    });";Explain="FirebaseAuth tạo tài khoản, uid dùng làm document id Users."},@{Name="saveArtwork";Role="Lưu canvas.";Code="Bitmap bitmap = drawingView.exportBitmap();`nString imageUrl = convertBitmapToBase64(bitmap);`nfirestore.collection(`"Artworks`").document(id).set(new Artwork(id, uid, imageUrl));";Explain="Lấy bitmap từ ZoomDrawingView, chuyển ảnh và lưu Artworks."});
$vinh.Uis=@(@{Name="W_Canvas";Role="Vẽ và lưu.";Code="btnSave.setOnClickListener(v -> {`n    Bitmap bitmap = drawingView.exportBitmap();`n    manageCanvas.saveArtwork(bitmap, edtTitle.getText().toString());`n});";Explain="Giao diện gọi exportBitmap rồi lưu artwork."});
$vinh.Api=$null; $vinh.EvalRows=@(@("UC-10","Vẽ/lưu","Artworks","Tốn RAM","Nén ảnh"),@("UC-01","Auth","FirebaseAuth/Users","Sai mật khẩu","Hiển thị lỗi"),@("UC-02","Profile","Users","Ảnh lỗi","Validate"),@("UC-03","Notification","Notifications","Mất mạng","Callback lỗi"));
$vinh.ExtraCode="public Bitmap exportBitmap() {`n    Bitmap out = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);`n    Canvas c = new Canvas(out);`n    draw(c);`n    return out;`n}"; $vinh.ExtraExplain="exportBitmap kết xuất toàn bộ canvas thành Bitmap để lưu dưới dạng ảnh."; $vinh.Dependencies=$commonDeps; $vinh.ApiConfig="Module này không yêu cầu cấu hình API ngoài."; $vinh.Conclusion="Module lõi cung cấp nền tảng tài khoản, hồ sơ, thông báo và công cụ vẽ canvas cho toàn bộ App-Draw."

foreach ($cfg in @($linh,$manh,$van,$vinh)) {
    $body = AddGenericReport $cfg
    SaveDocx (Join-Path (Get-Location) $cfg.File) $body
}

Get-ChildItem BaoCao_KyThuat_*.docx | Select-Object Name,Length

