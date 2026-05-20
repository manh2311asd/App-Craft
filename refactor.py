import os
import re
import shutil

moves = {
    "ChallengeEntryListActivity": "challenge",
    "ChallengeGradingActivity": "challenge",
    "CreateChallengeActivity": "challenge",
    "CreateProjectActivity": "project",
    "DoingProjectDetailActivity": "project",
    "EditProfileActivity": "profile",
    "ProfileActivity": "profile",
    "EventRegistrationSuccessActivity": "event",
    "TicketActivity": "event",
    "HomeworkActivity": "explore",
    "NotificationAdapter": "notification",
    "NotificationsActivity": "notification",
    "PostDetailActivity": "community",
    "ZoomDrawingView": "drawing"
}

base_dir = "app/src/main/java/com/example/appdraw"
manifest_path = "app/src/main/AndroidManifest.xml"
layout_dir = "app/src/main/res/layout"

def update_file_content(filepath, replacements):
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
            
        new_content = content
        for old, new in replacements:
            new_content = new_content.replace(old, new)
            
        if new_content != content:
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(new_content)
    except Exception as e:
        print(f"Error updating {filepath}: {e}")

# 1. Create dirs and move files, update their package
for cls, pkg in moves.items():
    src_file = os.path.join(base_dir, f"{cls}.java")
    target_dir = os.path.join(base_dir, pkg)
    target_file = os.path.join(target_dir, f"{cls}.java")
    
    if os.path.exists(src_file):
        os.makedirs(target_dir, exist_ok=True)
        shutil.move(src_file, target_file)
        
        # Update package
        with open(target_file, 'r', encoding='utf-8') as f:
            content = f.read()
        content = content.replace("package com.example.appdraw;", f"package com.example.appdraw.{pkg};")
        with open(target_file, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Moved {cls} to {pkg}")

# 2. Update imports and usages in all java files
java_files = []
for root, _, files in os.walk(base_dir):
    for f in files:
        if f.endswith('.java'):
            java_files.append(os.path.join(root, f))

for jf in java_files:
    try:
        with open(jf, 'r', encoding='utf-8') as f:
            content = f.read()
            
        new_content = content
        for cls, pkg in moves.items():
            # Update explicit imports
            new_content = new_content.replace(f"import com.example.appdraw.{cls};", f"import com.example.appdraw.{pkg}.{cls};")
            
            # If a class in root package used this class, it needs an import now
            # Only do this if we are not in the new package
            current_pkg = re.search(r"package\s+([a-zA-Z0-9_.]+);", new_content)
            if current_pkg:
                cp = current_pkg.group(1)
                # If current package is com.example.appdraw, and it uses Cls, add import
                # A simple regex to check if cls is used as a word
                if cp == "com.example.appdraw" and re.search(rf"\b{cls}\b", new_content):
                    if f"import com.example.appdraw.{pkg}.{cls};" not in new_content:
                        # inject import after package
                        new_content = new_content.replace(f"package {cp};", f"package {cp};\n\nimport com.example.appdraw.{pkg}.{cls};", 1)
        
        if new_content != content:
            with open(jf, 'w', encoding='utf-8') as f:
                f.write(new_content)
    except Exception as e:
        print(f"Error java file {jf}: {e}")

# 3. Update AndroidManifest
manifest_replacements = []
for cls, pkg in moves.items():
    manifest_replacements.append((f'android:name=".{cls}"', f'android:name=".{pkg}.{cls}"'))
    manifest_replacements.append((f'android:name="com.example.appdraw.{cls}"', f'android:name="com.example.appdraw.{pkg}.{cls}"'))
update_file_content(manifest_path, manifest_replacements)

# 4. Update Layouts
for root, _, files in os.walk(layout_dir):
    for f in files:
        if f.endswith('.xml'):
            lp = os.path.join(root, f)
            reps = []
            for cls, pkg in moves.items():
                reps.append((f'tools:context=".{cls}"', f'tools:context=".{pkg}.{cls}"'))
                reps.append((f'tools:context="com.example.appdraw.{cls}"', f'tools:context="com.example.appdraw.{pkg}.{cls}"'))
                reps.append((f'<com.example.appdraw.{cls}', f'<com.example.appdraw.{pkg}.{cls}'))
            update_file_content(lp, reps)

print("Refactoring complete.")
