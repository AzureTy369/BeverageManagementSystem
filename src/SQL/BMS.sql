-- --------------------------------------------------------
-- Máy chủ:                      127.0.0.1
-- Phiên bản máy chủ:            10.4.32-MariaDB
-- HĐH máy chủ:                  Win64
-- HeidiSQL Phiên bản:           12.10.0.7000
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- Dumping database structure for beveragemanagementsystem
CREATE DATABASE IF NOT EXISTS `beveragemanagementsystem` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;
USE `beveragemanagementsystem`;

-- Dumping structure for table beveragemanagementsystem.chitietsanpham
CREATE TABLE IF NOT EXISTS `chitietsanpham` (
  `MaChiTietSanPham` varchar(10) NOT NULL,
  `MaSanPham` varchar(10) NOT NULL,
  `KichThuoc` varchar(20) DEFAULT NULL,
  `Gia` decimal(10,2) NOT NULL,
  `SoLuongTon` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`MaChiTietSanPham`),
  KEY `MaSanPham` (`MaSanPham`),
  CONSTRAINT `chitietsanpham_ibfk_1` FOREIGN KEY (`MaSanPham`) REFERENCES `sanpham` (`MaSanPham`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping structure for table beveragemanagementsystem.chitietphieunhap
CREATE TABLE IF NOT EXISTS `chitietphieunhap` (
  `MaPhieuNhap` varchar(10) NOT NULL,
  `MaChiTietSanPham` varchar(10) NOT NULL,
  `SoLuong` int(11) NOT NULL,
  `DonGia` decimal(10,2) NOT NULL,
  `ThanhTien` decimal(10,2) NOT NULL,
  PRIMARY KEY (`MaPhieuNhap`,`MaChiTietSanPham`),
  KEY `MaChiTietSanPham` (`MaChiTietSanPham`),
  CONSTRAINT `chitietphieunhap_ibfk_1` FOREIGN KEY (`MaPhieuNhap`) REFERENCES `phieunhap` (`MaPhieuNhap`),
  CONSTRAINT `chitietphieunhap_ibfk_2` FOREIGN KEY (`MaChiTietSanPham`) REFERENCES `chitietsanpham` (`MaChiTietSanPham`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping structure for table beveragemanagementsystem.chucvu
CREATE TABLE IF NOT EXISTS `chucvu` (
  `MaChucVu` varchar(10) NOT NULL,
  `TenChucVu` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `Luong` decimal(10,2) NOT NULL,
  PRIMARY KEY (`MaChucVu`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table beveragemanagementsystem.chucvu
REPLACE INTO `chucvu` (`MaChucVu`, `TenChucVu`, `Luong`) VALUES
  ('CV001', 'Quản trị viên', 20000000.00),
  ('CV002', 'Quản lý', 15000000.00),
  ('CV003', 'Nhân viên bán hàng', 8000000.00);

-- Dumping structure for table beveragemanagementsystem.customer
CREATE TABLE IF NOT EXISTS `customer` (
  `customerID` varchar(10) NOT NULL,
  `firstname` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `lastname` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `phone` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`customerID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table beveragemanagementsystem.customer
INSERT INTO customer (customerID, firstname, lastname, address, phone) VALUES 
('CTM0000000001', 'Nguyen Hoang', 'Hai', 'Ha Noi', '0782748863'),
('CTM0000000002', 'Bui Thi Khanh', 'Ha', 'Can Tho', '0912345678'),
('CTM0000000003', 'Nguyen Van', 'Hieu', 'Ho Chi Minh', '0923456789'),
('CTM0000000004', 'Nguyen Hoang Dan', 'Ngoc', 'Buon Ma Thuot', '0934567890'),
('CTM0000000005', 'Truong Kim', 'Anh', 'Buon Ma Thuot', '0945678901'),
('CTM0000000006', 'Mai Bao', 'Ngoc', 'Buon Ma Thuot', '0956789012'),
('CTM0000000007', 'Nguyen Thi Thanh', 'Tuyen', 'Hau Giang', '0967890123'),
('CTM0000000008', 'Nguyen Xuan', 'Thanh', 'Ha Noi', '0978901234'),
('CTM0000000009', 'Bui Quang Minh', 'Hieu', 'Ho Chi Minh', '0989012345'),
('CTM0000000010', 'Nguyen Khac', 'Nhu', 'Bac Lieu', '0990123456');

-- Dumping structure for table beveragemanagementsystem.chitiethoadon
CREATE TABLE IF NOT EXISTS `chitiethoadon` (
  `MaHoaDon` varchar(10) NOT NULL,
  `MaSanPham` varchar(10) NOT NULL,
  `SoLuong` int(11) NOT NULL,
  `DonGia` decimal(10,2) NOT NULL,
  `ThanhTien` decimal(10,2) NOT NULL,
  PRIMARY KEY (`MaHoaDon`,`MaSanPham`),
  KEY `MaSanPham` (`MaSanPham`),
  CONSTRAINT `chitiethoadon_ibfk_1` FOREIGN KEY (`MaHoaDon`) REFERENCES `hoadon` (`MaHoaDon`),
  CONSTRAINT `chitiethoadon_ibfk_2` FOREIGN KEY (`MaSanPham`) REFERENCES `sanpham` (`MaSanPham`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping structure for table beveragemanagementsystem.hoadon
CREATE TABLE IF NOT EXISTS `hoadon` (
  `MaHoaDon` varchar(10) NOT NULL,
  `MaNhanVien` varchar(10) NOT NULL,
  `MaKhachHang` varchar(10) DEFAULT NULL,
  `NgayLapHoaDon` datetime NOT NULL,
  `TongHoaDon` decimal(10,2) NOT NULL,
  `PhuongThucThanhToan` varchar(50) NOT NULL,
  `MaKhuyenMai` varchar(10) DEFAULT NULL,
  `TamTinh` decimal(10,2) DEFAULT NULL,
  `GiamGia` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`MaHoaDon`),
  KEY `MaNhanVien` (`MaNhanVien`),
  KEY `MaKhachHang` (`MaKhachHang`),
  KEY `MaKhuyenMai` (`MaKhuyenMai`),
  CONSTRAINT `hoadon_ibfk_1` FOREIGN KEY (`MaNhanVien`) REFERENCES `nhanvien` (`MaNhanVien`),
  CONSTRAINT `hoadon_ibfk_2` FOREIGN KEY (`MaKhachHang`) REFERENCES `customer` (`customerID`),
  CONSTRAINT `hoadon_ibfk_3` FOREIGN KEY (`MaKhuyenMai`) REFERENCES `khuyenmai` (`MaKhuyenMai`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping structure for table beveragemanagementsystem.khuyenmai
REATE TABLE IF NOT EXISTS `khuyenmai` (
  `MaKhuyenMai` varchar(10) NOT NULL,
  `TenKhuyenMai` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `NgayBatDau` datetime NOT NULL,
  `NgayKetThuc` datetime NOT NULL,
  PRIMARY KEY (`MaKhuyenMai`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping structure for table beveragemanagementsystem.chitietkhuyenmai
CREATE TABLE IF NOT EXISTS `chitietkhuyenmai` (
  `MaKhuyenMai` varchar(10) NOT NULL,
  `MaSanPham` varchar(10) NOT NULL,
  `PhanTramKhuyenMai` decimal(5,2) NOT NULL,
  PRIMARY KEY (`MaKhuyenMai`,`MaSanPham`),
  KEY `MaSanPham` (`MaSanPham`),
  CONSTRAINT `chitietkhuyenmai_ibfk_1` FOREIGN KEY (`MaKhuyenMai`) REFERENCES `khuyenmai` (`MaKhuyenMai`),
  CONSTRAINT `chitietkhuyenmai_ibfk_2` FOREIGN KEY (`MaSanPham`) REFERENCES `sanpham` (`MaSanPham`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping structure for table beveragemanagementsystem.loai
CREATE TABLE IF NOT EXISTS `loai` (
  `MaLoai` varchar(10) NOT NULL,
  `TenLoai` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`MaLoai`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table beveragemanagementsystem.loai
INSERT INTO `loai` (`MaLoai`, `TenLoai`) VALUES
  ('L001', 'Đồ uống'),
  ('L002', 'Thực phẩm'),
  ('L003', 'Khác');

-- Dumping structure for table beveragemanagementsystem.loaisanpham
CREATE TABLE IF NOT EXISTS `loaisanpham` (
  `MaLoaiSP` varchar(10) NOT NULL,
  `TenLoaiSP` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `MoTa` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`MaLoaiSP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table beveragemanagementsystem.loaisanpham
REPLACE INTO `loaisanpham` (`MaLoaiSP`, `TenLoaiSP`, `MoTa`) VALUES
  ('LSP001', 'Nước giải khát', 'Các loại đồ uống giải khát'),
  ('LSP002', 'Bia', 'Các loại bia'),
  ('LSP003', 'Nước suối', 'Các loại nước suối đóng chai'),
  ('LSP004', 'Nước suối', 'Các loại nước suối đóng chai');

-- Dumping structure for table beveragemanagementsystem.nhacungcap
CREATE TABLE IF NOT EXISTS `nhacungcap` (
  `MaNhaCungCap` varchar(10) NOT NULL,
  `TenNhaCungCap` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `SoDienThoai` varchar(15) NOT NULL,
  `DiaChi` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `Email` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`MaNhaCungCap`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table beveragemanagementsystem.nhacungcap
REPLACE INTO `nhacungcap` (`MaNhaCungCap`, `TenNhaCungCap`, `SoDienThoai`, `DiaChi`, `Email`) VALUES
  ('001', 'COCACOLA', '1231231231', '3123', '123@gmail.com');

-- Dumping structure for table beveragemanagementsystem.nhanvien
CREATE TABLE IF NOT EXISTS `nhanvien` (
  `MaNhanVien` varchar(10) NOT NULL,
  `TenTaiKhoan` varchar(50) NOT NULL,
  `MatKhau` varchar(50) NOT NULL,
  `Ho` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `Ten` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `MaChucVu` varchar(10) NOT NULL,
  `SoDienThoai` varchar(15) NOT NULL,
  PRIMARY KEY (`MaNhanVien`),
  UNIQUE KEY `TenTaiKhoan` (`TenTaiKhoan`),
  KEY `MaChucVu` (`MaChucVu`),
  CONSTRAINT `nhanvien_ibfk_1` FOREIGN KEY (`MaChucVu`) REFERENCES `chucvu` (`MaChucVu`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table beveragemanagementsystem.nhanvien
REPLACE INTO `nhanvien` (`MaNhanVien`, `TenTaiKhoan`, `MatKhau`, `Ho`, `Ten`, `MaChucVu`, `SoDienThoai`) VALUES
  ('NV001', 'admin', 'admin123', 'Admin', 'User', 'CV001', '0987654321'),
  ('NV002', 'Thaiduidz', '123123', 'Nguyen', 'Thai1', 'CV003', '123456'),
  ('NV003', 'Thaiduidz1', '123123', 'Nguyen', 'Thai', 'CV002', '123123123');

-- Dumping structure for table beveragemanagementsystem.phieunhap
CREATE TABLE IF NOT EXISTS `phieunhap` (
  `MaPhieuNhap` varchar(10) NOT NULL,
  `MaNhaCungCap` varchar(10) NOT NULL,
  `MaNhanVien` varchar(10) NOT NULL,
  `ThoiGian` datetime NOT NULL,
  `TongSoTien` decimal(10,2) NOT NULL,
  PRIMARY KEY (`MaPhieuNhap`),
  KEY `MaNhaCungCap` (`MaNhaCungCap`),
  KEY `MaNhanVien` (`MaNhanVien`),
  CONSTRAINT `phieunhap_ibfk_1` FOREIGN KEY (`MaNhaCungCap`) REFERENCES `nhacungcap` (`MaNhaCungCap`),
  CONSTRAINT `phieunhap_ibfk_2` FOREIGN KEY (`MaNhanVien`) REFERENCES `nhanvien` (`MaNhanVien`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping structure for table beveragemanagementsystem.sanpham
CREATE TABLE IF NOT EXISTS `sanpham` (
  `MaSanPham` varchar(10) NOT NULL,
  `TenSanPham` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `MaLoai` varchar(10) NOT NULL,
  `DonViTinh` varchar(20) NOT NULL,
  `SoLuongTon` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`MaSanPham`),
  KEY `MaLoai` (`MaLoai`),
  CONSTRAINT `sanpham_ibfk_1` FOREIGN KEY (`MaLoai`) REFERENCES `loai` (`MaLoai`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;