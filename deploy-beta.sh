#!/bin/bash

echo "🚀 Mindful Growth - Beta Deployment Script"
echo "=========================================="

# Clean build
echo "🧹 Cleaning project..."
./gradlew clean

# Run tests
echo "🧪 Running tests..."
./gradlew test
if [ $? -ne 0 ]; then
    echo "❌ Tests failed! Aborting deployment."
    exit 1
fi

# Lint check
echo "🔍 Running lint checks..."
./gradlew lint
if [ $? -ne 0 ]; then
    echo "⚠️  Lint issues found! Review before deploying."
fi

# Build debug APK
echo "🔨 Building debug APK..."
./gradlew assembleDebug
if [ $? -ne 0 ]; then
    echo "❌ Build failed! Aborting deployment."
    exit 1
fi

# Copy APK to output folder
echo "📦 Packaging APK..."
mkdir -p beta-builds
cp app/build/outputs/apk/debug/app-debug.apk beta-builds/mindful-growth-v1.0.0-beta1.apk

echo "✅ Beta build complete!"
echo "📍 APK location: beta-builds/mindful-growth-v1.0.0-beta1.apk"
echo ""
echo "Next steps:"
echo "1. Test on multiple devices"
echo "2. Run through beta test checklist"
echo "3. Collect feedback from testers"
echo "4. Upload to Google Play Console (Internal Testing)"
