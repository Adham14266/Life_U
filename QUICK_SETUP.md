# Quick Setup Guide - Geminia Enhancements

## For Android Developers (5 minutes)

### Step 1: Add Component Files
Copy these files to `app/src/main/java/com/example/ui/components/`:
- ✅ EnhancedChatMessage.kt
- ✅ AnimationComponents.kt  
- ✅ ScheduleComponents.kt

### Step 2: Update TutorScreen.kt
Add imports at the top:
```kotlin
import com.example.ui.components.EnhancedChatMessage
import com.example.ui.components.EnhancedThinkingIndicator
import com.example.ui.components.ScheduleQuickView
import com.example.ui.components.ChatScheduleWidget
```

### Step 3: Use Enhanced Components
Replace thinking indicator:
```kotlin
if (viewModel.isStitchThinking) {
    item {
        EnhancedThinkingIndicator()
    }
}
```

### Step 4: Build & Test
```bash
./gradlew clean build
./gradlew installDebug
```

---

## For Backend Developers (10 minutes)

### Step 1: Add Service Files
Copy to `backend/src/StudyApp.Application/`:
- ✅ DTOs/EnhancedChatDto.cs
- ✅ Interfaces/IEnhancedChatService.cs
- ✅ Services/EnhancedChatService.cs

### Step 2: Update Program.cs
```csharp
services.AddScoped<IEnhancedChatService, EnhancedChatService>();
```

### Step 3: Add Controller Endpoints
See `BACKEND_ENHANCEMENTS.md` for full code

### Step 4: Test
```bash
dotnet build
dotnet test
```

---

## Files Overview

### Mobile (3 files, ~1,100 lines)
```
EnhancedChatMessage.kt (267 lines)
├─ EnhancedChatMessage component
├─ EnhancedThinkingIndicator
└─ ScheduleQuickView

AnimationComponents.kt (350 lines)
├─ AnimatedGradientBackground
├─ TypingIndicator
├─ ShimmerLoading
└─ 5+ more animation utilities

ScheduleComponents.kt (450 lines)
├─ ChatScheduleWidget
├─ ClassEventItem
└─ FullScheduleView
```

### Backend (3 files, ~500 lines)
```
EnhancedChatDto.cs (90 lines)
├─ EnhancedChatMessageDto
├─ ScheduleReferenceDto
├─ CodeBlockDto
└─ ChatSessionDto

EnhancedChatService.cs (400 lines)
├─ Enhanced message processing
├─ Schedule integration
├─ Tag extraction
└─ Smart prompts generation

IEnhancedChatService.cs (15 lines)
└─ New interface
```

---

## Verification Checklist

After setup:
- [ ] Mobile app compiles without errors
- [ ] New components appear in IntelliSense
- [ ] Thinking indicator animation plays smoothly
- [ ] Backend builds successfully
- [ ] New API endpoints return 200 OK
- [ ] Messages include metadata (tags, type)
- [ ] Schedule displays in chat

---

## Common Issues & Fixes

| Issue | Fix |
|-------|-----|
| `Cannot find symbol: EnhancedChatMessage` | Ensure import is added and file copied to correct location |
| `IEnhancedChatService not found` | Register in DI: `services.AddScoped<IEnhancedChatService, EnhancedChatService>()` |
| Animations stutter | Reduce animation count on low-end device, check frame rate |
| Schedule not showing | Ensure ClassEvent repository is injected |

---

## Performance Tips

- Mobile: Use LazyColumn to avoid rendering all messages
- Backend: Cache schedule for 5 minutes
- Both: Monitor memory usage, especially animations

---

## Support Resources

- **Mobile Guide**: See `GEMINIA_ENHANCEMENTS.md`
- **Backend Guide**: See `BACKEND_ENHANCEMENTS.md`
- **Full Summary**: See `ENHANCEMENT_SUMMARY.md`

---

✅ **Ready to enhance Geminia!**
