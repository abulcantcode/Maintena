# Maintena
Fineal Year Project: Vehicle Maintenance Application

# Prerequisites

1) Install Android Studio on your computer.

2) Install the Android SDK and necessary build tools using Android Studio's SDK Manager.

# Step-by-step Installation Guide

1) Download the ZIP file for the code and extract it to a local directory.

2) Open Android Studios and select 'Open an existing Android Studio project'.

3) Navigate to the directory where you cloned or extracted the repository and select the 'Maintena' directory (or 'Maintena-master').

4) Wait for the project to load and for Android Studio to finish indexing the project files.

5) Create a new virtual device by selecting 'Tools > AVD Manager' from the menu bar.

6) Click the 'Create Virtual Device' button, select a device definition that matches your desired screen size and resolution (This app is intended to work on a mobile screen so select any mobile screen)

7) Select a system image to use for the virtual device (e.g., the latest version of Android that your app supports) and click Next. (This applications lowest compatibility is Android 7.0 Nougat)

8) Give the virtual device a name and click 'Finish' to create the device.

9) Run the app on the virtual device by selecting the virtual device from the dropdown menu in the toolbar and clicking the 'Run' button.

10) Wait for Android Studios to build the app and install it on the virtual device.

11) Once the app is installed, you should see the app's login screen on the virtual device.

That's it! You should now be able to use the CarPro Maintenance app on the virtual device.

# Project Structure (from root)

 - app/src/main/java/com/example/maintena - contains all of the Java classes 
 
 - app/src/main/res/layout - contains xml the layout files 

 - app/src/main/res/drawable - contains xml the drawable files 
 
 - app/src/main/res - contains all xml files used in application
 
 - app/build.gradle - contains the dependencies
 
 - app/src/main/AndroidManifest.xml - contains the ManifestFiles
 
 - app/src/google-services.json - contains google-services which allows app to connect to database

# Tips for Testing 

- Whne creating an account, be sure to use an email that can be accessed. You cannot log in without clicking the verify link sent to your email once you have signed up

- Upon seeing 3 horizontal lines in the top right hand of any screen, click to access extra features. 

- When clicking the search button on an auto find card, be sure to use an existing email within the system ( this was the intended design ) 

- Tip for testing is to create two accounts, 1 private account and 1 garage / dealer account

- Auto find button only work with real numberplates 

- Floating button on the bottom right is to add a new record

# Testing

(must be connected to the internet)

1) Upon opening the app, click the sign up text and then select either 'private' or 'garage / dealer' ( It is advised to make one of each account )

2) fill in all the detials and then click create account

3) Go to the email account of the account you just created and click the firebase link to verify your account

4) open up the application and click sign in (if not already in the sign in page)

5) enter login detials and then click sign in (once logged in, you can log out by clicking the three lines on the top right and then selecting log out) 

6) click the red plus on the bottom right to create a new car

7) enter a valid numberplate and then click 'find car' 

8) enter the car name manually ( if you like you can upload a photo using the 'upload picture' button - this opens up your gallery )

9) click 'add car'

10) once the page has switch, a new card should appear, click this card

11) click the red plus button to add a maintenance record

12) Fill in the details

13) select 'auto find' 

14) enter your dealer email in 'contact email' and then click search

15) you can upload an image as the reciept if you like 

16) click add job

17) click on the new card once the screen has changed

18) click the 'request verification' button

19) press the back button on the android system navigation bar

20) press the back button on the top left of the screen untll you get back to the 'my garage' page

21) select the three lines and click 'log out'

22) login with your dealer account details

23) click 'view pending requests' and then click the card 

24) click 'verify' - (if you selected 'reject' , add a comment and then press confirm)

26) press the top left to go back 

27) log out ( or you can select view records to see the record - you can click the card to see further details)

28) login with private account detials

29) click a vehicle card 

30) select the 3 lines on the top right and then select the 'transfer vehicle' option

31) enter the dealer eamil, click the 'search' button 

32) click the 'confirm transfer' button 

33) log out, and then log in as a dealer.

34) select 'my garage'

35) select the menu icon on the top right

36) select "Pending Transfers' option

37) click 'accept' on the desired vehicle

38) navigate back to my garage page

39) the vehicle should have trasnfered along with its maintenance records

-- These steps should touch ovcer all of the main features of this application -- 

