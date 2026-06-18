# Mục đích: Dùng JDK để compile source code thành file .jar
FROM eclipse-temurin:17-jdk-alpine AS builder
ARG CACHEBUST=1
# Đặt thư mục làm việc bên trong container (tương đương cd /build)
# Mọi lệnh COPY, RUN phía dưới đều thực hiện trong thư mục này
WORKDIR /build
#-----------------------------------------------------------
#                           BƯỚC 1:
# Copy file cấu hình Maven TRƯỚC khi copy source code
# Lý do: Docker cache theo từng layer — nếu pom.xml không đổi,
# Docker tái sử dụng layer đã cache → KHÔNG cần download lại
# tiết kiệm thời gian rất nhiều
# -----------------------------------------------------------
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw
# Download toàn bộ thư viện Maven về trước
# -q = quiet mode (bớt log)
RUN ./mvnw dependency:go-offline -q

# -----------------------------------------------------------
#                           BƯỚC 2:
# Copy source code và tiến hành build
# Copy source sau vì source thay đổi thường xuyên hơn pom.xml
# Nếu copy sớm hơn sẽ làm mất tác dụng cache ở bước trên
# -----------------------------------------------------------
COPY src ./src

# Compile và đóng gói thành file .jar
# -DskipTests = bỏ qua test khi build image
# (test nên chạy ở CI pipeline riêng, không phải ở đây)
RUN ./mvnw package -DskipTests -q


# ============================================================
#  STAGE 2: RUN
#  Mục đích: Image nhỏ gọn chỉ để CHẠY app, không build
#  Chỉ lấy file .jar từ stage 1, bỏ lại toàn bộ JDK + source
# ============================================================

# Dùng JRE thay vì JDK vì chỉ cần runtime để chạy .jar
# alpine = bản Linux siêu nhẹ (~5MB) → image cuối nhỏ hơn ~200MB
FROM eclipse-temurin:17-jre-alpine

# Thư mục làm việc của app trong container
WORKDIR /app

# -----------------------------------------------------------
# Tạo user và group riêng để chạy app (KHÔNG dùng root)
# Lý do bảo mật: nếu bị tấn công, hacker không có quyền root
# -S = system user/group (không có home dir, không login được)
# -----------------------------------------------------------
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# -----------------------------------------------------------
# Chỉ copy file .jar từ stage "builder", KHÔNG copy JDK hay source
# Đây là điểm mấu chốt của multi-stage build:
# image cuối sạch, nhỏ, không lộ source code
# -----------------------------------------------------------
COPY --from=builder /build/target/*.jar app.jar

# Đổi quyền sở hữu file .jar sang appuser
# Phải làm TRƯỚC lệnh USER bên dưới (vì sau đó không còn quyền root)
RUN chown appuser:appgroup app.jar

# Từ đây container chạy với appuser thay vì root
USER appuser

# -----------------------------------------------------------
# Khai báo port app sẽ lắng nghe (mang tính documentation)
# KHÔNG tự động mở port — muốn mở phải dùng -p khi docker run
# Ví dụ: docker run -p 8080:8080 my-spring-app
# -----------------------------------------------------------
EXPOSE 8080

# -----------------------------------------------------------
# Health Check: Docker/Kubernetes dùng để biết app đã sẵn sàng chưa
#
# --interval=30s    : Kiểm tra mỗi 30 giây
# --timeout=5s      : Quá 5s không response → coi là failed
# --start-period=30s: Chờ 30s sau khi start mới bắt đầu check
#                     (Spring Boot cần thời gian khởi động)
# --retries=3       : Fail 3 lần liên tiếp → đánh dấu "unhealthy"
#
# Yêu cầu: project cần có Spring Actuator dependency trong pom.xml
# -----------------------------------------------------------
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

# -----------------------------------------------------------
# Lệnh chạy app khi container khởi động
# Dùng ENTRYPOINT (không bị override) thay vì CMD (có thể bị override)
#
# Các tham số JVM:
# -XX:+UseContainerSupport   : JVM đọc đúng RAM/CPU giới hạn của container
#                              (thay vì đọc tài nguyên của máy host)
# -XX:MaxRAMPercentage=75.0  : JVM chỉ dùng tối đa 75% RAM container
#                              (còn lại cho OS và các tiến trình khác)
# -Djava.security.egd=...    : Dùng nguồn entropy nhanh hơn
#                              (tránh app bị treo lúc khởi động)
# -jar app.jar               : Chạy file jar
# -----------------------------------------------------------
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]