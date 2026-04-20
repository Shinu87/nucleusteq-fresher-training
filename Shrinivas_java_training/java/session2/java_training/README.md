# 🚆 Smart Railway Ops System

## 📌 Overview

This is a Spring Boot backend application that simulates a railway system.

It includes:

- Passenger Management APIs
- Notification System
- Dynamic Message Formatter

---

## 🧠 Concepts Used

- IoC (Inversion of Control)
- Dependency Injection (Constructor-based)
- Component Scanning
- Layered Architecture (Controller → Service → Repository)
- Exception Handling

---

# 🚀 APIs

---

## 👤 Passenger Management

### 🔹 Get All Passengers

**GET /users**

![Get All Passengers](./screenshots/get-all-passengers.jpg)

---

### 🔹 Get Passenger by ID

**GET /users/{id}**

![Get Passenger By ID](./screenshots/get-passenger-by-id.jpg)

---

### 🔹 Passenger Not Found

![Passenger Not Found](screenshots/get-passenger-not-found.jpg)

---

### 🔹 Create Passenger

**POST /users**

![Create Passenger Request](screenshots/create-passenger-request.jpg)

---

### 🔹 Duplicate Passenger Error

![Duplicate Passenger](screenshots/duplicate-passenger-error.jpg)

---

## 🔔 Notification System

### 🔹 Booking Notification

**POST /notify?eventType=BOOKING**

![Booking Notification](screenshots/notification-booking.jpg)

---

### 🔹 Cancellation Notification

![Cancellation Notification](screenshots/notification-cancellation.jpg)

---

### 🔹 Default Notification

![Default Notification](screenshots/notification-default.jpg)

---

## 💬 Message Formatter

### 🔹 Short Message

**GET /message?type=SHORT**

![Short Message](screenshots/message-short.jpg)

---

### 🔹 Long Message

![Long Message](screenshots/message-long.jpg)

---

### 🔹 Invalid Type

![Invalid Message](screenshots/message-invalid.jpg)
