"""Shared path to the SQLite file used by Java and Python."""

from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent

DB_FILE = ROOT / "database" / "college_sms.db"
