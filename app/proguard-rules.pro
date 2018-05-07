# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\hello\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keep public class * implements com.bumptech.glide.module.GlideModule
      -keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
          **[] $VALUES;
          public *;
      }

-dontwarn javax.xml.bind.DatatypeConverter
      -dontwarn retrofit2.**
-dontwarn org.apache.commons.**
-keep class org.apache.http.** { *; }
-dontwarn org.apache.http.**
-dontwarn com.squareup.okhttp.**
      -keep class com.fasterxml.jackson.databind.ObjectMapper {
          public <methods>;
          protected <methods>;
      }
      -keep class com.fasterxml.jackson.databind.ObjectWriter {
          public ** writeValueAsString(**);
      }
      -keepnames class com.fasterxml.jackson.** { *; }
      -dontwarn com.fasterxml.jackson.databind.**



       -keep public class com.couchbase.lite.** { *; }
            -dontwarn com.couchbase.lite.**
            -keep public class com.fasterxml.jackson.** { *; }
            -dontwarn com.fasterxml.jackson.**

            -keep class android.support.v4.** { *; }
            -keep interface android.support.v4.** { *; }

            -dontwarn android.support.v7.**
            -keep class android.support.v7.** { *; }
            -keep interface android.support.v7.** { *; }