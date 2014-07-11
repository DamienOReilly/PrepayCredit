# Prepay Credit for Android
[![Google Play](https://developer.android.com/images/brand/en_generic_rgb_wo_60.png)](https://play.google.com/store/apps/details?id=damo.three.ie)
[![F-Droid](https://f-droid.org/wiki/images/c/c4/F-Droid-button_available-on.png)](https://f-droid.org/repository/browse/?fdid=damo.three.ie)

**NOTE:** This application, nor its author is not affiliated with Hutchison 3G Ireland.
**NOTE:** These applications are for 3 Ireland Prepay users only.

This application will fetch and display your My3 usage information (For 3 Ireland 3Pay users). Usage information is grouped and sorted. This application will work when you are on 3's GSM network and also when on Wi-Fi.
Please note, https://my3account.three.ie is AWFUL slow. It can have a knock on effect on this application.

This application needs your My3 username/password. You can register at https://my3account.three.ie/Sign_up

# Build with Maven

## Build APK and WAR

1. Have Android SDK "tools", "platform-tools", and "build-tools" directories in your PATH (http://developer.android.com/sdk/index.html)
2. Export ANDROID_HOME pointing to your Android SDK
3. Install Android core libraries (4.4.2_r3) and support libraries (support v4, appcompat v7). I used https://github.com/mosabua/maven-android-sdk-deployer for this.
4. Install maven and from root project directory:
5. Execute 'mvn clean'
6. Execute 'mvn compile'
7. Execute 'mvn package'

# Contribute

Fork and do a Pull Request. If you have nice ideas, I can merge them in.

# Licenses
Prepay Credit for Android is licensed under the GPLv3+.
The file COPYING includes the full license text.

## Details
Prepay Credit for Android is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Prepay Credit for Android is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Prepay Credit for Android.  If not, see <http://www.gnu.org/licenses/>.

## Libraries
* JSoup  
  http://jsoup.org/  
  MIT License: http://jsoup.org/license  

* JSON  
  http://www.json.org/  
  MIT License: http://www.json.org/license.html  
  
* Joda Time  
  http://joda-time.sourceforge.net  
  Apache License v2: http://joda-time.sourceforge.net/license.html

* Apache HttpClient  
  http://hc.apache.org/httpcomponents-client-ga/index.html  
  Apache License v2: http://www.apache.org/licenses/LICENSE-2.0.txt  

* ACRA  
  http://acra.ch/  
  Apache License v2: https://github.com/ACRA/acra/blob/master/LICENSE  
  
* PreferenceFragment-Compat  
  https://github.com/Machinarius/PreferenceFragment-Compat  
  Apache License v2: https://github.com/ACRA/acra/blob/master/LICENSE  
  
* Phone handset logo  
  Creative Commons Attribution 3.0  
  http://creativecommons.org/licenses/by/3.0/legalcode  
  