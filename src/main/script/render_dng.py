import rawpy
from PIL import Image
import sys
from pathlib import Path

if len(sys.argv) < 3:
    print("Usage: render_dng.py <input.dng> <output.jpg>")
    sys.exit(1)

input_path = Path(sys.argv[1])
output_path = Path(sys.argv[2])

print(f"Input path: {input_path}")
print(f"Output path: {output_path}")

if not input_path.exists():
    print(f"ERROR: Input file does not exist: {input_path}")
    sys.exit(1)

try:
    with rawpy.imread(str(input_path)) as raw:
        rgb = raw.postprocess()
        img = Image.fromarray(rgb)
        img.thumbnail((300, 300))  # resize for thumbnail
        img.save(str(output_path))
        print("Thumbnail saved to:", output_path)
except Exception as e:
    print(f"ERROR: Failed to process DNG: {e}")
    sys.exit(1)
