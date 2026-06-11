# Add project specific ProGuard rules here.
-keep class com.worldcup.albumtracker.data.model.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Retrofit / Gson
-keep class com.google.gson.** { *; }
-keep class retrofit2.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
