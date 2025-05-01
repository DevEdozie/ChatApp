# 🗨️ ChatApp - Built with Jetpack Compose

ChatApp is a modern real-time chat application developed using **Jetpack Compose** and powered by **Firebase** for authentication and real-time messaging. It also supports offline caching and persistent local user preferences, giving users a seamless and responsive chatting experience.

---

## ✨ Features

✅ **Firebase Authentication** – Secure user login and signup using email and password.  
✅ **Real-time messaging** – Messages are synced instantly via **Firestore**, ensuring up-to-date chats across devices.  
✅ **Offline support** – Uses **Room** for local caching of chat threads and messages, so conversations are available even when offline.  
✅ **Compose UI** – Entire app UI is built using **Jetpack Compose**, ensuring reactive, declarative, and modern Android development practices.  
✅ **DataStore Integration** – Persistent storage for user data such as profile image and email.  
✅ **Profile Management** – Users can update and view their profile picture and email.  
✅ **Typing Indicator** – See when the other user is typing in real-time.  
✅ **On-the-fly Chat Creation** – Start chatting with any registered user by entering their email.  
✅ **Chat Notifications** – Users are notified when a new chat thread is created with them.  

---

## 🛠️ Tech Stack

- **Jetpack Compose** – Modern UI toolkit for native Android.
- **Firebase Authentication** – Secure and scalable user auth.
- **Firebase Firestore** – Cloud NoSQL database for real-time sync.
- **Room Database** – Local database for caching messages and threads.
- **DataStore** – Local key-value storage for user preferences.
- **Hilt** – Dependency injection.
- **MVVM Architecture** – Clean and testable architecture.
- **Kotlin Coroutines & Flow** – Asynchronous and reactive programming.

---



## 🚀 Getting Started

### Prerequisites
- Android Studio (Giraffe or later)
- Firebase project setup with Authentication and Firestore enabled
- Google Services JSON file in your `/app` directory

### Clone the repository
```bash
git clone https://github.com/DevEdozie/ChatApp.git
