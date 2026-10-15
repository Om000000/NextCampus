#!/usr/bin/env bash
set -e

mkdir -p /app/database

# Auto-seed database if needed
if [ ! -f "/app/database/college_sms.db" ] && [ -f "/app/database/schema.sql" ]; then
    echo "Initializing database from schema.sql..."
    sqlite3 /app/database/college_sms.db < /app/database/schema.sql || true
fi

export DISPLAY=:99
SCREEN_WIDTH=${SCREEN_WIDTH:-1366}
SCREEN_HEIGHT=${SCREEN_HEIGHT:-768}
SCREEN_DEPTH=${SCREEN_DEPTH:-24}

# Clean any leftover X11 sockets or locks
rm -f /tmp/.X99-lock /tmp/.tX99-lock /tmp/.X11-unix/X99 || true
mkdir -p /tmp/.X11-unix
chmod 1777 /tmp/.X11-unix

# Prepare noVNC index redirect
if [ -d "/usr/share/novnc" ]; then
    cat << 'EOF' > /usr/share/novnc/index.html
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta http-equiv="refresh" content="0; url=vnc.html?autoconnect=true&resize=scale" />
  <title>NextCampus Desktop</title>
</head>
<body style="background:#0f172a;color:#f8fafc;font-family:sans-serif;text-align:center;padding-top:20vh;">
  <h2>Connecting to NextCampus Desktop GUI...</h2>
  <p><a href="vnc.html?autoconnect=true&resize=scale" style="color:#38bdf8;">Click here if not redirected automatically</a></p>
</body>
</html>
EOF
fi

echo "=========================================================="
echo "  Starting NextCampus Desktop (Web VNC Mode)..."
echo "  Access in Browser: http://localhost:6080"
echo "  Direct VNC URL:    http://localhost:6080/vnc.html?autoconnect=true&resize=scale"
echo "=========================================================="

# Start TigerVNC standalone server (Built-in X11 display + VNC server on port 5900)
echo "Starting Xvnc on display :99 (port 5900, ${SCREEN_WIDTH}x${SCREEN_HEIGHT})..."
Xvnc :99 -geometry ${SCREEN_WIDTH}x${SCREEN_HEIGHT} -depth ${SCREEN_DEPTH} -rfbport 5900 -SecurityTypes None -pn -ac &
VNC_PID=$!

# Wait for display socket
for i in {1..30}; do
    if [ -e "/tmp/.X11-unix/X99" ]; then
        echo "Xvnc display server is ready on :99!"
        break
    fi
    sleep 0.2
done

# Start Openbox window manager
echo "Starting Openbox window manager..."
openbox &
WM_PID=$!
sleep 1

# Start websockify for noVNC HTML5 browser access
echo "Starting websockify on port 6080..."
websockify --web /usr/share/novnc 6080 localhost:5900 &
NOVNC_PID=$!
sleep 1

echo "=========================================================="
echo "  NextCampus Desktop is LIVE!"
echo "  Open http://localhost:6080 in your browser."
echo "=========================================================="

echo "Launching NextCampus Java Swing Desktop Application..."
cd /app
java --enable-native-access=ALL-UNNAMED -cp "java/lib/*:java/out" com.college.sms.Main gui &
JAVA_PID=$!

# Keep container alive while services are running
wait -n $JAVA_PID $NOVNC_PID $VNC_PID || true
wait
