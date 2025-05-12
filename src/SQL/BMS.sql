-- Tạo bảng sanphamtonkho để lưu thông tin tồn kho
CREATE TABLE sanphamtonkho (
  ma_sp VARCHAR(10) PRIMARY KEY,
  so_luong INT NOT NULL DEFAULT 0,
  ngay_cap_nhat VARCHAR(30) NOT NULL,
  FOREIGN KEY (ma_sp) REFERENCES sanpham(ma_sp)
);

-- Tạo bảng phieunhap (nếu chưa có)
CREATE TABLE phieunhap (
  ma_phieu VARCHAR(10) PRIMARY KEY,
  ma_ncc VARCHAR(10) NOT NULL,
  ma_nv VARCHAR(10) NOT NULL,
  ngay_nhap VARCHAR(30) NOT NULL,
  tong_tien VARCHAR(30) NOT NULL,
  trang_thai VARCHAR(20) NOT NULL,
  FOREIGN KEY (ma_ncc) REFERENCES nhacungcap(ma_ncc),
  FOREIGN KEY (ma_nv) REFERENCES nhanvien(ma_nv)
);

-- Tạo bảng chitietphieunhap để lưu chi tiết phiếu nhập
CREATE TABLE chitietphieunhap (
  ma_phieu VARCHAR(10) NOT NULL,
  ma_sp VARCHAR(10) NOT NULL,
  so_luong INT NOT NULL,
  don_gia FLOAT NOT NULL,
  thanh_tien FLOAT NOT NULL,
  PRIMARY KEY (ma_phieu, ma_sp),
  FOREIGN KEY (ma_phieu) REFERENCES phieunhap(ma_phieu),
  FOREIGN KEY (ma_sp) REFERENCES sanpham(ma_sp)
); 