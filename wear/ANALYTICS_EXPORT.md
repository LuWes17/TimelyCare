# Analytics CSV Export Guide

## Overview
The TimelyCare Wear OS app includes a built-in analytics system that tracks user interactions and exports them as CSV files for research and analysis.

## How to Use

### 1. Start Recording
- Launch the app on your Wear OS watch
- Tap the **green button** at the top of the screen (with circle icon)
- The button will turn **red** to indicate recording is active

### 2. Use the App
- Navigate through screens and interact with the app normally
- All taps, screen views, and durations are automatically tracked

### 3. Stop Recording
- Tap the **red button** at the top of the screen (with square icon)
- A success message will appear showing the filename
- The CSV file is automatically saved to the watch

## Exporting CSV Files

### Check Available Files
```bash
adb shell ls /storage/emulated/0/Android/data/com.example.wear/files/
```

### Download All Analytics Files
```bash
adb pull /storage/emulated/0/Android/data/com.example.wear/files/ ./wear_analytics/
```

### Download a Specific File
```bash
adb pull /storage/emulated/0/Android/data/com.example.wear/files/analytics_a1b2c3d4_20260125_143022.csv ./
```

## CSV Format

The exported CSV contains the following columns:

| Column | Description | Example |
|--------|-------------|---------|
| `date_time` | When the event occurred | `2025-01-19 23:14:53` |
| `session_id` | Unique session identifier | `a1b2c3d4` |
| `event_type` | Type of event | `element_tap`, `screen_view`, `session_start`, `session_end` |
| `screen` | Screen where event occurred | `HOME`, `SETTINGS`, `VITALS` |
| `element` | UI element that was tapped | `icon_button:Settings`, `selector:TextSize_Large` |
| `duration_seconds` | Time spent on screen or total session time | `4.10`, `20.00` |

### Example CSV Output
```csv
date_time,session_id,event_type,screen,element,duration_seconds
2025-01-19 23:14:53,a1b2c3d4,session_start,,,
2025-01-19 23:14:54,a1b2c3d4,screen_view,HOME,,
2025-01-19 23:14:58,a1b2c3d4,element_tap,HOME,icon_button:Settings,
2025-01-19 23:14:58,a1b2c3d4,screen_view,SETTINGS,,4.10
2025-01-19 23:15:13,a1b2c3d4,session_end,,,20.00
```

## File Storage Location

**On Watch:**
```
/storage/emulated/0/Android/data/com.example.wear/files/
```

**Filename Format:**
```
analytics_[8-char-UUID]_[YYYYMMDD_HHmmss].csv
```

## Prerequisites

- ADB (Android Debug Bridge) installed on your computer
- Watch connected via Bluetooth to phone
- Phone connected to computer via USB or wireless debugging enabled
- USB debugging enabled on phone and watch

## Troubleshooting

### "command not found: adb"
Install ADB:
- **With Homebrew:** `brew install android-platform-tools`
- **With Android Studio:** Add to PATH: `export PATH=$PATH:$HOME/Library/Android/sdk/platform-tools`

### "No such file or directory"
- Make sure you've run at least one analytics session (START → use app → END)
- Verify the watch is connected: `adb devices`
- Check if files exist: `adb shell ls /storage/emulated/0/Android/data/com.example.wear/files/`

### Wildcard Not Working
Instead of using `analytics_*.csv`, either:
- Pull the entire directory (recommended)
- Pull each file individually by name

## Questions?

For issues or questions about the analytics system, please refer to the main project documentation.
