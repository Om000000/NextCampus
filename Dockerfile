# NextCampus – College Student Management System
# Multi-runtime Dockerfile (OpenJDK 21 + Python 3 + SQLite)

FROM eclipse-temurin:21-jdk-jammy

# Prevent interactive prompts during apt install
ENV DEBIAN_FRONTEND=noninteractive
ENV PYTHONUNBUFFERED=1
ENV PYTHONDONTWRITEBYTECODE=1

# Install Python 3, pip, sqlite3, and essential X11/font libs for Java AWT/Swing
RUN apt-get update && apt-get install -y --no-install-recommends \
    python3 \
    python3-pip \
    python3-venv \
    sqlite3 \
    curl \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libfreetype6 \
    fonts-dejavu-core \
    && rm -rf /var/lib/apt/lists/*

# Set up Python virtual environment
ENV VIRTUAL_ENV=/opt/venv
RUN python3 -m venv $VIRTUAL_ENV
ENV PATH="$VIRTUAL_ENV/bin:$PATH"

# Install Python requirements
WORKDIR /app
COPY python/requirements.txt /app/python/requirements.txt
RUN pip install --no-cache-dir --upgrade pip && \
    pip install --no-cache-dir -r /app/python/requirements.txt

# Copy source code and database assets
COPY database/ /app/database/
COPY java/ /app/java/
COPY python/ /app/python/
COPY entrypoint.sh /app/entrypoint.sh

# Compile Java code
RUN mkdir -p /app/java/out && \
    javac -cp "java/lib/*:java/src" -d /app/java/out \
    java/src/com/college/sms/*.java \
    java/src/com/college/sms/db/*.java \
    java/src/com/college/sms/model/*.java \
    java/src/com/college/sms/dao/*.java \
    java/src/com/college/sms/ui/*.java \
    java/src/com/college/sms/util/*.java

# Make entrypoint executable
RUN chmod +x /app/entrypoint.sh

# Persistent SQLite database volume
VOLUME ["/app/database"]

# Expose Web Portal / Phone Presenty port
EXPOSE 5000

# Default entrypoint and mode
ENTRYPOINT ["/app/entrypoint.sh"]
CMD ["web"]
