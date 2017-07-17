## LoyaltyCard Vendor App

**LoyaltyCard is a loyalty card system for Android. It consists of a Android application for customers, and Android application for vendors, and a backend API deployed using firebase cloud functions. The system requires a firebase account.**
<br>
<br>

Links to vendor app and cloud-functions repositories:
<br>
<br>
[LoyaltyCard Customer App](https://github.com/fullstacknz/LoyaltyCard-Customer)

[LoyaltyCard Cloud Functions](https://github.com/fullstacknz/cloud-functions)

<br>
<br>

### Setup Instructions

This project was created in Android Studio and requires a Firebase App to run. To create a Firebase Application you can visit the Firebase Website and sign in with your Google account.

[https://firebase.google.com/](https://firebase.google.com/)

After signing in create a new app and choose the option to 'Add another app'. Follow the instructions to connect the this Android app to your Firebase app.
<br>
<br>
LoyaltyCard will not run as intended if you do not provide your SHA-1 certificate to Firebase. This is also true for running LoyaltyCard in your development environment.

#### Required Files

For the app to run you must add the **google-services.json** file that is provided by Firebase to the **./app** subdirectory of the project.
<br>
<br>

**It is recommended that you set up cloud functions using the link provided above before continuing.**

The project also requires that a Java Class file called **CONFIG.java** is added to the path

`./app/src/main/java/card/loyalty/loyaltycardvendor/`. 

The contents of this file needs to structured as follows

        package card.loyalty.loyaltycardvendor;

        public class CONFIG {

            public static final String PUSH_CLOUD_FUNCTION = ""; // url for push cloud function goes here

        }

You will need to fill in the missing string value with the url for your cloud function once you have set it up. This can be found in the Firebase console under **Functions**

<br>
The application should now be able to run in Android Studio.

<br>

### Using LoyaltyCard Vendor App

After launching the app the vendor will be required to sign up using either email or a Google account. The email address is not verified, but each **user** is required to have a unique email. One account will sign in to both the vendor app and the customer app.

<br>

After sign in the vendor should provide their details. After providing details they should add an offer to get started. The details entered for this offer will be visible to this vendor's customers.

<br>

After creating an offer it will be visible from the offers screen. A vendor should scan a customer's QR code with every purchase relating to that offer. The customer will accumulate rewards. To launch the scanner the vendor can tap the intended offer.

<br>

To send out additional promotions to customer's that have visited the vendor, they can select push promotion from the navigation drawer, fill in the required form details then press send. The customer should soon receive a notification that they have been sent a promotion. This notification will reach them regardless of whether they currently have their app running in the foreground.
