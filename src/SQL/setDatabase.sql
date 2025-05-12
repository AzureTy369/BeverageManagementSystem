-- Sử dụng cơ sở dữ liệu beveragemanagementsystem
USE beveragemanagementsystem;

-- Tắt kiểm tra khóa ngoại
SET FOREIGN_KEY_CHECKS = 0;

-- Xóa dữ liệu từ tất cả các bảng
DELETE FROM chitiethoadon;
DELETE FROM hoadon;
DELETE FROM chitietphieunhap;
DELETE FROM phieunhap;
DELETE FROM chitietkhuyenmai;
DELETE FROM khuyenmai;
DELETE FROM chitietsanpham;
DELETE FROM sanpham;
DELETE FROM nhanvien;
DELETE FROM nhacungcap;
DELETE FROM loaisanpham;
DELETE FROM customer;
DELETE FROM loai;
DELETE FROM chucvu;

-- Bật lại kiểm tra khóa ngoại
SET FOREIGN_KEY_CHECKS = 1;
-- DEMO INSERT

I-- Dumping data for table beveragemanagementsystem.chucvu
INSERT INTO `chucvu` (`MaChucVu`, `TenChucVu`, `Luong`) VALUES
  ('CV001', 'Quản trị viên', 20000000.00),
  ('CV002', 'Quản lý', 15000000.00),
  ('CV003', 'Nhân viên bán hàng', 8000000.00),
  ('CV004', 'Nhân viên kho', 7000000.00),
  ('CV005', 'Kế toán', 10000000.00);

-- Dumping data for table beveragemanagementsystem.loai
INSERT INTO `loai` (`MaLoai`, `TenLoai`) VALUES
  ('L001', 'Nước ngọt'),
  ('L002', 'Trà'),
  ('L003', 'Cà phê'),
  ('L004', 'Nước suối'),
  ('L005', 'Sữa');

-- Dumping data for table beveragemanagementsystem.customer
INSERT INTO `customer` (`customerID`, `firstname`, `lastname`, `address`, `phone`) VALUES
('CTM0000001', 'Nguyen Hoang', 'Hai', 'Ha Noi', '0782748863'),
('CTM0000002', 'Bui Thi Khanh', 'Ha', 'Can Tho', '0912345678'),
('CTM0000003', 'Nguyen Van', 'Hieu', 'Ho Chi Minh', '0923456789'),
('CTM0000004', 'Nguyen Hoang Dan', 'Ngoc', 'Buon Ma Thuot', '0934567890'),
('CTM0000005', 'Truong Kim', 'Anh', 'Buon Ma Thuot', '0945678901'),
('CTM0000006', 'Mai Bao', 'Ngoc', 'Buon Ma Thuot', '0956789012'),
('CTM0000007', 'Nguyen Thi Thanh', 'Tuyen', 'Hau Giang', '0967890123'),
('CTM0000008', 'Nguyen Xuan', 'Thanh', 'Ha Noi', '0978901234'),
('CTM0000009', 'Bui Quang Minh', 'Hieu', 'Ho Chi Minh', '0989012345'),
('CTM0000010', 'Nguyen Khac', 'Nhu', 'Bac Lieu', '0990123456');

-- Dumping data for table beveragemanagementsystem.khuyenmai
INSERT INTO `khuyenmai` (`MaKhuyenMai`, `TenKhuyenMai`, `NgayBatDau`, `NgayKetThuc`) VALUES
  ('KM001', 'Khai trương', '2020-03-26 00:00:00', '2020-04-26 23:59:59'),
  ('KM002', '30/4 - 1/5', '2021-04-30 00:00:00', '2021-05-01 23:59:59'),
  ('KM003', 'Black Friday', '2022-11-25 00:00:00', '2025-11-26 23:59:59'),
  ('KM004', 'Giáng sinh', '2023-12-25 00:00:00', '2023-12-31 23:59:59'),
  ('KM005', 'Tết Nguyên Đán', '2024-02-10 00:00:00', '2024-02-14 23:59:59'),
  ('KM006', 'Quốc khánh 2/9', '2024-09-01 00:00:00', '2024-09-30 23:59:59');

-- Dumping data for table beveragemanagementsystem.loaisanpham
INSERT INTO `loaisanpham` (`MaLoaiSP`, `TenLoaiSP`, `MoTa`) VALUES
  ('LSP001', 'Nước giải khát', 'Các loại đồ uống giải khát'),
  ('LSP002', 'Trà đóng chai', 'Các loại trà đóng chai'),
  ('LSP003', 'Cà phê đóng chai', 'Các loại cà phê đóng chai'),
  ('LSP004', 'Nước suối', 'Các loại nước suối đóng chai'),
  ('LSP005', 'Sữa tươi', 'Các loại sữa tươi đóng hộp');

-- Dumping data for table beveragemanagementsystem.nhacungcap
INSERT INTO `nhacungcap` (`MaNhaCungCap`, `TenNhaCungCap`, `SoDienThoai`, `DiaChi`, `Email`) VALUES
  ('NCC001', 'Coca-Cola Vietnam', '0987654321', 'Ho Chi Minh', 'contact@cocacola.com.vn'),
  ('NCC002', 'Pepsi Vietnam', '0912345678', 'Ha Noi', 'info@pepsi.com.vn'),
  ('NCC003', 'TH True Milk', '0923456789', 'Nghe An', 'thtrue@thmilk.vn'),
  ('NCC004', 'Lavie Vietnam', '0934567890', 'Long An', 'lavie@vietnamwater.com'),
  ('NCC005', 'Nescafe Vietnam', '0945678901', 'Ho Chi Minh', 'nescafe@nestle.com.vn'),
  ('NCC006', 'Lipton Vietnam', '0956789012', 'Ha Noi', 'lipton@unilever.com.vn'),
  ('NCC007', 'Vinamilk', '0967890123', 'Ho Chi Minh', 'vinamilk@vinamilk.com.vn'),
  ('NCC008', 'Aquafina Vietnam', '0978901234', 'Da Nang', 'aquafina@pepsi.com.vn');

-- Dumping data for table beveragemanagementsystem.nhanvien
INSERT INTO `nhanvien` (`MaNhanVien`, `TenTaiKhoan`, `MatKhau`, `Ho`, `Ten`, `MaChucVu`, `SoDienThoai`) VALUES
  ('NV001', 'admin', 'admin123', 'Admin', 'User', 'CV001', '0987654321'),
  ('NV002', 'nhh1205', 'nhh1205@Aa', 'Nguyen Hoang', 'Hai', 'CV002', '0782748863'),
  ('NV003', 'ntt1903', 'ntt1903@Aa', 'Nguyen Thanh', 'Truc', 'CV003', '0912345678'),
  ('NV004', 'vht2405', 'vht2405@Aa', 'Vo Huyen', 'Tran', 'CV003', '0923456789'),
  ('NV005', 'dtv0104', 'dtv0104@Aa', 'Dao Thuy', 'Vy', 'CV004', '0934567890'),
  ('NV006', 'tgl2011', 'tgl2011@Aa', 'Trinh Gia', 'Linh', 'CV004', '0945678901'),
  ('NV007', 'nmd2010', 'nmd2010@Aa', 'Nguyen Minh', 'Dat', 'CV005', '0956789012'),
  ('NV008', 'nbu0503', 'nbu0503@Aa', 'Nguyen Bao', 'Uyen', 'CV005', '0967890123');

-- Dumping data for table beveragemanagementsystem.sanpham
INSERT INTO `sanpham` (`MaSanPham`, `TenSanPham`, `MaLoai`, `DonViTinh`, `SoLuongTon`) VALUES
  ('SP001', 'Coca-Cola', 'L001', 'Chai', 100),
  ('SP002', 'Pepsi', 'L001', 'Chai', 100),
  ('SP003', 'TH True Milk', 'L005', 'Hộp', 80),
  ('SP004', 'Lavie', 'L004', 'Chai', 150),
  ('SP005', 'Nescafe Gold', 'L003', 'Chai', 60),
  ('SP006', 'Lipton Ice Tea', 'L002', 'Chai', 90),
  ('SP007', 'Vinamilk Sữa Tươi', 'L005', 'Hộp', 70),
  ('SP008', 'Aquafina', 'L004', 'Chai', 120),
  ('SP009', 'Sprite', 'L001', 'Chai', 100),
  ('SP010', 'Fanta', 'L001', 'Chai', 100);

-- Dumping data for table beveragemanagementsystem.chitietsanpham
INSERT INTO `chitietsanpham` (`MaChiTietSanPham`, `MaSanPham`, `KichThuoc`, `Gia`, `SoLuongTon`) VALUES
  ('CTSP001', 'SP001', '500ml', 10000.00, 50),
  ('CTSP002', 'SP001', '1.5L', 20000.00, 50),
  ('CTSP003', 'SP002', '500ml', 10000.00, 50),
  ('CTSP004', 'SP003', '1L', 15000.00, 80),
  ('CTSP005', 'SP004', '500ml', 8000.00, 100),
  ('CTSP006', 'SP005', '250ml', 12000.00, 60),
  ('CTSP007', 'SP006', '500ml', 9000.00, 90),
  ('CTSP008', 'SP007', '1L', 14000.00, 70),
  ('CTSP009', 'SP008', '500ml', 8000.00, 120),
  ('CTSP010', 'SP009', '500ml', 10000.00, 100);

-- Dumping data for table beveragemanagementsystem.chitietkhuyenmai
INSERT INTO `chitietkhuyenmai` (`MaKhuyenMai`, `MaSanPham`, `PhanTramKhuyenMai`) VALUES
  ('KM001', 'SP001', 5.00),
  ('KM001', 'SP002', 5.00),
  ('KM002', 'SP003', 10.00),
  ('KM003', 'SP004', 15.00),
  ('KM004', 'SP005', 20.00),
  ('KM005', 'SP006', 10.00),
  ('KM006', 'SP007', 5.00);

-- Dumping data for table beveragemanagementsystem.phieunhap
INSERT INTO `phieunhap` (`MaPhieuNhap`, `MaNhaCungCap`, `MaNhanVien`, `ThoiGian`, `TongSoTien`) VALUES
  ('PN001', 'NCC001', 'NV005', '2019-01-19 10:00:00', 500000.00),
  ('PN002', 'NCC002', 'NV006', '2020-02-01 14:00:00', 400000.00),
  ('PN003', 'NCC003', 'NV005', '2020-02-23 09:00:00', 600000.00),
  ('PN004', 'NCC004', 'NV006', '2021-06-16 11:00:00', 300000.00),
  ('PN005', 'NCC005', 'NV005', '2021-07-24 15:00:00', 350000.00),
  ('PN006', 'NCC006', 'NV006', '2021-09-22 13:00:00', 450000.00),
  ('PN007', 'NCC007', 'NV005', '2022-01-17 12:00:00', 500000.00),
  ('PN008', 'NCC008', 'NV006', '2022-02-03 16:00:00', 400000.00);

-- Dumping data for table beveragemanagementsystem.chitietphieunhap
INSERT INTO `chitietphieunhap` (`MaPhieuNhap`, `MaChiTietSanPham`, `SoLuong`, `DonGia`, `ThanhTien`) VALUES
  ('PN001', 'CTSP001', 50, 10000.00, 500000.00),
  ('PN002', 'CTSP003', 40, 10000.00, 400000.00),
  ('PN003', 'CTSP004', 40, 15000.00, 600000.00),
  ('PN004', 'CTSP005', 50, 6000.00, 300000.00),
  ('PN005', 'CTSP006', 50, 7000.00, 350000.00),
  ('PN006', 'CTSP007', 50, 9000.00, 450000.00),
  ('PN007', 'CTSP008', 50, 10000.00, 500000.00),
  ('PN008', 'CTSP009', 50, 8000.00, 400000.00);

-- Dumping data for table beveragemanagementsystem.hoadon
INSERT INTO `hoadon` (`MaHoaDon`, `MaNhanVien`, `MaKhachHang`, `NgayLapHoaDon`, `TongHoaDon`, `PhuongThucThanhToan`, `MaKhuyenMai`, `TamTinh`, `GiamGia`) VALUES
  ('HD001', 'NV003', 'CTM0000000001', '2019-03-26 14:00:00', 19000.00, 'Tiền mặt', 'KM001', 20000.00, 1000.00),
  ('HD002', 'NV004', 'CTM0000000002', '2020-09-30 15:00:00', 9500.00, 'Thẻ ngân hàng', 'KM001', 10000.00, 500.00),
  ('HD003', 'NV003', 'CTM0000000003', '2021-10-18 16:00:00', 18000.00, 'Tiền mặt', 'KM002', 20000.00, 2000.00),
  ('HD004', 'NV004', 'CTM0000000004', '2022-04-30 17:00:00', 6800.00, 'Thẻ ngân hàng', 'KM002', 6800.00, 0.00),
  ('HD005', 'NV003', 'CTM0000000005', '2022-05-01 18:00:00', 10800.00, 'Tiền mặt', 'KM002', 10800.00, 0.00),
  ('HD006', 'NV004', 'CTM0000000006', '2023-07-07 19:00:00', 9000.00, 'Thẻ ngân hàng', 'KM003', 9000.00, 0.00),
  ('HD007', 'NV003', 'CTM0000000007', '2024-06-19 20:00:00', 14000.00, 'Tiền mặt', 'KM004', 14000.00, 0.00),
  ('HD008', 'NV004', 'CTM0000000008', '2024-10-30 21:00:00', 8000.00, 'Thẻ ngân hàng', 'KM005', 8000.00, 0.00);

-- Dumping data for table beveragemanagementsystem.chitiethoadon
INSERT INTO `chitiethoadon` (`MaHoaDon`, `MaSanPham`, `SoLuong`, `DonGia`, `ThanhTien`) VALUES
  ('HD001', 'SP001', 2, 10000.00, 20000.00),
  ('HD002', 'SP002', 1, 10000.00, 10000.00),
  ('HD003', 'SP003', 2, 10000.00, 20000.00),
  ('HD004', 'SP004', 1, 6800.00, 6800.00),
  ('HD005', 'SP005', 3, 3600.00, 10800.00),
  ('HD006', 'SP006', 1, 9000.00, 9000.00),
  ('HD007', 'SP007', 1, 14000.00, 14000.00),
  ('HD008', 'SP008', 1, 8000.00, 8000.00);