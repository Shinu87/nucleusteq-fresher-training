# 🚀 Todo Application - Spring Boot

A simple REST API built using Spring Boot for Todo management.

---

# 📌 Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- Maven
- H2 Database

---

# 📌 API Endpoints

## Create Todo

**POST** `/todos`

---

## Get All Todos

**GET** `/todos`

---

## Get Todo by ID

**GET** `/todos/{id}`

---

## Update Todo

**PUT** `/todos/{id}`

---

## Delete Todo

**DELETE** `/todos/{id}`

---

# 📸 API Screenshots

## 1️⃣ Create Todo

![Create Todo](screenshots/01_create_todo_request.jpg)

---

## 2️⃣ Get All Todos

![Get All Todos](screenshots/get_all_todos.jpg)

---

## 3️⃣ Get Todo by ID

![Get Todo By ID](screenshots/get_todo_by_id.jpg)

---

## 4️⃣ Before Update

![Before Update](screenshots/get-todo-id-1-before-update.jpg)

---

## 6️⃣ After Update

![After Update](screenshots/update-todo-id-1-after-update.jpg)

---

## 7️⃣ Delete Todo

![Delete Todo](screenshots/delete_todo.jpg)

---

## 8️⃣ Error Case

![Todo Not Found](screenshots/todo_not_found_error.jpg)

---

# 🚀 Run Project

```bash
mvn spring-boot:run
```
