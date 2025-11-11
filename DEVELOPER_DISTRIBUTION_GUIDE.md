# TimelyCare App - Developer Distribution Guide

## Pre-Distribution Checklist

### 1. Final Development Steps
- [ ] Complete all app features and functionality
- [ ] Test both mobile and watch apps thoroughly on emulators
- [ ] Verify automatic watch app installation works
- [ ] Update version codes in both `app/build.gradle.kts` and `wear/build.gradle.kts`
- [ ] Update app names and package IDs if needed

### 2. Code Cleanup
```bash
# Fix any lint issues
./gradlew lint

# Run tests if available
./gradlew test

# Clean project
./gradlew clean
```

## Building Release APKs

### Option 1: Android Studio (Recommended)
1. **Build > Generate Signed Bundle / APK...**
2. Select **APK**
3. Choose **Create new keystore** (first time) or use existing
4. **Keystore Information:**
   - Save keystore file securely (you'll need it for updates)
   - Use strong password
   - Fill in certificate details
5. Select **release** build variant
6. Check **V1 (Jar Signature)** and **V2 (Full APK Signature)**
7. Click **Finish**

### Option 2: Command Line
```bash
# Generate release APK (requires keystore setup)
./gradlew assembleRelease

# Find APKs at:
# app/build/outputs/apk/release/app-release.apk
# wear/build/outputs/apk/release/wear-release.apk
```

### 3. Verify Automatic Installation Setup
- The mobile APK should contain the embedded wear APK
- Check `app/src/main/res/raw/` for `wearapp.apk` after build
- Mobile APK size should be significantly larger (contains watch app)

## Distribution Process

### 1. Prepare Distribution Files
```
TimelyCare_Study_v1.0/
├── TimelyCare_mobile.apk          # Main APK for participants
├── PARTICIPANT_INSTRUCTIONS.md    # Installation guide
└── study_materials/               # Any additional study documents
    ├── consent_form.pdf
    └── study_protocol.pdf
```

### 2. Upload to Distribution Platform

#### Option A: Google Drive
1. Create folder: "TimelyCare Study Distribution"
2. Upload all files
3. Set sharing permissions to "Anyone with link can view"
4. Copy shareable link

#### Option B: Email Distribution
1. Compress files if needed (APK may be large)
2. Send individual emails with:
   - APK attachment
   - PARTICIPANT_INSTRUCTIONS.md
   - Brief installation overview in email body

### 3. Send to Participants
**Email Template:**
```
Subject: TimelyCare Study App - Installation Instructions

Hi [Participant Name],

Thank you for participating in the TimelyCare study!

Please find attached:
- TimelyCare_mobile.apk (the app to install)
- PARTICIPANT_INSTRUCTIONS.md (detailed installation guide)

IMPORTANT: Only install the mobile app on your phone. The watch app will install automatically.

Installation Summary:
1. Enable "Install from unknown sources" in your phone settings
2. Download and install the APK on your phone
3. Wait for automatic watch app installation (2-5 minutes)
4. Open apps on both devices

If you encounter any issues, please contact me immediately at [your email].

Best regards,
[Your name]
```

## Testing Distribution

### Before Sending to Participants
1. **Test Installation Process:**
   - Install APK on a test device (or ask a colleague)
   - Verify watch app installs automatically
   - Test both apps work correctly

2. **Check File Integrity:**
   - Ensure APK isn't corrupted during upload/download
   - Test download link works from different devices

### Backup Plan
- Keep unsigned APK ready in case participants have signing issues
- Have alternative distribution method ready (different cloud service)
- Test installation instructions yourself first

## During Study Distribution

### Participant Support
- Monitor for installation issues in first 24 hours
- Be available for troubleshooting
- Keep track of successful installations vs. issues

### Common Issues & Solutions
- **"Can't install APK"** → Guide through enabling unknown sources
- **"Watch app not installing"** → Check Wear OS app connection
- **"App won't open"** → Verify device compatibility

## Post-Distribution

### Version Updates
- If you need to release updates, use same keystore
- Increment version codes in build files
- Test update process doesn't break automatic watch installation

### Data Collection
- Monitor app usage/crashes if you've implemented analytics
- Collect participant feedback on installation process

---

## Quick Reference Commands

```bash
# Build release APK
./gradlew assembleRelease

# Check build outputs
ls app/build/outputs/apk/release/
ls wear/build/outputs/apk/release/

# Verify embedded watch app
ls app/src/main/res/raw/
```

**Remember:** Keep your keystore file and passwords secure! You'll need them for any future app updates.