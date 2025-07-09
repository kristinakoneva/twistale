# ✍️🌪️🎨 Twistale ✍️🌪️🎨
_Developed by Kristina Koneva (student index number: 249013)_

## 📃 Overview

Twistale is a native mobile Android application inspired by [the "Paper Telephone" game](https://www.thegamegal.com/2011/06/18/paper-telephone/) developed for educational and learning purposes. The app's main goal is to let users create collaborative stories through interactive game rounds.

The host player creates a game room and other players join the same room by entering its game room number. The game starts with everybody writing an initial phrase. In the next round, each player receives a phrase from another participant and must draw something representing it. Once all players finish drawing, they receive a drawing from someone else and must describe it in text.

In a turn-based manner, this cycle continues through multiple rounds, alternating between text and drawings. At the end of the game, the result is a unique story, formed through a mix of text and illustrations, shaped by every player’s contributions along the way.

The system uses a Client-Server architecture. The Android application is the client and Firebase acts as the backend-as-a-service (BaaS).

The Android client is interacting with three core Firebase services:
- [Firebase Authentication](https://firebase.google.com/docs/auth)
- [Cloud Firestore](https://firebase.google.com/docs/firestore)
- [Cloud Storage](https://firebase.google.com/docs/storage).

Additionally, the app also uses Shared Preferences for saving some data on the device’s local storage.

The client-side Android app follows a layer architecture - it contains data, domain and UI layer. 

## 📦 Component Diagram


<img width="70%" alt="Twistale Component Diagram" src="https://github.com/user-attachments/assets/0fb3772c-a082-4fc1-aa47-1b498cfc4f4f" />

## 🕸️ Class Diagram

![Twistale Class Diagram](https://github.com/user-attachments/assets/dffbbbf5-f6dc-4d3e-be01-37c21bd856bb)

## 🎨 User Interface

### 🔒 Authentication Screen

<img width="30%" alt="Register Screen" src="https://github.com/user-attachments/assets/7041bc66-2e86-4ad8-8f2d-716df3884508" display="inline"/>
<img width="30%" alt="Login Screen" src="https://github.com/user-attachments/assets/0793ea93-94e5-4f4c-872e-92fcf290e3dc" display="inline"/>

### 🤸 Game Room Screen

<img width="30%" alt="Game Room idle" src="https://github.com/user-attachments/assets/99992dbd-2e0a-4362-8402-c543bd7be692" display="inline"/>
<img width="30%" alt="Game Room as a host" src="https://github.com/user-attachments/assets/3c81ea61-8243-432c-8253-4ac5a8a0102a" display="inline"/>
<img width="30%" alt="Game Room as a regular player" src="https://github.com/user-attachments/assets/bbe451fd-d3b1-485d-8590-1321d72785e8" display="inline"/>


### 🛝 Game Play Screen

<img width="30%" alt="Intial writing round" src="https://github.com/user-attachments/assets/b2b15b86-1c97-4ca9-b1dc-020098b8cc2a" display="inline"/>
<img width="30%" alt="Waiting" src="https://github.com/user-attachments/assets/4553ddfb-aed7-4d97-b888-51f64beaa424" display="inline"/>

<br>

<img width="30%" alt="Writing round" src="https://github.com/user-attachments/assets/73d215e7-07a3-480d-80f2-08fc42772777" display="inline"/>
<img width="30%" alt="Drawing round" src="https://github.com/user-attachments/assets/062790b6-0b7f-4825-ab0c-626b38526c84" display="inline"/>

### 📖 Game Story Screen

<img width="30%" alt="Story Screen 1" src="https://github.com/user-attachments/assets/6581fd18-6a22-4d02-9292-9ffca605fea1" display="inline"/>
<img width="30%" alt="Story Screen 2" src="https://github.com/user-attachments/assets/2563e25a-2d75-4211-91e9-8f31cc1447d0" display="inline"/>

