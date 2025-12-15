# MediShelf
**A Medical Bookstore Android Application**

MediShelf is an e-commerce Android application developed **individually** as a semester project for APT 3060 at USIU. It simulates a bookstore workflow, allowing users to browse medical textbooks, manage a cart, and simulate payments via MPESA.

## Features
* **User Authentication:** Registration and Login using Firebase Auth.
* **Product Catalog:** Browse medical books (e.g., Gray's Anatomy, Robbins & Cotran).
* **Shopping Cart:** Add items, adjust quantities, and calculate totals.
* **Checkout Flow:** Address capture and simulated MPESA STK Push (Time-delayed mock to demonstrate success flow).
* **Admin Dashboard:** A hardcoded admin view to monitor orders in real-time.

## Tech Stack
* **Language:** Java
* **Backend:** Firebase Realtime Database
* **Payment:** Local UI Simulation (Mocking the behavior of Safaricom Daraja API)
* **IDE:** Android Studio

## Setup & Configuration
To run this project locally, you will need to:
1.  Clone the repo.
2.  Add your own `google-services.json` file from Firebase Console to the `app/` folder.
3.  (Optional) The `MpesaUtils.java` file contains placeholders for API keys if you wish to implement live payments later.

---
**Author:** Mohamed Sheikh
*Disclaimer: This project was created for educational purposes as a classroom demo.*
