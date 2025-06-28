# 🔐 Cypher-Com-App

A secure communication desktop application developed in Java using SQLite, with encryption via Caesar and Vigenère ciphers. Created as a coursework project for the Information Security Systems course.

## 📚 Project Overview

This app allows users to:
- Register and log in with hashed passwords
- Encrypt messages using Caesar or Vigenère cipher
- Store encrypted messages in a local SQLite database
- View and decrypt messages with the correct keys
- Interact through a simple JavaFX GUI

## 🛠 Technologies Used

- Java (JDK 8+)
- JavaFX (UI)
- SQLite (local DB)
- SHA-256 for password hashing
- Caesar Cipher & Vigenère Cipher (encryption)
- NetBeans IDE

## 📦 Features

- **User Authentication**: Secure login and registration system with SHA-256 hashed passwords.
- **Message Encryption**: 
  - *Caesar Cipher*: Character shift based on a numeric key.
  - *Vigenère Cipher*: Text-based key encryption.
- **Message Storage**: Encrypted messages are stored in an SQLite database.
- **GUI Interface**: Built with JavaFX, includes Register/Login, Encrypt, Decrypt, and View features.

## 💾 Database Schema

### `users` table
| Column     | Type    | Description                  |
|------------|---------|------------------------------|
| id         | INTEGER | Primary key, auto-increment  |
| username   | TEXT    | Unique username              |
| password   | TEXT    | SHA-256 hashed password      |

### `messages` table
| Column            | Type    | Description                       |
|-------------------|---------|-----------------------------------|
| id                | INTEGER | Primary key, auto-increment       |
| user_id           | INTEGER | Foreign key to users              |
| cipher            | TEXT    | Algorithm used (Caesar/Vigenère)  |
| encrypted_message | TEXT    | The actual encrypted message      |

## 🚀 How to Run

1. Clone the repo:
   ```bash
   git clone https://github.com/valentob/Cypher-Com-App.git
   
2. Open the project in NetBeans or another Java IDE.

3. Add the SQLite JDBC `.jar` file to your project libraries.

4. Build and run the project.

## 👤 Author

Valento Bardhoshi  
www.linkedin.com/in/valentobardhoshi
