# PennyMatters: Digital Financial Recording Assistant
[![License](https://img.shields.io/github/license/BrownCai/PennyMatters?color=4D7A97&logo=apache)](https://opensource.org/licenses/Apache-2.0)
[![Detectie](https://img.shields.io/badge/Plagiarism_Detectie-ACTIEF-red)](#academic-integrity-warning)

[简体中文](README_zh.md) | [English](README.md)

**This application requires connection to the KU Leuven internal network, as all data is hosted on university servers.​​**

---

## 1. Core Functionality

PennyMatters revolutionizes expense tracking by simulating a conversational interface – imagine messaging your personal finance advisor. Target users include individuals seeking granular expenditure control.

**Key Features:**  
- 🗨️ **Chat-Style Recording**: Log expenses through natural dialogue (e.g.: "Coffee €3.50 at Starbucks")  
- 📸 **Receipt OCR**: Upload shopping receipts for automatic data extraction  
- 📊 **Smart Analytics**: Receive weekly/monthly spending reports, price trend alerts, and visualized expenditure charts  
- 🛒 **Price Comparison**: Search local stores for real-time price benchmarks before purchases 

---


## 2. Feature Walkthrough

### 2.1. Welcome & Authentication 

https://github.com/user-attachments/assets/74bcb2e0-0d79-4927-92e1-d148f729a391

- Auto-redirect to login after splash screen  
- Specially designed loading animation
- Enhanced security: Fingerprint login & encrypted input fields  
- *Note: Android system restrictions prevent recording during password entry*


### 2.2. Data Visualization

https://github.com/user-attachments/assets/49af9a06-41ee-4f25-be17-3cb93e93a7a4

- Powered by Anychart with interactive graphs  
- Real-time server data synchronization  
- Customizable timeframes (day/week/month)


### 2.3. Conversational Logging  

https://github.com/user-attachments/assets/9ff16966-88cc-4764-8ee1-403663e7e6a3

- Send expense entries like instant messages  
- Smart categorization


### 2.4. Price Intelligence

https://github.com/user-attachments/assets/b961ac0b-2663-4004-86ff-9a53e4e2a0f5

- Cross-store price comparisons  
- Filter by product category/description


### 2.5. Receipt Scanning  

https://github.com/user-attachments/assets/b2558dc6-779a-4499-8e1e-33b8cbe6a228

- **Long-press send button** to activate OCR  
- Editable auto-generated chat message entries  
- Cloud-synced data validation


### 2.6. Biometric Login

https://github.com/user-attachments/assets/e60cbec8-24dc-46ea-a375-403f3166888a

- Instant app access via fingerprint  
- Persistent session management

### 2.7. Data Management

https://github.com/user-attachments/assets/dafcd1a2-db20-492c-94fb-22a1ec7ab391

- Local chat history caching  
- Swipe-sidebar menu for data clearance  
- Automatic history purge upon logout
