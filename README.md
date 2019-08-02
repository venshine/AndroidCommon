AndroidCommon
==
一个简单的开源Android工具类库，提供许多常用的类帮助我们开发程序。

Permission
--

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
```

Proguard
--

``` xml
-keep class com.wx.android.common.** { *; }
-keepclassmembers class com.wx.android.common.** { *; }
-dontwarn com.wx.android.common.**
```

Setup
--
* Download the project from [GitHub](https://github.com/venshine/AndroidCommon.git)
* Import it to your Eclipse workspace or IntelliJ IDEA project
* Set your project properties, then add a android project library, and select AndroidCommon

Usage
--
##### Gradle:
```groovy
compile 'com.wx.android.common:common:1.0.1'
```

Author
--
* Email：venshine.cn@gmail.com

License
--
    Copyright (C) 2015 venshine.cn@gmail.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
    http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
