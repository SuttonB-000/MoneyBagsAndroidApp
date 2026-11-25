# Money Bags – Money Manager

## Overview

Money Bags is a mobile budgeting app that gives users a visual way to organize their money. Each budget category is represented by a "money bag" with its own name and balance. Users can create bags for different purposes, add money to them, and record spending from each bag.

## Features

- **Visual budget categories:** Create named money bags for different purposes.
- **Balance overview:** View each bag's balance and the combined total on the home screen.
- **Money tracking:** Add money to a bag or subtract money to record spending.
- **Bag management:** Create, edit, and delete bags with press-and-hold gestures.
- **Personalization:** Choose dark mode, adjust text size, and change the displayed currency.
- **Help:** Open a brief guide to the app's controls from Settings.

## How to Use

### Create a Money Bag

Press and hold an empty space in the bag area. Enter a name and starting amount, then tap **Add Bag**.

### Add Money or Record Spending

Tap **+$** to add money or **−$** to record spending. Tap the bag you want to update, enter an amount, then tap **Add** or **Subtract**. The bag's balance and the combined total update automatically. The symbols on the buttons reflect the selected currency.

### Edit or Delete a Money Bag

Press and hold a bag to change its name or balance, then tap **Save Changes**. To remove it, tap **Delete** and confirm the deletion.

### Change Settings

Tap **Settings** on the home screen to switch dark mode, adjust text size, or choose a display currency. The **Help** option gives a brief overview of the controls. The **About** section shows the app version.

## Built With

- **Kotlin** for the app's logic.
- **Jetpack Compose** for the user interface.
- **Material 3** for buttons, dialogs, and other interface components.
- **SharedPreferences** for dark mode, text size, and currency settings.
- **Gradle** for the Android build and dependencies.

## Getting Started

Open the `MoneyBags` folder in Android Studio, let Gradle sync, and run the `app` configuration on an emulator or Android device running Android 7.0 (API 24) or later.

## Current Limitations

- Bag names and balances are kept in temporary screen state and are not saved for a fresh app launch.
- Changing the currency changes the symbol and display format; it does not convert balances using exchange rates.
- Subtracting money updates a bag's balance but does not create a spending history.
