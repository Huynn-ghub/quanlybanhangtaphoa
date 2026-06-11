# Quản Lý Bán Hàng

Ứng dụng quản lý bán hàng desktop viết bằng **Java Swing**, kết nối **PostgreSQL**. Dự án được xây dựng phục vụ mục đích học tập, áp dụng mô hình **MVC** và thao tác CRUD với cơ sở dữ liệu quan hệ.

## Tính năng

- Đăng nhập, phân quyền ADMIN / EMPLOYEE (mã hóa mật khẩu BCrypt)
- Quản lý sản phẩm, danh mục, tồn kho
- Lập hóa đơn bán hàng, in PDF
- Quản lý nhập kho từ nhà cung cấp
- Quản lý khách hàng, nhân viên
- Thống kê doanh thu với biểu đồ
- Xuất báo cáo Excel

## Công nghệ sử dụng

| Thành phần | Công nghệ |
|---|---|
| Ngôn ngữ | Java 17 |
| Giao diện | Java Swing + FlatLaf 3.4.1 |
| Cơ sở dữ liệu | PostgreSQL |
| Build tool | Maven |
| Thư viện | postgresql, jbcrypt, jfreechart, poi, openpdf |

## Cài đặt

**Yêu cầu:** JDK 17+, Maven 3.8+, PostgreSQL 14+

**1. Clone dự án**
```bash
git clone https://github.com/Huynn-ghub/quanlybanhang.git
cd quanlybanhang
```

**2. Tạo database**
```bash
psql -U postgres -c "CREATE DATABASE quanlybanhang;"
psql -U postgres -d quanlybanhang -f src/main/resources/schema.sql
psql -U postgres -d quanlybanhang -f src/main/resources/data.sql
```

**3. Cấu hình kết nối**
```bash
cp src/main/resources/db.properties.example src/main/resources/db.properties
```
Mở `db.properties` và điền thông tin kết nối thực tế.

**4. Chạy ứng dụng**
```bash
mvn clean compile exec:java -Dexec.mainClass="com.quanlybanhang.App"
```
Hoặc mở bằng IntelliJ IDEA và chạy `App.java`.

## Cấu trúc dự án

```
src/main/java/com/quanlybanhang/
├── App.java            # Entry point
├── config/             # Kết nối database
├── model/              # Entity classes
├── dao/                # Truy vấn database
├── controller/         # Business logic
├── view/               # Giao diện Swing
└── util/               # Tiện ích (in hóa đơn PDF)
```

## Lưu ý

> File `db.properties` chứa thông tin nhạy cảm, đã được thêm vào `.gitignore` và **không được commit lên git**. Sử dụng file `db.properties.example` làm mẫu.

## License

Dự án được phát triển phục vụ mục đích học tập.
