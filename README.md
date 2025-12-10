# 📘 Study Helper — Android Flashcard App

Study Helper is an Android app designed to help organize study subjects and review questions in a clean, flashcard-style interface. Each subject contains a set of questions, and each question includes both a prompt and an answer that can be revealed when needed.

The app supports **local storage through Room**, **remote importing of subjects**, and **customizable settings** such as dark mode and subject sorting.

---

## ✨ Features

- 🏠 **Main Screen** displaying all subjects as colored cards  
- ❓ **Question Viewer** with flashcard-style “Show Answer / Hide Answer” interaction  
- 🔀 **Navigation Arrows** to move between questions in a subject  
- ☁️ **Import System** to load new subjects from a remote source  
- ⚙️ **Settings Page** with dark theme toggle, subject sorting, and default question preferences  
- ➕ **Add Subject Dialog** for creating new subjects  
- 🌙 **Dark Mode Support** for a comfortable study experience  

---

## 📱 Screenshots Overview

Here is the flow of the app illustrated with screenshots:

1. **Main Screen** — shows existing subjects  
2. **Subject Question Page** — displays the first question  
3. **Show Answer** — reveals the answer beneath the question  
4. **Next Question** — navigate using the right arrow  
5. **Import Screen** — select subjects using checkboxes and tap *Import*  
6. **Main Screen After Import** — newly added subjects appear instantly  
7. **Settings Page** — toggle dark mode and adjust preferences  
8. **Dark Mode Enabled** — entire UI adapts to dark theme  
9. **Add Subject Dialog** — create a new subject using the “+” button  

---

## 🧩 Architecture

The app follows the **MVVM architecture** and uses several Jetpack components:

- 🗂 **Model Layer**: `Subject`, `Question`  
- 🧪 **Repository Layer**: handles Room database operations and remote fetch  
- 🧠 **ViewModels**: provide lifecycle-aware data for UI screens  
- 🖥 **UI Layer**: Activities + DialogFragment for user interaction  
- 🛢 **Room Database**: stores all subjects and questions locally  
- 🌐 **Volley**: fetches remote subjects and questions for importing  

---

## 🛠 Technologies Used

- Kotlin  
- Room Persistence Library  
- LiveData + ViewModel (MVVM pattern)  
- RecyclerView & ListView for lists  
- Volley (network requests)  
- Material Design components  
- Dark mode support  

---

## 🎯 Purpose

Study Helper provides students with a simple and organized way to study:
- Browse subjects  
- Review questions one at a time  
- Reveal answers only when ready  
- Add or import new study material  
- Customize the study environment  

---
