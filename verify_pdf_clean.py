import pymupdf

doc = pymupdf.open("NextCampus_Project_Report_TYBBA_CA.pdf")
print(f"Total pages: {len(doc)}")
found_q = []
for idx, page in enumerate(doc):
    text = page.get_text()
    if "?" in text:
        found_q.append((idx + 1, text))

if found_q:
    print(f"WARNING: Found ? on pages: {[p for p, _ in found_q]}")
    for p, txt in found_q:
        print(f"--- Page {p} ---")
        for line in txt.split("\n"):
            if "?" in line:
                print("  Line:", line)
else:
    print("SUCCESS: EXACT ZERO '?' QUESTION MARKS ACROSS ENTIRE PDF!")
