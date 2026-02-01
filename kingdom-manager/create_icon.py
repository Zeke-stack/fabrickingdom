import tkinter as tk
from PIL import Image, ImageDraw
import os

def create_icon():
    """Create a simple icon for the application"""
    # Create a 64x64 image
    size = 64
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Draw a simple castle icon
    # Base
    draw.rectangle([10, 40, 54, 54], fill=(139, 69, 19, 255))  # Brown base
    
    # Towers
    draw.rectangle([10, 20, 18, 40], fill=(105, 105, 105, 255))  # Left tower
    draw.rectangle([46, 20, 54, 40], fill=(105, 105, 105, 255))  # Right tower
    draw.rectangle([28, 15, 36, 40], fill=(105, 105, 105, 255))  # Center tower
    
    # Tower tops
    draw.polygon([(8, 20), (18, 20), (13, 10)], fill=(178, 34, 34, 255))  # Left roof
    draw.polygon([(46, 20), (56, 20), (51, 10)], fill=(178, 34, 34, 255))  # Right roof
    draw.polygon([(26, 15), (38, 15), (32, 5)], fill=(178, 34, 34, 255))  # Center roof
    
    # Gate
    draw.rectangle([25, 35, 39, 54], fill=(139, 69, 19, 255))  # Gate
    draw.rectangle([28, 38, 36, 52], fill=(101, 67, 33, 255))  # Gate inner
    
    # Windows
    draw.rectangle([12, 25, 16, 30], fill=(255, 255, 200, 255))  # Left window
    draw.rectangle([48, 25, 52, 30], fill=(255, 255, 200, 255))  # Right window
    draw.rectangle([30, 20, 34, 25], fill=(255, 255, 200, 255))  # Center window
    
    # Save as ICO
    img.save('icon.ico', format='ICO', sizes=[(64, 64), (32, 32), (16, 16)])
    print("Icon created successfully!")

if __name__ == "__main__":
    create_icon()
