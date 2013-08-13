# My3 Prepay for Android
[![Google Play](http://developer.android.com/images/brand/en_generic_rgb_wo_45.png)](https://play.google.com/store/apps/details?id=damo.three.ie)

Note these applications are for 3 Ireland Prepay users only.
This application will fetch and display your My3 3Pay usage information. Usage information is grouped and sorted. This application will work when you are on 3's GSM network and also when on Wi-Fi.
Please note, https://my3account.three.ie is AWFUL slow. It can have a knock on effect on this application.
This application needs your My3 username/password. You can register at https://my3account.three.ie (Note: don't access this URL when on 3's network, as it will simply bypass the registration screen and directly show you your account page as it validates you via your GSM session. Access it via Wi-Fi on your handset or use a PC/Laptop)
You need your SIM Serial to register with My3. If you need your Sim Serial (ICCID), hit refresh once, and the application will show it for you, if you have no login details entered.
This application will use an intermediate server to speed up usage retrieval by default. This is most noticeable when you are on a 2G connection, and to some extend a 3G connection. The intermediate server is secure.damienoreilly.org and traffic is encrypted over TLS. The intermediate server is not used when you are on Wi-Fi,
You can set whether you want to use the intermediate server or connect directly to my3account.three.ie from settings.
This application, nor its author is not affiliated with Hutchison 3G Ireland.

# Build with Maven

## Build APK and WAR

1. Have Android SDK "tools", "platform-tools", and "build-tools" directories in your PATH (http://developer.android.com/sdk/index.html)
2. Export ANDROID_HOME pointing to your Android SDK
3. Install maven and from root project directory:
4. Execute 'mvn clean'
5. Execute 'mvn compile'
6. Execute 'mvn package'

# Contribute

Fork and do a Pull Request. If you have nice ideas, I can merge them in.

# Licenses
My3 Prepay for Android is licensed under the GPLv3+.  
The file COPYING includes the full license text.

## Details
My3 Prepay for Android is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

My3 Prepay for Android is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with My3 Prepay for Android.  If not, see <http://www.gnu.org/licenses/>.

## Libraries
* ActionBarSherlock  
  http://actionbarsherlock.com/  
  Apache License v2: https://github.com/JakeWharton/ActionBarSherlock/blob/master/LICENSE.txt 

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
  Apache License v2: http://joda-time.sourceforge.net/license.html
