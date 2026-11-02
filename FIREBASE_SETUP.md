# Cấu hình Firebase cho UTHTodo

Mã nguồn đã có sẵn lớp `FirebaseAuthRepository`, Email/Password và Google Sign-In. Phần duy nhất cần làm thủ công là tạo project Firebase và đặt thông tin Android của project vào ứng dụng.

## Các bước bắt buộc

1. Vào Firebase Console, tạo project mới.
2. Add Android app với package name: `com.uthtodo.app`.
3. Tải `google-services.json` và đặt vào thư mục `app/`.
4. Trong Firebase Authentication → Sign-in method, bật **Email/Password** và **Google**.
5. Trong Project settings → Your apps, lấy **Web client ID** rồi thay giá trị `REPLACE_WITH_FIREBASE_WEB_CLIENT_ID` trong `app/src/main/res/values/strings.xml`.
6. Nếu dùng Google Sign-In trên thiết bị thật, thêm SHA-1/SHA-256 của signing key vào Firebase Project settings → Your apps.
7. Sync Gradle. Dự án đã khai báo sẵn Google Services Gradle plugin và sẽ tự áp dụng plugin khi `app/google-services.json` tồn tại. Sau đó chạy app, chọn **Tạo tài khoản** để tạo tài khoản test rồi đăng nhập.

Nếu chưa thực hiện các bước trên, app vẫn mở được giao diện đăng nhập nhưng không thể đồng bộ nhiệm vụ. App không còn chế độ demo/khách; người dùng phải đăng nhập Firebase để vào workflow chính.

## Firestore cho nhiệm vụ

Workflow mới lưu dữ liệu tại `users/{uid}/tasks/{taskId}`. Sau khi tạo Firestore Database trong Firebase Console, hãy vào **Firestore Database → Rules** và dán nội dung file `firestore.rules` ở thư mục gốc project, sau đó nhấn **Publish**. Không dùng Test mode khi đưa app ra ngoài vì Test mode cho phép truy cập dữ liệu quá rộng.
