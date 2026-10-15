#!/usr/bin/env bash
set -e

# Ensure database directory exists
mkdir -p /app/database

# Auto-seed database schema if database file does not exist
if [ ! -f "/app/database/college_sms.db" ] && [ -f "/app/database/schema.sql" ]; then
    echo "Initializing database from schema.sql..."
    sqlite3 /app/database/college_sms.db < /app/database/schema.sql || true
fi

MODE="${1:-web}"

case "$MODE" in
    web|attendance)
        echo "=========================================================="
        echo "  NextCampus – College Student Management System"
        echo "  Starting Phone Presenty & Student Web Portal..."
        echo "  Listening on: http://0.0.0.0:5000"
        echo "=========================================================="
        cd /app/python
        exec python3 attendance_server.py
        ;;

    console|cli)
        echo "=========================================================="
        echo "  NextCampus – Interactive Console Mode"
        echo "=========================================================="
        cd /app
        exec java --enable-native-access=ALL-UNNAMED -cp "java/lib/*:java/out" com.college.sms.Main console
        ;;

    gui|desktop)
        echo "=========================================================="
        echo "  NextCampus – Swing Desktop GUI Mode"
        echo "=========================================================="
        cd /app
        exec java --enable-native-access=ALL-UNNAMED -cp "java/lib/*:java/out" com.college.sms.Main gui
        ;;

    both|all)
        echo "=========================================================="
        echo "  NextCampus – Dual Mode"
        echo "  Starting Web Portal in background & Console in foreground..."
        echo "=========================================================="
        cd /app/python
        python3 attendance_server.py &
        WEB_PID=$!
        trap "kill $WEB_PID" EXIT INT TERM
        sleep 2
        cd /app
        java --enable-native-access=ALL-UNNAMED -cp "java/lib/*:java/out" com.college.sms.Main console
        ;;

    bash|sh)
        exec /bin/bash "${@:2}"
        ;;

    *)
        exec "$@"
        ;;
esac
