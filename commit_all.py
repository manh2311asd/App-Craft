import os
import subprocess

commands = [
    # First re-commit gitignore
    'git add .gitignore',
    'git commit -m "Add project reports (*.md, *.docx) to gitignore"',
    
    # Now commit the individual directories
    'git add app/src/main/java/com/example/appdraw/challenge/',
    'git commit -m "Tính năng Thử thách - Phụ trách: Cao Đức Mạnh & Đặng Thị Hồng Vân"',
    'git add app/src/main/java/com/example/appdraw/community/',
    'git commit -m "Tính năng Cộng đồng - Phụ trách: Cao Đức Mạnh"',
    'git add app/src/main/java/com/example/appdraw/drawing/',
    'git commit -m "Tính năng Vẽ Canvas - Phụ trách: Vũ Quang Vinh"',
    'git add app/src/main/java/com/example/appdraw/event/',
    'git commit -m "Tính năng Sự kiện - Phụ trách: Đặng Thị Hồng Vân"',
    'git add app/src/main/java/com/example/appdraw/explore/',
    'git commit -m "Tính năng Khám phá & Học tập - Phụ trách: Lê Thùy Linh"',
    'git add app/src/main/java/com/example/appdraw/notification/',
    'git commit -m "Tính năng Thông báo - Phụ trách: Cao Đức Mạnh"',
    'git add app/src/main/java/com/example/appdraw/profile/',
    'git commit -m "Tính năng Hồ sơ cá nhân - Phụ trách: Vũ Quang Vinh"',
    'git add app/src/main/java/com/example/appdraw/project/',
    'git commit -m "Tính năng Quản lý Dự án - Phụ trách: Vũ Quang Vinh"',
    
    # Any other java files or resources
    'git add .',
    'git commit -m "Cập nhật Manifest, giao diện (UI) và xóa file rác"',
    'git push -f'
]

# Set environment variable to avoid charset issues if possible
my_env = os.environ.copy()
my_env["PYTHONIOENCODING"] = "utf-8"

for cmd in commands:
    subprocess.run(cmd, shell=True, env=my_env)
