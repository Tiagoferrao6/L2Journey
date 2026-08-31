#!/usr/bin/env python3
import os
import struct
import numpy as np
import rasterio
from rasterio.transform import from_origin

def parse_l2j_file(filepath):
    """
    Parses a Lineage 2 .l2j Geodata file and extracts the Z-height matrix.
    L2J geodata usually consists of 256x256 blocks per region file.
    For simplicity in this QGIS tool, we read 16-bit blocks (little endian).
    Format varies slightly by chronicle, but typically:
    - 2 bytes (short) per cell:
      - bits 0-3: NSWE (flags for movement/collision)
      - bits 4-15: Z height
    """
    print(f"Processing {filepath}...")
    
    # 256 * 8 blocks per region = 2048 cells width/height usually for a single L2J file (region)
    # Actually, a region is 256x256 geodata blocks, each block is 8x8 cells.
    # So 2048 x 2048 cells = 4,194,304 cells.
    width = 2048
    height = 2048
    
    elevation_matrix = np.zeros((height, width), dtype=np.int16)
    
    try:
        with open(filepath, 'rb') as f:
            for block_y in range(256):
                for block_x in range(256):
                    # For flat blocks: 1 byte type, 2 bytes height
                    # For complex blocks: 1 byte type, 64 * 2 bytes height
                    # For multilayer: variable.
                    # Since parsing full L2J block logic is massive (due to Flat/Complex/Multilayer types),
                    # we do a simplified placeholder for the Z height extraction.
                    # A full implementation would check block type (0=Flat, 1=Complex, 2=Multilayer).
                    
                    block_type = f.read(1)
                    if not block_type:
                        break
                        
                    b_type = struct.unpack('<B', block_type)[0]
                    
                    if b_type == 0:
                        # Flat block
                        z_val = struct.unpack('<h', f.read(2))[0]
                        z = z_val >> 1 # shift flag
                        for cell_y in range(8):
                            for cell_x in range(8):
                                cy = block_y * 8 + cell_y
                                cx = block_x * 8 + cell_x
                                elevation_matrix[cy, cx] = z
                    elif b_type == 1:
                        # Complex block (64 cells)
                        for cell_y in range(8):
                            for cell_x in range(8):
                                z_val = struct.unpack('<h', f.read(2))[0]
                                z = z_val >> 1 # shift flag
                                cy = block_y * 8 + cell_y
                                cx = block_x * 8 + cell_x
                                elevation_matrix[cy, cx] = z
                    elif b_type == 2:
                        # Multilayer block
                        # Skip processing layers for this simple Z-map viewer, just grab top layer or skip
                        for cell_y in range(8):
                            for cell_x in range(8):
                                layers = struct.unpack('<B', f.read(1))[0]
                                for _ in range(layers):
                                    f.read(2) # skip layers
    except Exception as e:
        print(f"Error parsing {filepath}: {e}")
        
    return elevation_matrix

def create_geotiff(matrix, out_path, region_x, region_y):
    """
    Saves the elevation matrix as a GeoTIFF using rasterio.
    """
    height, width = matrix.shape
    
    # Calculate real L2 Cartesian coordinates
    # Grid starts at region 11_10 (X: -131072, Y: -163840)
    # Each region is 32768 x 32768 in-game units.
    # Geodata cells are 16x16 units.
    l2_x = (region_x - 20) * 32768
    l2_y = (region_y - 18) * 32768
    
    # from_origin(west, north, xsize, ysize)
    # L2 uses Y axis pointing south usually.
    transform = from_origin(l2_x, l2_y, 16, 16)
    
    with rasterio.open(
        out_path,
        'w',
        driver='GTiff',
        height=height,
        width=width,
        count=1,
        dtype=matrix.dtype,
        crs='EPSG:3857', # Pseudo-Mercator just for QGIS plotting placeholder
        transform=transform,
    ) as dst:
        dst.write(matrix, 1)
        
    print(f"Saved GeoTIFF to {out_path}")

if __name__ == "__main__":
    print("=== L2J Geodata to GeoTIFF Converter ===")
    # Example usage:
    # matrix = parse_l2j_file("20_20.l2j")
    # create_geotiff(matrix, "20_20_elevation.tif", 20, 20)
    print("Put your .l2j files in the same folder and edit the script to batch process them.")
