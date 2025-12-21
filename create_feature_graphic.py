from PIL import Image, ImageDraw, ImageFont
import os

# Create a 1024x500 image
width = 1024
height = 500
image = Image.new('RGB', (width, height), '#1a1a2e')

draw = ImageDraw.Draw(image)

# Define colors based on the app
purple = '#9d4edd'
green = '#4CAF50'
light_purple = '#c77dff'
white = '#ffffff'
yellow = '#ffd60a'

# Draw gradient-like background with circles
for i in range(5):
    alpha = int(30 - i * 5)
    color = f'#{purple[1:]}'
    draw.ellipse([50 + i*40, 100 + i*30, 350 + i*40, 400 + i*30],
                 outline=purple, width=2)

# Draw target/goal icon on the left
center_x, center_y = 200, 250
for radius in [80, 60, 40, 20]:
    draw.ellipse([center_x - radius, center_y - radius,
                  center_x + radius, center_y + radius],
                 outline=light_purple, width=3)

# Draw arrow hitting center
draw.polygon([(center_x + 60, center_y - 10),
              (center_x + 20, center_y),
              (center_x + 60, center_y + 10)],
             fill=yellow)
draw.rectangle([center_x + 20, center_y - 3, center_x + 100, center_y + 3],
               fill=yellow)

# Try to use a nice font, fall back to default if not available
try:
    title_font = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 70)
    subtitle_font = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 32)
    feature_font = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 24)
except:
    title_font = ImageFont.load_default()
    subtitle_font = ImageFont.load_default()
    feature_font = ImageFont.load_default()

# Draw main title
title = "LeetCode Plus"
title_bbox = draw.textbbox((0, 0), title, font=title_font)
title_width = title_bbox[2] - title_bbox[0]
draw.text((width - title_width - 100, 80), title, fill=white, font=title_font)

# Draw subtitle
subtitle = "Master Your Coding Journey"
subtitle_bbox = draw.textbbox((0, 0), subtitle, font=subtitle_font)
subtitle_width = subtitle_bbox[2] - subtitle_bbox[0]
draw.text((width - subtitle_width - 100, 170), subtitle, fill=light_purple, font=subtitle_font)

# Draw feature list
features = [
    "📊  Track Your Progress",
    "🎯  Set Weekly Goals",
    "🏆  Contest Reminders",
    "💡  Problem Solutions"
]

y_pos = 250
for feature in features:
    draw.text((480, y_pos), feature, fill=white, font=feature_font)
    y_pos += 45

# Add a subtle accent line
draw.rectangle([450, 220, 460, 420], fill=green)

# Save the image
output_path = '/Users/pathao/Desktop/2025/LeetcodePlus/LeetcodePlus/ss/feature_graphic_1024x500.png'
image.save(output_path)
print(f"Feature graphic saved to: {output_path}")
