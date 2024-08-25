# Ứng dụng Quản lý Nhân viên

Ứng dụng này được xây dựng để quản lý thông tin nhân viên bao gồm tên, năm sinh và quê quán. Ứng dụng hỗ trợ các tính năng như tìm kiếm nhân viên, lọc theo năm sinh, quê quán, và thêm, chỉnh sửa, xóa nhân viên.

## Các tính năng chính

### 1. Hiển thị danh sách nhân viên

### 2. Tìm kiếm nhân viên

### 3. Lọc nhân viên theo năm sinh hoặc quê quán

### 4. Thêm mới và chỉnh sửa nhân viên

### 5. Xóa nhân viên

## Cài đặt và sử dụng

### 1. Cài đặt môi trường phát triển
- Đảm bảo rằng bạn đã cài đặt Android Studio phiên bản mới nhất và các công cụ cần thiết để phát triển ứng dụng Android.

### 2. Clone dự án
- Sử dụng Git để clone dự án về máy:
    ```bash
    git clone https://github.com/kouhoang/ReactiveEx.git
    ```

### 3. Mở dự án trong Android Studio
- Mở Android Studio và chọn "Open an existing project", sau đó điều hướng đến thư mục dự án đã clone.

### 4. Chạy ứng dụng
- Chọn thiết bị ảo hoặc thiết bị thực tế và nhấn "Run" để xây dựng và chạy ứng dụng.

## Cơ sở dữ liệu

- Ứng dụng sử dụng SQLite làm cơ sở dữ liệu. Cơ sở dữ liệu được quản lý bởi lớp `DatabaseHelper`, với một bảng duy nhất là `staff` lưu trữ thông tin nhân viên.

