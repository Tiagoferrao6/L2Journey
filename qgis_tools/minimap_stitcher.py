#!/usr/bin/env python3
import os
import re
from PIL import Image

def stitch_minimaps(input_folder, output_file):
    """
    Reads L2 minimap images from input_folder (e.g. 15_20.bmp)
    and stitches them together into a single global mosaic.
    """
    print(f"Stitching minimaps from {input_folder}...")
    
    # L2 map grid typically spans X: 11 to 26 and Y: 10 to 25 depending on chronicle
    # Let's find min and max from the files
    files = [f for f in os.listdir(input_folder) if f.endswith('.bmp') or f.endswith('.png')]
    
    if not files:
        print("No map files found in the folder.")
        return
        
    tiles = {}
    min_x, max_x = 999, -999
    min_y, max_y = 999, -999
    
    tile_width, tile_height = 0, 0
    
    for f in files:
        match = re.match(r'^(\d+)_(\d+)', f)
        if match:
            x, y = int(match.group(1)), int(match.group(2))
            min_x = min(min_x, x)
            max_x = max(max_x, x)
            min_y = min(min_y, y)
            max_y = max(max_y, y)
            tiles[(x, y)] = os.path.join(input_folder, f)
            
            # get dimensions from the first valid tile
            if tile_width == 0:
                with Image.open(tiles[(x, y)]) as img:
                    tile_width, tile_height = img.size

    if tile_width == 0:
        print("Could not read tile dimensions.")
        return
        
    print(f"Grid detected: X {min_x} to {max_x}, Y {min_y} to {max_y}")
    print(f"Tile size: {tile_width}x{tile_height}")
    
    grid_width = max_x - min_x + 1
    grid_height = max_y - min_y + 1
    
    mosaic_width = grid_width * tile_width
    mosaic_height = grid_height * tile_height
    
    print(f"Creating mosaic canvas of {mosaic_width}x{mosaic_height} pixels...")
    mosaic = Image.new('RGB', (mosaic_width, mosaic_height), (0, 0, 0))
    
    for (x, y), path in tiles.items():
        with Image.open(path) as img:
            # L2 map tiles: X is left-to-right, Y is top-to-bottom
            paste_x = (x - min_x) * tile_width
            paste_y = (y - min_y) * tile_height
            mosaic.paste(img, (paste_x, paste_y))
            
    print(f"Saving global map to {output_file}...")
    # Save as PNG
    mosaic.save(output_file, format="PNG")
    print("Done! You can now georeference this single global map in QGIS.")

if __name__ == "__main__":
    print("=== L2 Minimap Stitcher ===")
    print("Place your extracted map tiles (e.g., 20_20.bmp) in a folder named 'tiles'.")
    if not os.path.exists("tiles"):
        os.makedirs("tiles")
        print("Created 'tiles' folder. Please put your images there and run again.")
    else:
        stitch_minimaps("tiles", "L2_Global_Map.png")
