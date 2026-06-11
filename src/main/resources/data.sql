-- Sample data for QuanLyBanHang (PostgreSQL)

-- Xóa dữ liệu cũ nếu có (theo thứ tự phụ thuộc)
TRUNCATE import_receipt_details, import_receipts, order_details, orders, products, categories, suppliers, customers, users RESTART IDENTITY;

-- 1. Thêm Nhân viên (Mật khẩu mặc định của tất cả là '123456', được hash bằng BCrypt)
-- Hash: $2a$10$w8.d8vHwR4lT7D/BfX/y.O95M8tP1p3XfP0R372bXvNpeY1xZ6q3e
INSERT INTO users (username, password, full_name, email, phone, role, status) VALUES
('nv01', '$2a$10$w8.d8vHwR4lT7D/BfX/y.O95M8tP1p3XfP0R372bXvNpeY1xZ6q3e', 'Trần Thị Thu Ngân', 'nv01@qlbh.com', '0912345678', 'EMPLOYEE', TRUE),
('nv02', '$2a$10$w8.d8vHwR4lT7D/BfX/y.O95M8tP1p3XfP0R372bXvNpeY1xZ6q3e', 'Lê Văn Bán Hàng', 'nv02@qlbh.com', '0922334455', 'EMPLOYEE', TRUE);

-- 2. Thêm Nhóm sản phẩm
INSERT INTO categories (name, description) VALUES
('Điện thoại & Máy tính bảng', 'Các dòng điện thoại thông minh, máy tính bảng mới nhất'),
('Laptop & Phụ kiện', 'Máy tính xách tay và chuột, bàn phím, tai nghe'),
('Thiết bị âm thanh', 'Tai nghe bluetooth, loa di động, loa soundbar'),
('Thiết bị đeo thông minh', 'Đồng hồ thông minh, vòng đeo tay sức khỏe');

-- 3. Thêm Nhà cung cấp
INSERT INTO suppliers (code, name, phone, email, address) VALUES
('NCC001', 'Tổng kho Công nghệ Hùng Cường', '0243999888', 'contact@hungcuongtech.com', '123 Đường Láng, Đống Đa, Hà Nội'),
('NCC002', 'Công ty Phân phối Số Dầu khí (PSD)', '0283822666', 'info@psd.com.vn', '35 Nguyễn Huệ, Quận 1, TP. Hồ Chí Minh'),
('NCC003', 'Nhà phân phối linh kiện Digiworld', '0283929000', 'contact@digiworld.com.vn', '195 Nguyễn Thái Học, Quận 1, TP. Hồ Chí Minh');

-- 4. Thêm Sản phẩm
INSERT INTO products (code, name, category_id, purchase_price, sale_price, stock_quantity, unit, image_path, status) VALUES
('SP001', 'iPhone 15 Pro Max 256GB', 1, 26000000.00, 29990000.00, 15, 'Chiếc', '/images/iphone15.png', TRUE),
('SP002', 'Samsung Galaxy S24 Ultra', 1, 24000000.00, 27490000.00, 12, 'Chiếc', '/images/s24u.png', TRUE),
('SP003', 'iPad Pro M2 11-inch WiFi', 1, 18500000.00, 20990000.00, 8, 'Chiếc', '/images/ipadpro.png', TRUE),
('SP004', 'MacBook Air M2 8GB 256GB', 2, 21000000.00, 23990000.00, 10, 'Chiếc', '/images/macbookair.png', TRUE),
('SP005', 'Chuột Logitech MX Master 3S', 2, 1800000.00, 2290000.00, 30, 'Cái', '/images/mxmaster3s.png', TRUE),
('SP006', 'Tai nghe Apple AirPods Pro 2', 3, 4500000.00, 5290000.00, 20, 'Hộp', '/images/airpodspro2.png', TRUE),
('SP007', 'Loa Marshall Emberton II', 3, 3100000.00, 3790000.00, 15, 'Cái', '/images/emberton2.png', TRUE),
('SP008', 'Apple Watch Series 9 GPS 41mm', 4, 8500000.00, 9490000.00, 14, 'Chiếc', '/images/aw9.png', TRUE);

-- 5. Thêm Khách hàng
INSERT INTO customers (code, name, phone, email, address, loyalty_points) VALUES
('KH001', 'Khách vãng lai', '0000000000', 'khachhang@qmbh.com', 'Tại quầy', 0),
('KH002', 'Trần Minh Hoàng', '0966778899', 'hoangtm@gmail.com', '456 Lê Lợi, Quận Gò Vấp, TP. HCM', 120),
('KH003', 'Phạm Thanh Thảo', '0977889900', 'thaopt@yahoo.com', '789 Trần Hưng Đạo, Quận 5, TP. HCM', 350),
('KH004', 'Lê Anh Tuấn', '0988990011', 'tuanla@hotmail.com', '101 Hai Bà Trưng, Quận 3, TP. HCM', 50);

-- 6. Thêm một số Hóa đơn bán hàng mẫu
INSERT INTO orders (code, user_id, customer_id, order_date, total_amount, discount, final_amount, payment_method, notes) VALUES
('HD001', 2, 2, '2026-06-01 10:15:00', 32280000.00, 100000.00, 32180000.00, 'TRANSFER', 'Khách chuyển khoản ngân hàng'),
('HD002', 2, 3, '2026-06-02 14:30:00', 5290000.00, 0.00, 5290000.00, 'CASH', 'Khách trả tiền mặt'),
('HD003', 3, 1, '2026-06-03 18:45:00', 2290000.00, 0.00, 2290000.00, 'CASH', 'Mua chuột lẻ');

-- Chi tiết hóa đơn
INSERT INTO order_details (order_id, product_id, quantity, price, total_price) VALUES
(1, 1, 1, 29990000.00, 29990000.00), -- 1 iPhone 15 Pro Max
(1, 5, 1, 2290000.00, 2290000.00),   -- 1 Chuột Logitech
(2, 6, 1, 5290000.00, 5290000.00),   -- 1 Tai nghe AirPods Pro 2
(3, 5, 1, 2290000.00, 2290000.00);   -- 1 Chuột Logitech

-- 7. Thêm Phiếu nhập kho mẫu
INSERT INTO import_receipts (code, user_id, supplier_id, import_date, total_amount) VALUES
('PN001', 1, 1, '2026-05-25 08:00:00', 390000000.00),
('PN002', 1, 2, '2026-05-26 09:30:00', 165000000.00);

-- Chi tiết phiếu nhập
INSERT INTO import_receipt_details (receipt_id, product_id, quantity, price, total_price) VALUES
(1, 1, 15, 26000000.00, 390000000.00), -- Nhập 15 iPhone 15 Pro Max
(2, 3, 5, 18500000.00, 92500000.00),   -- Nhập 5 iPad Pro
(2, 4, 3, 21000000.00, 63000000.00),   -- Nhập 3 MacBook Air
(2, 5, 5, 1800000.00, 9000000.00);     -- Nhập 5 Chuột Logitech
