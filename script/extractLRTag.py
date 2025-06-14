import sqlite3
import json
import re
from collections import defaultdict

# Path to your Lightroom catalog
db_path = "Y:/70_Catalog_Phototheque/70_Catalog_Phototheque-2-v12.lrcat"

# Connect to the database
conn = sqlite3.connect(db_path)
cursor = conn.cursor()

# Query to get all keywords
cursor.execute("SELECT id_local, name, dateCreated, parent FROM AgLibraryKeyword")
rows = cursor.fetchall()
conn.close()

# Convert rows into dictionaries
keywords = [
    {
        "id": row[0],
        "name": row[1] if row[1] is not None else "",  # Replace None with empty string
        "dateCreated": row[2],
        "parent": row[3]
    }
    for row in rows
]

# Group keywords by parent
grouped = defaultdict(list)
for kw in keywords:
    grouped[kw["parent"]].append(kw)

# Sort each group alphabetically by name (case-insensitive)
for parent_id in grouped:
    grouped[parent_id].sort(key=lambda x: x["name"].lower() if isinstance(x["name"], str) else "")

# Recursive function to build hierarchy
def build_hierarchy(parent_id):
    return [
        {
            "id": kw["id"],
            "name": kw["name"],
            "dateCreated": kw["dateCreated"],
            "children": build_hierarchy(kw["id"])
        }
        for kw in grouped.get(parent_id, [])
    ]

# Build initial hierarchy
full_hierarchy = build_hierarchy(None)

# Remove the first node if it has an empty name and only one top-level node
if len(full_hierarchy) == 1 and full_hierarchy[0]["name"] == "":
    tag_hierarchy = full_hierarchy[0]["children"]
else:
    tag_hierarchy = full_hierarchy

# Save to JSON
with open("./script/lightroom_tags_grouped.json", "w", encoding="utf-8") as f:
    json.dump(tag_hierarchy, f, indent=2, ensure_ascii=False)