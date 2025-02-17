# 🏋️‍♂️ Fitness App - Android Frontend

**Fitness App** is a social media-based fitness tracking application built with **Android (Java)**. Users can **log activities, follow/unfollow others, search for users, view activity summaries, manage friends, and interact with posts**.

---

## 🚀 Features
### ✅ **User Authentication**
- **Login & Registration with JWT authentication**
- Uses `SharedPreferences` to store tokens
- Auto-login feature

### ✅ **Home Feed**
- Displays **social media-style feed** with fitness activities
- Users can **like, comment, and view activity details**
- **Pagination & real-time updates**

### ✅ **User Profile**
- Displays **full name, username, and activity summary**
- Users can **follow/unfollow others**
- **Dynamically updates follow button** status

### ✅ **Activity Summary & Friends List**
- View **logged activities** grouped by type
- **Friends list with full name & username**
- Switch between lists dynamically

### ✅ **Search Users**
- **Real-time search** for users by name or username
- Results update dynamically while typing
- Click on a result to **view profile & follow/unfollow**

### ✅ **Follow/Unfollow System**
- **Follow/unfollow users dynamically**
- API calls to `/follow/` & `/unfollow/` endpoints
- Updates follow status in real-time

### ✅ **Networking & API Integration**
- Uses **Volley** for making API requests
- **Authorization headers** are added for secure API calls
- **Handles API errors and caching** for better performance

---

## 🛠 **Tech Stack**
- **Language**: Java (Android)
- **Networking**: Volley Library
- **UI Components**: XML Layouts, Material UI
- **Data Storage**: SharedPreferences
- **Architecture**: MVVM-like pattern

---

## 🔧 **Installation & Setup**
### **1️⃣ Clone the Repository**
```sh
git clone https://github.com/yourusername/FitnessApp.git
cd FitnessApp
