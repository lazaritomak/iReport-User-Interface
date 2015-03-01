iReport-User-Interface
=

User interface for iReport

**Don't forget to Change the Server's IP address in Functions.java.**

**MAJOR UPDATE!: Removed the 'select category button' and placed the code to the submit button instead, where you get to choose a category first before you submit the report.**



TODO:
=

✔ - Done

✘ - Cannot Do/Buggy/Requires More Technical Expertise

*Activities/Modules for User:*

1. Take Picture/Video ✔
2. Caption media with Text✔
3. Select category ✔
4. Account Creation ✔
5. Account Log In ✔
6. Get current location of device. ✔
7. Submit ✔ 
8. Generate report ID upon sending to data center (server generated). ✔
9. Force Generation of image to Gallery. ✔
10. Check for GPS Service Accessibility. ✔
11. Generation of Report Status (Crude layout). ✔

Minor TODO list:
=

*Aesthetics/Minor Functionalities*

1. Thumbnail for video ✔
2. Limit video duration ✘
3. Validate text content ✔
4. Change image storage to internal ✔
5. Increase video thumbnail size.✔ changed to ✘, due to removal of video feature
6. Change video thumbnail orientation ✔ changed to ✘, due to removal of video feature
7. Fixed orientation and formatting for Status Reports ✘
8. UI is now configured to display properly when viewed in landscape mode ✔
9. Back Button now shows a sign out prompt ✔


APK Location:
=

The apk can be found in 

...app/build/outputs/apk/app-debug.apk


Notes/Details:
=

Place the **iReportDB** folder inside the htdocs folder located in your xampp directory, and change the **IP Address** of the server from **Functions.java**

*Minimum SDK* = 17

*Target SDK* = 21

App is tested on a virtual Google Nexus 7.

Image doesn't seem to show up in emulators. Testing further. (FIXED)

GPS does not work in in Emulators (Genymotion). To test in real Android Phone. (FIXED, must be enabled and tested).
