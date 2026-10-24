import os
from PIL import Image, ImageDraw, ImageFont

assets_dir = r"presentation_assets"
os.makedirs(assets_dir, exist_ok=True)

# Helper function to get a font
def get_font(size, bold=False):
    # Try common Windows fonts
    font_names = ["timesbd.ttf" if bold else "times.ttf", "arialbd.ttf" if bold else "arial.ttf", "segoeuib.ttf" if bold else "segoeui.ttf"]
    for f in font_names:
        try:
            return ImageFont.truetype(f, size)
        except:
            continue
    return ImageFont.load_default()

def draw_rounded_rect(draw, bbox, radius, fill, outline=None, width=1):
    x0, y0, x1, y1 = bbox
    draw.rounded_rectangle([x0, y0, x1, y1], radius=radius, fill=fill, outline=outline, width=width)

# 1. SCREEN 1: LOGIN DIALOG MOCKUP
def create_login_mockup():
    w, h = 900, 520
    im = Image.new("RGBA", (w, h), (15, 23, 42, 255)) # Midnight slate #0f172a
    draw = ImageDraw.Draw(im)

    # Window title bar
    draw.rectangle([0, 0, w, 40], fill=(30, 41, 59, 255))
    f_title = get_font(16, bold=True)
    f_reg = get_font(14)
    f_bold = get_font(14, bold=True)
    f_sm = get_font(12)
    f_h1 = get_font(22, bold=True)

    draw.text((18, 10), "NextCampus – Multi-Role Academic Authentication (HRM Pune)", fill=(241, 245, 249), font=f_title)
    # Window controls
    draw.ellipse([w-70, 14, w-56, 28], fill=(234, 179, 8)) # Yellow
    draw.ellipse([w-45, 14, w-31, 28], fill=(239, 68, 68)) # Red
    draw.ellipse([w-95, 14, w-81, 28], fill=(34, 197, 94)) # Green

    # Left branding card
    draw_rounded_rect(draw, [30, 65, 340, 480], 12, (30, 41, 59, 255), (51, 65, 85, 255), 2)
    # Paste college logo if exists
    logo_path = os.path.join(assets_dir, "college_logo.png")
    if os.path.exists(logo_path):
        logo = Image.open(logo_path).convert("RGBA")
        logo.thumbnail((260, 130))
        im.paste(logo, (55, 85), logo)

    draw.text((50, 240), "NextCampus ERP", fill=(16, 185, 129), font=f_h1)
    draw.text((50, 275), "Hutatma Rajguru Mahavidyalaya", fill=(248, 250, 252), font=f_bold)
    draw.text((50, 298), "Department of BBA(CA)", fill=(148, 163, 184), font=f_reg)
    draw.text((50, 335), "• Role-Based Access Control (RBAC)", fill=(203, 213, 225), font=f_sm)
    draw.text((50, 360), "• Dynamic Light/Dark Theme", fill=(203, 213, 225), font=f_sm)
    draw.text((50, 385), "• Anti-Proxy Token Security", fill=(203, 213, 225), font=f_sm)
    draw.text((50, 410), "• Embedded SQLite Database", fill=(203, 213, 225), font=f_sm)
    draw.text((50, 445), "Version: 2.4.0 (TYBBA-CA)", fill=(100, 116, 139), font=f_sm)

    # Right Login Form Card
    draw_rounded_rect(draw, [360, 65, 870, 480], 12, (30, 41, 59, 255), (51, 65, 85, 255), 2)
    draw.text((385, 85), "Sign In to Your Academic Portal", fill=(248, 250, 252), font=f_h1)
    draw.text((385, 118), "Select your role to access relevant department features", fill=(148, 163, 184), font=f_reg)

    # Role selector tabs
    roles = ["HOD / Admin", "Teacher", "Librarian", "Student"]
    rx = 385
    for i, r in enumerate(roles):
        bg = (16, 185, 129) if i == 0 else (51, 65, 85)
        fg = (255, 255, 255) if i == 0 else (203, 213, 225)
        draw_rounded_rect(draw, [rx, 150, rx+110, 182], 6, bg)
        draw.text((rx+12, 158), r, fill=fg, font=f_bold if i==0 else f_reg)
        rx += 118

    # Inputs
    draw.text((385, 205), "Username / Registered Mobile:", fill=(203, 213, 225), font=f_bold)
    draw_rounded_rect(draw, [385, 230, 845, 268], 6, (15, 23, 42), (71, 85, 105), 1)
    draw.text((400, 240), "tanpure (HOD Account)", fill=(248, 250, 252), font=f_reg)

    draw.text((385, 285), "Password / Security PIN:", fill=(203, 213, 225), font=f_bold)
    draw_rounded_rect(draw, [385, 310, 845, 348], 6, (15, 23, 42), (71, 85, 105), 1)
    draw.text((400, 320), "••••••••••••", fill=(248, 250, 252), font=f_reg)

    # Fast demo login chips
    draw.text((385, 365), "Demo Quick Sign-In:", fill=(148, 163, 184), font=f_sm)
    demo_chips = [("HOD", 385), ("Teacher", 475), ("Librarian", 575), ("Student (Omkar)", 685)]
    for lbl, cx in demo_chips:
        draw_rounded_rect(draw, [cx, 385, cx+85, 410], 4, (51, 65, 85), (71, 85, 105), 1)
        draw.text((cx+10, 391), lbl, fill=(56, 189, 248), font=f_sm)

    # Sign In Button
    draw_rounded_rect(draw, [385, 430, 845, 470], 6, (16, 185, 129))
    draw.text((550, 440), "SECURE LOGIN →", fill=(255, 255, 255), font=f_bold)

    out_p = os.path.join(assets_dir, "screen1_login.png")
    im.save(out_p)
    print("Saved screen1_login.png")

# 2. SCREEN 2: STUDENT DASHBOARD & EXAM ENROLLMENT
def create_student_dashboard_mockup():
    w, h = 900, 520
    im = Image.new("RGBA", (w, h), (15, 23, 42, 255))
    draw = ImageDraw.Draw(im)

    f_title = get_font(16, bold=True)
    f_reg = get_font(13)
    f_bold = get_font(13, bold=True)
    f_sm = get_font(11)
    f_h1 = get_font(18, bold=True)

    # Top Navigation bar
    draw.rectangle([0, 0, w, 48], fill=(30, 41, 59, 255))
    draw.text((20, 14), "NextCampus | Student Self-Service Portal", fill=(241, 245, 249), font=f_title)
    draw_rounded_rect(draw, [w-240, 10, w-140, 38], 6, (51, 65, 85))
    draw.text((w-230, 16), "☀️ Light Mode", fill=(248, 250, 252), font=f_sm)
    draw_rounded_rect(draw, [w-130, 10, w-20, 38], 6, (239, 68, 68))
    draw.text((w-105, 16), "Logout", fill=(255, 255, 255), font=f_bold)

    # Student Profile Banner
    draw_rounded_rect(draw, [20, 60, w-20, 130], 10, (30, 41, 59), (51, 65, 85), 1)
    draw.ellipse([35, 70, 85, 120], fill=(16, 185, 129))
    draw.text((48, 83), "OH", fill=(255, 255, 255), font=f_h1)

    draw.text((100, 72), "OMKAR SANDIP HADAWALE", fill=(248, 250, 252), font=f_h1)
    draw.text((100, 98), "Roll No: 02  |  Class: TYBBA(CA) Sem-V  |  PRN: 202301640023  |  Status: Regular", fill=(148, 163, 184), font=f_reg)
    draw_rounded_rect(draw, [w-170, 78, w-40, 112], 6, (16, 185, 129))
    draw.text((w-158, 87), "Seat: BBA5-S102", fill=(255, 255, 255), font=f_bold)

    # 3 Summary Cards
    cards = [
        ("ATTENDANCE", "88.5%", "Eligible for Exams", (16, 185, 129), 20),
        ("LIBRARY BOOKS", "2 Borrowed", "0 Overdue / ₹0 Fine", (56, 189, 248), 315),
        ("INTERNAL EXAM", "ENROLLED", "Seat: BBA5-S102 Approved", (245, 158, 11), 610)
    ]
    for title, val, sub, col, cx in cards:
        draw_rounded_rect(draw, [cx, 142, cx+270, 215], 8, (30, 41, 59), (51, 65, 85), 1)
        draw.text((cx+15, 152), title, fill=col, font=f_bold)
        draw.text((cx+15, 172), val, fill=(248, 250, 252), font=f_h1)
        draw.text((cx+15, 196), sub, fill=(148, 163, 184), font=f_sm)

    # Active Tab Content: Internal Exam & Hall Ticket Section
    draw_rounded_rect(draw, [20, 228, w-20, 500], 10, (30, 41, 59), (51, 65, 85), 1)
    draw.text((35, 242), "Internal Examination Form & Digital Hall Ticket Status", fill=(248, 250, 252), font=f_h1)

    # Table Header
    draw.rectangle([35, 275, w-35, 305], fill=(51, 65, 85))
    draw.text((45, 282), "Subject Code & Name", fill=(241, 245, 249), font=f_bold)
    draw.text((360, 282), "Date & Slot", fill=(241, 245, 249), font=f_bold)
    draw.text((540, 282), "Exam Status", fill=(241, 245, 249), font=f_bold)
    draw.text((700, 282), "Action / Admit Card", fill=(241, 245, 249), font=f_bold)

    rows = [
        ("CA-501 : Cyber Security & Law", "24-Oct-2026 (10:00 AM)", "Approved", True),
        ("CA-502 : Advanced Java Programming", "25-Oct-2026 (10:00 AM)", "Approved", True),
        ("CA-503 : Cloud Computing & DevOps", "26-Oct-2026 (10:00 AM)", "Approved", True),
        ("CA-504 : Software Testing & Quality Assurance", "27-Oct-2026 (10:00 AM)", "Approved", True),
        ("CA-505 : Project Laboratory Work", "28-Oct-2026 (02:00 PM)", "Approved", True),
    ]
    ry = 310
    for subj, dte, stat, app in rows:
        draw.rectangle([35, ry, w-35, ry+32], fill=(24, 33, 47) if (ry//32)%2==0 else (30, 41, 59))
        draw.text((45, ry+8), subj, fill=(248, 250, 252), font=f_reg)
        draw.text((360, ry+8), dte, fill=(203, 213, 225), font=f_sm)
        draw.text((540, ry+8), "✔ Form Approved", fill=(34, 197, 94), font=f_bold)
        draw_rounded_rect(draw, [700, ry+4, 840, ry+28], 4, (16, 185, 129))
        draw.text((712, ry+8), "Download Hall Ticket", fill=(255, 255, 255), font=f_sm)
        ry += 35

    draw.text((35, 478), "Allocated Examination Seat Number: BBA5-S102  |  Examination Center: Lab 1, Hutatma Rajguru Mahavidyalaya", fill=(245, 158, 11), font=f_bold)

    out_p = os.path.join(assets_dir, "screen2_student_exam.png")
    im.save(out_p)
    print("Saved screen2_student_exam.png")

# 3. SCREEN 3: LIBRARY MANAGEMENT & CIRCULATION
def create_library_mockup():
    w, h = 900, 520
    im = Image.new("RGBA", (w, h), (15, 23, 42, 255))
    draw = ImageDraw.Draw(im)

    f_title = get_font(16, bold=True)
    f_reg = get_font(13)
    f_bold = get_font(13, bold=True)
    f_sm = get_font(11)
    f_h1 = get_font(18, bold=True)

    # Title bar
    draw.rectangle([0, 0, w, 48], fill=(30, 41, 59, 255))
    draw.text((20, 14), "NextCampus | Library Information & Book Circulation Engine", fill=(241, 245, 249), font=f_title)
    draw_rounded_rect(draw, [w-160, 10, w-20, 38], 6, (16, 185, 129))
    draw.text((w-145, 16), "+ Issue New Book", fill=(255, 255, 255), font=f_bold)

    # Search Bar & Filter Row
    draw_rounded_rect(draw, [20, 60, 450, 98], 6, (15, 23, 42), (71, 85, 105), 1)
    draw.text((35, 71), "🔍 Search books by Title, Author, ISBN, or Subject...", fill=(148, 163, 184), font=f_reg)
    draw_rounded_rect(draw, [465, 60, 580, 98], 6, (51, 65, 85))
    draw.text((485, 71), "Filter: All (128)", fill=(248, 250, 252), font=f_sm)

    # Circulation Rule Banner
    draw_rounded_rect(draw, [595, 60, w-20, 98], 6, (24, 33, 47), (245, 158, 11), 1)
    draw.text((610, 68), "Library Circulation Policy Active:", fill=(245, 158, 11), font=f_bold)
    draw.text((610, 83), "Loan: 14 Days  |  Late Fine: ₹5.00 / day", fill=(203, 213, 225), font=f_sm)

    # Main Catalog Table
    draw_rounded_rect(draw, [20, 110, w-20, 500], 10, (30, 41, 59), (51, 65, 85), 1)
    draw.rectangle([20, 110, w-20, 145], fill=(51, 65, 85))
    draw.text((35, 122), "ISBN", fill=(241, 245, 249), font=f_bold)
    draw.text((150, 122), "Book Title", fill=(241, 245, 249), font=f_bold)
    draw.text((450, 122), "Author", fill=(241, 245, 249), font=f_bold)
    draw.text((630, 122), "Copies (Total/Avail)", fill=(241, 245, 249), font=f_bold)
    draw.text((790, 122), "Status / Action", fill=(241, 245, 249), font=f_bold)

    books = [
        ("978-007-180925", "Java: The Complete Reference", "Herbert Schildt", "10 / 6 Available", "Available"),
        ("978-013-468599", "Effective Java (3rd Edition)", "Joshua Bloch", "8 / 5 Available", "Available"),
        ("978-026-203384", "Introduction to Algorithms", "Cormen, Leiserson", "5 / 1 Available", "Low Stock"),
        ("978-013-235088", "Clean Code: Agile Craftsman", "Robert C. Martin", "6 / 4 Available", "Available"),
        ("978-144-935573", "Learning Python (5th Edition)", "Mark Lutz", "12 / 8 Available", "Available"),
        ("978-007-063412", "Database System Concepts", "Silberschatz, Korth", "15 / 11 Available", "Available"),
        ("978-059-651774", "JavaScript: The Good Parts", "Douglas Crockford", "7 / 0 Available", "All Issued")
    ]
    by = 150
    for isbn, title, auth, copies, stat in books:
        draw.rectangle([20, by, w-20, by+36], fill=(24, 33, 47) if (by//36)%2==0 else (30, 41, 59))
        draw.text((35, by+10), isbn, fill=(148, 163, 184), font=f_sm)
        draw.text((150, by+10), title, fill=(248, 250, 252), font=f_bold)
        draw.text((450, by+10), auth, fill=(203, 213, 225), font=f_reg)
        draw.text((630, by+10), copies, fill=(56, 189, 248), font=f_reg)

        btn_col = (16, 185, 129) if "Available" in stat else ((234, 179, 8) if "Low" in stat else (239, 68, 68))
        draw_rounded_rect(draw, [785, by+6, 875, by+30], 4, btn_col)
        draw.text((798, by+10), stat, fill=(255, 255, 255), font=f_sm)
        by += 40

    draw.text((35, 475), "Total Book Titles: 128  |  Total Physical Copies: 742  |  Active Borrowers: 86  |  Total Fines Collected: ₹1,450.00", fill=(148, 163, 184), font=f_sm)

    out_p = os.path.join(assets_dir, "screen3_library.png")
    im.save(out_p)
    print("Saved screen3_library.png")

# 4. SCREEN 4: TIMETABLE & SMART ANTI-PROXY ATTENDANCE
def create_timetable_mockup():
    w, h = 900, 520
    im = Image.new("RGBA", (w, h), (15, 23, 42, 255))
    draw = ImageDraw.Draw(im)

    f_title = get_font(16, bold=True)
    f_reg = get_font(13)
    f_bold = get_font(13, bold=True)
    f_sm = get_font(11)
    f_h1 = get_font(18, bold=True)

    # Title bar
    draw.rectangle([0, 0, w, 48], fill=(30, 41, 59, 255))
    draw.text((20, 14), "NextCampus | Timetable & Anti-Proxy Attendance Session", fill=(241, 245, 249), font=f_title)
    draw_rounded_rect(draw, [w-190, 10, w-20, 38], 6, (16, 185, 129))
    draw.text((w-175, 16), "Start Live Session", fill=(255, 255, 255), font=f_bold)

    # Left: Timetable grid restricted to registered teachers
    draw_rounded_rect(draw, [20, 60, 520, 500], 10, (30, 41, 59), (51, 65, 85), 1)
    draw.text((35, 75), "Department Timetable (TYBBA-CA Sem-5)", fill=(248, 250, 252), font=f_h1)
    draw.text((35, 100), "Teacher assignment strictly bound to registered faculty", fill=(148, 163, 184), font=f_sm)

    # Table Header
    draw.rectangle([35, 125, 505, 155], fill=(51, 65, 85))
    draw.text((45, 132), "Time Slot", fill=(241, 245, 249), font=f_bold)
    draw.text((150, 132), "Subject", fill=(241, 245, 249), font=f_bold)
    draw.text((320, 132), "Assigned Faculty", fill=(241, 245, 249), font=f_bold)

    tt_rows = [
        ("08:00 - 08:50", "Cyber Security", "Prof. A.S.Tanpure (HOD)"),
        ("08:50 - 09:40", "Advanced Java", "Prof. D.S.Morde"),
        ("09:40 - 10:30", "Cloud Computing", "Prof. A.S.Tanpure (HOD)"),
        ("10:30 - 11:00", "-- BREAK --", "-- RECESS --"),
        ("11:00 - 11:50", "Software Testing", "Prof. D.S.Morde"),
        ("11:50 - 01:30", "Project Lab & Viva", "Prof. A.S.Tanpure (HOD)")
    ]
    ty = 160
    for t_slot, subj, fac in tt_rows:
        draw.rectangle([35, ty, 505, ty+35], fill=(24, 33, 47) if (ty//35)%2==0 else (30, 41, 59))
        draw.text((45, ty+10), t_slot, fill=(56, 189, 248), font=f_sm)
        draw.text((150, ty+10), subj, fill=(248, 250, 252), font=f_bold)
        draw.text((320, ty+10), fac, fill=(34, 197, 94) if "HOD" in fac else (203, 213, 225), font=f_sm)
        ty += 40

    draw.text((35, 472), "Note: Teachers dropdown queries users where role IN ('TEACHER','HOD','ADMIN')", fill=(148, 163, 184), font=f_sm)

    # Right: Anti-Proxy Rolling Token Display
    draw_rounded_rect(draw, [540, 60, w-20, 500], 10, (30, 41, 59), (51, 65, 85), 1)
    draw.text((560, 75), "Anti-Proxy Rolling Classroom Token", fill=(248, 250, 252), font=f_h1)
    draw.text((560, 100), "Projected on classroom screen for mobile scan", fill=(148, 163, 184), font=f_sm)

    # Big Rolling Token Card
    draw_rounded_rect(draw, [560, 130, 860, 270], 10, (15, 23, 42), (16, 185, 129), 2)
    draw.text((640, 145), "CURRENT LIVE TOKEN", fill=(148, 163, 184), font=f_sm)
    draw.text((630, 175), "NC - 8 4 9 2", fill=(16, 185, 129), font=get_font(32, bold=True))
    draw.text((615, 235), "⏱ Token Refreshes in: 14 seconds", fill=(234, 179, 8), font=f_bold)

    # Security verification highlights
    draw.text((560, 290), "Anti-Proxy Verification Rules Enforced:", fill=(248, 250, 252), font=f_bold)
    draw.text((560, 320), "1. Cryptographic Rolling Hash (Valid for only 30s)", fill=(203, 213, 225), font=f_sm)
    draw.text((560, 350), "2. Hardware Device Fingerprint (1 Phone = 1 Roll No)", fill=(203, 213, 225), font=f_sm)
    draw.text((560, 380), "3. Subnet IP Binding (Campus Wi-Fi local intranet)", fill=(203, 213, 225), font=f_sm)
    draw.text((560, 410), "4. Real-time Duplicate Prevention (Immediate rejection)", fill=(203, 213, 225), font=f_sm)

    # Live attendee counter
    draw_rounded_rect(draw, [560, 440, 860, 485], 6, (24, 33, 47), (51, 65, 85), 1)
    draw.text((575, 452), "Live Session Attendees: 48 / 52 Students Marked Present (92.3%)", fill=(34, 197, 94), font=f_bold)

    out_p = os.path.join(assets_dir, "screen4_timetable.png")
    im.save(out_p)
    print("Saved screen4_timetable.png")

if __name__ == "__main__":
    create_login_mockup()
    create_student_dashboard_mockup()
    create_library_mockup()
    create_timetable_mockup()
