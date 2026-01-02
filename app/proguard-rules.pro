# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Preserve line number information for debugging stack traces.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep all annotations
-keepattributes *Annotation*

# Apache HttpClient - ignore warnings about missing classes
-dontwarn org.apache.http.**
-dontwarn org.apache.commons.**
-dontwarn org.apache.http.client.**
-dontwarn org.apache.http.conn.**
-dontwarn org.apache.http.impl.**

# Google API Client
-dontwarn com.google.api.client.extensions.android.**
-dontwarn com.google.api.client.googleapis.extensions.android.**
-keep class com.google.api.client.** { *; }
-keepclassmembers class * {
  @com.google.api.client.util.Key <fields>;
}

# YouTube API
-keep class com.google.api.services.youtube.** { *; }
-keep class com.google.api.services.youtube.model.** { *; }
-keepclassmembers class com.google.api.services.youtube.model.** {
  @com.google.api.client.util.Key <fields>;
  <fields>;
}

# Google HTTP Client (used by YouTube API)
-keep class com.google.api.client.http.** { *; }
-keep class com.google.api.client.json.** { *; }
-dontwarn com.google.api.client.http.**
-dontwarn com.google.api.client.json.**

# Retrofit
-keepattributes Signature
-keepattributes Exceptions
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep data classes and model classes
-keep class com.byteutility.dev.leetcode.plus.data.model.** { *; }
-keep class com.byteutility.dev.leetcode.plus.domain.model.** { *; }

# Keep network request/response VOs (safeguard for Gson deserialization)
-keep class com.byteutility.dev.leetcode.plus.network.responseVo.** { *; }
-keep class com.byteutility.dev.leetcode.plus.network.requestVO.** { *; }

# TikXML
-keep class com.tickaroo.tikxml.** { *; }
-keep @com.tickaroo.tikxml.annotation.Xml class * { *; }
-keep @com.tickaroo.tikxml.annotation.Element class * { *; }
-keep @com.tickaroo.tikxml.annotation.Attribute class * { *; }
-keepclassmembers class * {
    @com.tickaroo.tikxml.annotation.* <fields>;
}

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.byteutility.dev.leetcode.plus.**$$serializer { *; }
-keepclassmembers class com.byteutility.dev.leetcode.plus.** {
    *** Companion;
}
-keepclasseswithmembers class com.byteutility.dev.leetcode.plus.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Hilt
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Jetpack Compose
-keep class androidx.compose.** { *; }
-keep class androidx.compose.runtime.** { *; }
-dontwarn androidx.compose.**

# Keep custom views
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Sora Editor (Rosemoe)
-keep class io.github.rosemoe.sora.** { *; }
-keep class io.github.rosemoe.sora.widget.** { *; }
-keep class io.github.rosemoe.sora.lang.** { *; }
-keep class io.github.rosemoe.sora.text.** { *; }
-keep class io.github.rosemoe.sora.graphics.** { *; }
-keep class io.github.rosemoe.sorern.** { *; }
-dontwarn io.github.rosemoe.sora.**

# TextMate (used by Sora Editor for syntax highlighting)
-keep class org.eclipse.tm4e.** { *; }
-keep class org.eclipse.jdt.** { *; }
-dontwarn org.eclipse.tm4e.**
-dontwarn org.eclipse.jdt.**

# JONI regex library (used by TextMate)
-keep class org.jcodings.** { *; }
-keep class org.joni.** { *; }
-dontwarn org.jcodings.**
-dontwarn org.joni.**

# Snakeyaml (may be used for TextMate grammar parsing)
-keep class org.yaml.snakeyaml.** { *; }
-dontwarn org.yaml.snakeyaml.**