# 🚀 Todo Application - Spring Boot

A simple REST API built using Spring Boot for Todo management.  
This project demonstrates CRUD operations, layered architecture, logging, and unit testing using Mockito.

---

# 📌 Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- Maven
- H2 Database (In-memory)
- JUnit 5
- Mockito

---

# 📌 Features

- Create Todo
- Retrieve all Todos
- Retrieve Todo by ID
- Update Todo
- Delete Todo
- Status tracking (PENDING / COMPLETED)
- Service layer logging
- Unit testing with Mockito (Repository mocked)

---

# 📌 API Endpoints

## ➕ Create Todo

**POST** `/todos`

---

## 📋 Get All Todos

**GET** `/todos`

---

## 🔍 Get Todo by ID

**GET** `/todos/{id}`

---

## ✏️ Update Todo

**PUT** `/todos/{id}`

---

## ❌ Delete Todo

**DELETE** `/todos/{id}`

---

# 📸 API Execution Screenshots

## 1️⃣ Create Todo (Request)

![Create Todo Request](screenshots/01_create_todo_request.jpg)

## 2️⃣ Create Todo (Logs)

![Create Todo Logs](screenshots/01_create_todo_full_flow.jpg)

---

## 3️⃣ Get All Todos (Response)

![Get All Todos](screenshots/get_all_todos.jpg)

## 4️⃣ Get All Todos (Logs)

![Get All Todos Logs](screenshots/02_get_all_todos_flow.jpg)

---

## 5️⃣ Get Todo by ID (Response)

![Get Todo By ID](screenshots/get_todo_by_id.jpg)

## 6️⃣ Get Todo by ID (Logs)

![Get Todo By ID Logs](screenshots/03_get_by_id_flow.jpg)

---

## 7️⃣ Update Todo (Before Update)

![Before Update](screenshots/get-todo-id-1-before-update.jpg)

## 8️⃣ Update Todo (Logs)

![Update Todo Logs](screenshots/04_update_todo_flow.jpg)

## 9️⃣ Update Todo (After Update)

![After Update](screenshots/update-todo-id-1-after-update.jpg)

---

## 🔟 Delete Todo (Response)

![Delete Todo](screenshots/delete_todo.jpg)

## 11️⃣ Delete Todo (Logs)

![Delete Todo Logs](screenshots/05_delete_todo_flow.jpg)

---

## 12️⃣ Error Case (Todo Not Found)

![Todo Not Found](screenshots/todo_not_found_error.jpg)

---

## 13️⃣ Unit Test Execution Logs

![Unit Test Execution Logs](screenshots/06_unit_test_execution.jpg)

---

# 🧪 Running the Project

## ▶️ Run Application

```bash
mvn spring-boot:run
```
