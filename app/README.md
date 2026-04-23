---
# 📱 Task Manager Android App (Firebase-Based)
---
## 📌 Overview
This is a real-time task management Android application built using Java and Firebase. The app allows users to securely log in, manage tasks, and sync data across devices instantly using cloud storage.

---

## 🚀 Features

### 🔐 Authentication
- User Signup & Login (Email & Password)
- Secure session handling
- Auto-login support
- Logout functionality

### 📝 Task Management
- Add new tasks
- Edit existing tasks
- Delete tasks (long press)
- Mark tasks as completed ✅
- Strike-through completed tasks

### 📂 Categories
- Organize tasks into:
    - Work
    - Personal
    - Study

### 🔍 Search
- Real-time task filtering

### ☁️ Cloud Sync (Firebase)
- Real-time database using Firebase Firestore
- Instant updates across devices
- User-specific task storage

### 🔔 Notifications
- Notification when a task is added

### 🌙 UI/UX
- Dark mode enabled
- Material Design UI
- RecyclerView for efficient list rendering
- Splash screen for professional launch

---

## 🛠️ Tech Stack

- **Language:** Java
- **IDE:** Android Studio
- **Backend:** Firebase Firestore
- **Authentication:** Firebase Auth
- **UI Components:** RecyclerView, Material Design
- **Database:** Cloud-based (No local DB required)

---

## 📂 Project Structure
```
app/ │ ├── ui/ │   ├── MainActivity.java │   ├── LoginActivity.java │   ├── SplashActivity.java │   ├── TaskAdapter.java │ ├── database/ │   ├── FirebaseHelper.java │ ├── model/ │   ├── Task.java │ ├── res/ │   ├── layout/ │   ├── values/ │   ├── mipmap/ │ └── AndroidManifest.xml
```
---

## 🔥 How It Works

1. User logs in or signs up
2. Each user gets a unique ID (UID)
3. Tasks are stored in:

users/{uid}/tasks

4. Firebase Firestore syncs data in real-time
5. RecyclerView updates instantly

---

## ⚙️ Setup Instructions

1. Clone the repository
2. Open in Android Studio
3. Add your Firebase project:
    - Download `google-services.json`
    - Place inside `app/`
4. Enable Firebase Authentication (Email/Password)
5. Sync Gradle
6. Run the app

---

## 📸 Screens (Optional)
- Login Screen
- Task List Screen
- Add/Edit Task
- Dark Mode UI

---

## 🎯 Key Learning Outcomes

- Firebase Authentication integration
- Real-time database (Firestore)
- RecyclerView implementation
- Android UI/UX design
- Cloud-based app architecture
- Debugging and lifecycle management

---

## 🚀 Future Enhancements

- Push notifications with reminders
- Task deadlines & scheduling
- User profile section
- Offline support
- Play Store deployment

---

## 👨‍💻 Author

Mohammed Nouman  
Computer Science Engineering Student

---

## ⭐ Conclusion

This project demonstrates a complete Android application lifecycle including authentication, real-time cloud data handling, and modern UI design — making it a production-level beginner-to-intermediate mobile application.


---
