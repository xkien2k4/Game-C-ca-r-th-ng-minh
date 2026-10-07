# CARO MASTER - Ứng Dụng Cờ Caro Đỉnh Cao (Offline-First)

> **Tác giả & Phát triển bởi:** **Nguyễn Vũ Xuân Kiên**  
> **Nền tảng:** Android (Native Jetpack Compose) & Kiến trúc thuần Kotlin sẵn sàng mở rộng đa nền tảng (Compose Multiplatform cho iOS).

**CARO MASTER** là ứng dụng trò chơi Cờ Caro di động chất lượng cao, hoạt động **100% Offline** không cần kết nối mạng Internet, không phụ thuộc vào Firebase hay Server backend bên ngoài.

Toàn bộ dữ liệu được quản lý và lưu trữ cục bộ an toàn bằng **Cơ sở dữ liệu SQLite** (`caro_master.db`) qua thư viện **Room Persistence** chuẩn của Android/Jetpack Compose.

---

## 🌟 TÍNH NĂNG NỔI BẬT

1. **Chế Độ Chơi Với Máy (AI Thông Minh 4 Cấp Độ):**
   - **Dễ (Easy):** Phù hợp người mới tập chơi, đánh giá vị trí đơn giản.
   - **Trung Bình (Medium):** Phân tích thế cờ theo mẫu tấn công & phòng thủ (chặn chuỗi 3, 4).
   - **Khó (Hard):** Thuật toán Minimax duyệt sâu trước các nước cờ.
   - **Cao Thủ (Master):** Thuật toán Minimax kết hợp **Alpha-Beta Pruning** và hàm Heuristic đánh giá nâng cao, phản xạ chặn bẫy hai đầu cực kỳ chuẩn xác.

2. **Chế Độ 2 Người Chơi Trên Cùng Thiết Bị (Local Pass & Play):**
   - Đổi tên người chơi tùy ý, chọn quân X/O, hoàn nước (Undo), đánh lại (Restart), nhận thua (Surrender).

3. **Bàn Cờ Caro Đa Dạng & Tương Tác Trực Quan:**
   - Hỗ trợ kích thước bàn cờ: **10x10**, **15x15 (Chuẩn)**, **19x19 (Lớn)**.
   - Hỗ trợ phóng to/thu nhỏ (Pinch-to-zoom) và vuốt di chuyển (Pan) bàn cờ mượt mà.
   - Đánh dấu vòng sáng nước đi vừa đánh và hiệu ứng đường hoàng kim nối 5 quân chiến thắng.

4. **Đồng Hồ Đếm Giờ Theo Lượt:**
   - Tùy chọn: **Không giới hạn (∞)**, **15 giây**, **30 giây**, **60 giây**.

5. **Lưu Trữ SQLite & Xem Lại Trận Đấu (Step-by-Step Replay):**
   - Lưu trữ toàn bộ diễn biến, từng nước đi vào bảng `moves` trong SQLite.
   - Trình Replay chuyên nghiệp: Nút Nước Đầu (`|<`), Nước Trước (`<`), Tự động phát (`Play/Pause`), Nước Kế (`>`), Nước Cuối (`>|`), cùng thanh trượt tua nhanh mọi thời điểm.

6. **Hệ Thống Xếp Hạng Elo & Cấp Độ (Level / EXP):**
   - Điểm Elo khởi đầu: **1000**.
   - Thắng: **+15 đến +45 Elo** (tùy độ khó), **+100 EXP**.
   - Thua: **-15 Elo**, **+25 EXP**.
   - Hòa: **+5 Elo**, **+50 EXP**.
   - Cấp độ yêu cầu: `Level * 500 EXP` với thanh tiến trình trực quan.

7. **Bộ Danh Hiệu & Thành Tích (Achievements):**
   - Mở khóa các huy chương: *Chiến thắng đầu tiên*, *Kỳ thủ tinh anh*, *Chuỗi thắng 5/10*, *Chạm mốc 1500/2000 Elo*, *Chiến thắng siêu tốc*, *Hạ gục Đại Sư AI*.

8. **Tùy Chỉnh Cá Nhân Hóa & Cài Đặt:**
   - Đổi tên kỳ thủ, chọn Avatar đại diện phong phú (Vua, Rồng, Hổ, Đại bàng, Ninja, Samurai, Robot...).
   - Bật/tắt âm thanh hiệu ứng, rung phản hồi xúc giác (Haptics), chuyển đổi Giao diện Tối/Sáng (Dark/Light Mode).

---

## 🗄️ CẤU TRÚC CƠ SỞ DỮ LIỆU SQLITE (`caro_master.db`)

### 1. Bảng `players`
| Cột | Kiểu | Mô tả |
|---|---|---|
| `id` | INTEGER PRIMARY KEY | ID người chơi (mặc định = 1) |
| `name` | TEXT | Tên kỳ thủ |
| `avatar` | TEXT | Mã đại diện avatar |
| `rating` | INTEGER | Điểm xếp hạng Elo (mặc định 1000) |
| `wins` | INTEGER | Tổng số trận thắng |
| `losses` | INTEGER | Tổng số trận thua |
| `draws` | INTEGER | Tổng số trận hòa |
| `total_games` | INTEGER | Tổng số trận đã đấu |
| `level` | INTEGER | Cấp độ hiện tại |
| `experience` | INTEGER | Điểm kinh nghiệm (EXP) tích lũy |
| `win_streak` | INTEGER | Chuỗi trận thắng liên tiếp hiện tại |
| `max_win_streak` | INTEGER | Kỷ lục chuỗi thắng cao nhất |
| `created_at` | TEXT | Thời điểm tạo hồ sơ |

### 2. Bảng `games`
| Cột | Kiểu | Mô tả |
|---|---|---|
| `id` | INTEGER PRIMARY KEY AUTOINCREMENT | ID ván cờ |
| `player_x` | TEXT | Tên người chơi quân X |
| `player_o` | TEXT | Tên người chơi quân O |
| `mode` | TEXT | Chế độ ("AI" hoặc "LOCAL") |
| `board_size` | INTEGER | Kích thước bàn (10, 15, 19) |
| `winner` | TEXT | Người thắng ("X", "O", "DRAW") |
| `result` | TEXT | Kết quả đối với người chơi ("WIN", "LOSS", "DRAW") |
| `total_moves` | INTEGER | Tổng số nước đi trong trận |
| `duration` | INTEGER | Thời lượng trận đấu (giây) |
| `rating_change` | INTEGER | Điểm Elo tăng/giảm |
| `difficulty` | TEXT | Mức độ AI ("EASY", "MEDIUM", "HARD", "MASTER") |
| `created_at` | TEXT | Thời gian thi đấu |

### 3. Bảng `moves` (Khóa ngoại tham chiếu `games(id)`)
| Cột | Kiểu | Mô tả |
|---|---|---|
| `id` | INTEGER PRIMARY KEY AUTOINCREMENT | ID nước đi |
| `game_id` | INTEGER | ID ván cờ (Foreign Key CASCADE) |
| `move_number` | INTEGER | Thứ tự nước đi (1, 2, 3...) |
| `row` | INTEGER | Tọa độ hàng trên bàn cờ |
| `col` | INTEGER | Tọa độ cột trên bàn cờ |
| `player` | TEXT | Quân cờ đặt ("X" hoặc "O") |
| `created_at` | TEXT | Thời gian thực hiện |

### 4. Bảng `achievements`
| Cột | Kiểu | Mô tả |
|---|---|---|
| `id` | INTEGER PRIMARY KEY AUTOINCREMENT | ID thành tích |
| `achievement_key` | TEXT UNIQUE | Mã định danh thành tích |
| `title` | TEXT | Tiêu đề danh hiệu |
| `description` | TEXT | Điều kiện mở khóa |
| `icon` | TEXT | Biểu tượng cảm xúc / Icon |
| `unlocked` | INTEGER (BOOLEAN) | Trạng thái (0: Khóa, 1: Mở) |
| `unlocked_at` | TEXT | Thời gian mở khóa |

### 5. Bảng `settings`
| Cột | Kiểu | Mô tả |
|---|---|---|
| `id` | INTEGER PRIMARY KEY | ID cài đặt (mặc định = 1) |
| `sound_enabled` | INTEGER | Bật/tắt âm thanh (1: Bật, 0: Tắt) |
| `vibration_enabled` | INTEGER | Bật/tắt rung phản hồi (1: Bật, 0: Tắt) |
| `dark_mode` | INTEGER | Giao diện tối (1: Tối, 0: Sáng) |
| `board_size` | INTEGER | Kích thước bàn cờ mặc định (15) |
| `timer_seconds` | INTEGER | Thời gian mỗi lượt mặc định (30) |
| `language` | TEXT | Ngôn ngữ hiển thị ("vi") |

---

## 📁 VỊ TRÍ TỆP DATABASE TRÊN THIẾT BỊ
Tệp cơ sở dữ liệu SQLite được tạo và quản lý tại:
```
/data/data/com.aistudio.caromaster.wkvpza/databases/caro_master.db
```

### Cách Reset Database:
1. Trong ứng dụng: Mở **Cài Đặt** -> chọn **Xóa Toàn Bộ Lịch Sử Trận Đấu**.
2. Hoặc xóa dữ liệu ứng dụng trong phần **Cài đặt điện thoại** -> **Ứng dụng** -> **Caro Master** -> **Xóa dữ liệu (Clear Data)**.

---

## 🛠️ HƯỚNG DẪN CÀI ĐẶT & CHẠY TRỰC TIẾP TRÊN ANDROID STUDIO

### 1. Phiên bản JDK và Công cụ khuyên dùng
- **Phiên bản Android Studio:** Android Studio **Ladybug (2024.2+)**, **Koala (2024.1+)** hoặc **Jellyfish (2023.3+)**.
- **Bản JDK hợp lý nhất:** **JDK 17 LTS** (hoặc **JDK 21 LTS**).
  - *Khuyên dùng:* Sử dụng luôn **Embedded JDK (jbr-17 / jbr-21)** tích hợp sẵn trong Android Studio, không cần cài thêm JDK bên ngoài để tránh xung đột biến môi trường `JAVA_HOME`.
  - Cấu hình trong Android Studio: **Settings / Preferences** ➔ **Build, Execution, Deployment** ➔ **Build Tools** ➔ **Gradle** ➔ chọn **Gradle JDK: Embedded JDK (version 17)**.

### 2. Các bước mở và chạy dự án
1. **Mở dự án:** Trong Android Studio, chọn **File** ➔ **Open** ➔ chọn thư mục gốc của dự án.
2. **Đồng bộ Gradle:** Đợi Android Studio tải dependencies và hoàn tất Gradle Sync.
3. **Chọn thiết bị chạy:** Chọn máy ảo Android Emulator (Android 10 - 15) hoặc cắm điện thoại Android thật (bật USB Debugging).
4. **Nhấn nút Run (Shift + F10 hoặc icon Play màu xanh):** Ứng dụng sẽ tự động biên dịch và cài đặt trực tiếp lên thiết bị.

### 3. Lệnh biên dịch bằng dòng lệnh (Terminal):
- **Kiểm tra Unit Test:**
  ```bash
  ./gradlew test
  ```
- **Biên dịch file cài đặt APK Debug:**
  ```bash
  ./gradlew assembleDebug
  ```
  File APK đầu ra nằm tại: `app/build/outputs/apk/debug/app-debug.apk`.

---

## 📱 KHẢ NĂNG CHẠY MƯỢT VÀ HỖ TRỢ ĐA NỀN TẢNG (ANDROID & iOS)
- **Tối ưu Android:** Ứng dụng được viết 100% bằng **Jetpack Compose** kết hợp thuật toán cờ Caro AI Minimax Alpha-Beta tối ưu chạy trên **Background Coroutine (Dispatchers.Default)**. Bàn cờ vẽ bằng Canvas GPU tăng tốc phần cứng, đạt tốc độ khung hình **60 - 120 FPS mượt mà**, không gây giật lag hay treo giao diện chính (UI Thread).
- **Hỗ trợ iOS:** Toàn bộ tầng logic game (`CaroAiEngine`, `Minimax`, `CaroRules`, `GameMode`, `BoardState`) và tầng ViewModel được viết hoàn toàn bằng Kotlin thuần túy (Pure Kotlin/KMP). Khi muốn xuất bản cho iOS, có thể tái sử dụng 100% mã nguồn logic này với **Compose Multiplatform (CMP)** hoặc nhúng qua Kotlin Multiplatform Shared Framework vào Xcode.
