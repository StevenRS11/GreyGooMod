#!/usr/bin/env python3
"""Split texture atlases into 16x16 PNG tiles.

Usage:
    python split_textures.py input.png [input2.png ...] [-o OUTPUT_DIR]

If OUTPUT_DIR is not provided, tiles will be written next to the
input file inside a folder named after it.
"""
import argparse
from pathlib import Path
from PIL import Image


def split_image(path: Path, output_dir: Path) -> None:
    img = Image.open(path)
    width, height = img.size
    if width % 16 or height % 16:
        raise ValueError(f"Image {path} size {width}x{height} is not divisible by 16")
    cols, rows = width // 16, height // 16
    output_dir.mkdir(parents=True, exist_ok=True)
    count = 0
    for y in range(rows):
        for x in range(cols):
            tile = img.crop((x * 16, y * 16, (x + 1) * 16, (y + 1) * 16))
            tile_path = output_dir / f"tile_{count:03d}.png"
            tile.save(tile_path)
            count += 1
    print(f"Wrote {count} tiles to {output_dir}")


def main():
    parser = argparse.ArgumentParser(description="Split texture atlases into 16x16 tiles")
    parser.add_argument('images', nargs='+', help='Input PNG files to split')
    parser.add_argument('-o', '--output', help='Directory to place extracted tiles')
    args = parser.parse_args()

    for image_path in args.images:
        p = Path(image_path)
        if not p.is_file():
            print(f"Skipping missing file: {p}")
            continue
        out_dir = Path(args.output) if args.output else p.with_suffix('')
        split_image(p, out_dir)


if __name__ == '__main__':
    main()
