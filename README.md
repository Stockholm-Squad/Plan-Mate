# PlanMate v1.0 & v2.0

**PlanMate** is a CLI-based task management application built with **Kotlin**, applying **SOLID principles**, **TDD**, and **clean architectural practices**. The application supports multi-user task handling with a clear separation of responsibilities between *admin* and *mate* user roles.

---

## 🚀 Features

### ✅ Functional Requirements

- **User Roles**: Supports two roles:
  - `Admin`: Can create/edit/delete projects, states, and users.
  - `Mate`: Can create/edit/delete tasks within projects.
- **Authentication**:
  - Username & password (passwords are hashed with **MD5**, not stored in plain text).
- **Projects & Tasks**:
  - Multiple projects are supported.
  - Each project has a **state** (e.g., TODO, In Progress, Done) editable by admin.
  - Each Task has a set of **dynamic states** (e.g., TODO, In Progress, Done) editable by admin and Mate.
  - Tasks are managed per project.
  - User can view all projects/project details
  - Admin can add/delete/update project.
  - User can show all tasks/show task by name/add task/delete task/update task/show tasks by project name.
- **States**:
  - Admin can add/update/delete **States** to be used later.
  - User can view all states available.
- **Swimlane View**:
  - Console-based swimlane UI to visualize Menues and tasks by state.
- **Audit System**:
  - View history of changes to tasks/projects and states with timestamp and author.
- **User Management**:
  - Admins can create users of type `mate`.
  - Admins can assign user to a `project`
  - Admins can assign user to a `task`

---

## 🧠 Technical Overview

### ⚙ Architecture

- **Layered Design**:
  - `ui/`: CLI user interface.
  - `logic/`: Business logic (domain layer, use cases, entities).
  - `data/`: Persistence layer using CSV files or MongoDB (v2.0).
- **Unidirectional Flow**:
  - UI → Logic ← Data  
    *(Logic is independent of UI and Data)*
- **Repositories**:
  - Example: `AuthenticationRepository`, `ProjectsRepository`, etc.

### 💾 Data Management

- **v1.0**: Data is persisted in **CSV files** using **Kotlin DataFrame**.
- **v2.0**: Migrated to **MongoDB** using **coroutines** for asynchronous operations.

---

## 🧪 Testing & Quality

- 100% test coverage using:
  - `JUnit5`, `MockK`, `Truth`
- Development follows **Test-Driven Development (TDD)**.
- **Peer Reviews** are required for code quality and learning.

---

## 💡 Key Concepts & Tools

- **Kotlin**
- **Coroutines** (for MongoDB async operations)
- **MongoDB** (PlanMate v2.0)
- **DataFrame** (CSV file read/write)
- **OOP & SOLID**
- **TDD (Test-Driven Development)**
- **Koin** (for Dependency Injection)
- **Command Line Interface (CLI)**

---

## 🛠️ Dependency Inversion

- **Storage logic is abstracted** via interfaces.
- Changing the data source from **CSV → MongoDB** does **not** affect domain or UI layers.
- Any logic breakage during migration indicates an improvement opportunity for better abstraction.

---

## 📦 Installation

1. Clone the repo:
   ```bash
   git clone https://github.com/your-org/planmate.git
   cd planmate
2. Build the project:
   ```bash
    ./gradlew build
3. Run CLI APP:
    ```bash
   ./gradlew run
