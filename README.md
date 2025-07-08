# BookFlow - Library Management System 📚🔄

![Java](https://img.shields.io/badge/Java-21-orange?logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-green?logo=spring&logoColor=white)
![React](https://img.shields.io/badge/React-19.0.0-blue?logo=react&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-5.7.2-blue?logo=typescript&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Containerized-blue?logo=docker&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green)

## 📖 Project Description

BookFlow is a modern, full-stack library management system designed to streamline library operations and enhance user experience. The system provides comprehensive functionality for managing books, users, and borrowing processes in both traditional and digital library environments.

### Key Features:
- **User Management**: Complete user registration, authentication, and profile management
- **Book Catalog**: Advanced book management with search, filtering, and categorization
- **Borrowing System**: Automated borrowing and return processes with due date tracking
- **Admin Dashboard**: Comprehensive administrative tools for library management
- **Real-time Notifications**: Email notifications for due dates, reservations, and system updates
- **Responsive Design**: Optimized for desktop, tablet, and mobile devices
- **Security**: JWT-based authentication with Spring Security integration

The system aims to digitize and modernize library operations while maintaining user-friendly interfaces for both library staff and patrons.

---

## 🏗️ System Architecture

### Backend (Spring Boot)
- **Framework**: Spring Boot 3.4.4
- **Language**: Java 21
- **Database**: MySQL 8.0 with Docker containerization
- **Build Tool**: Maven
- **Security**: Spring Security + JWT Authentication
- **ORM**: Spring Data JPA
- **Email Service**: Spring Mail integration

### Frontend (React + TypeScript)
- **Framework**: React 19.0.0
- **Language**: TypeScript 5.7.2
- **Build Tool**: Vite
- **UI Library**: Material-UI (MUI)
- **State Management**: React Context API
- **HTTP Client**: Axios
- **Routing**: React Router DOM

---

## 🚀 Installation and Setup

### Prerequisites
- **Java 21** or higher
- **Node.js 18+** and npm
- **Docker** and Docker Compose
- **MySQL 8.0** (if not using Docker)

### 1. Clone the Repository
```bash
git clone https://github.com/elzyr/bookFlow.git
cd bookFlow
```

### 2. Backend Setup
```bash
# Navigate to backend directory
cd backend

# Configure database in application.properties
cp src/main/resources/application.properties.example src/main/resources/application.properties

# Start MySQL using Docker
docker-compose up -d mysql

# Run the Spring Boot application
mvn spring-boot:run
```

### 3. Frontend Setup
```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start the development server
npm run dev
```

---

## 🛠️ Technologies Used

### Backend Technologies
- **Spring Boot 3.4.4** – REST API framework
- **Spring Security** – authentication and authorization
- **Spring Data JPA** – data persistence layer
- **MySQL 8.0** – relational database
- **Docker** – containerization
- **Maven** – dependency management
- **JWT** – token-based authentication
- **Spring Mail** – email notifications

### Frontend Technologies
- **React 19.0.0** – user interface library
- **TypeScript 5.7.2** – static type checking
- **Material-UI** – React component library
- **Vite** – build tool and dev server
- **Axios** – HTTP client
- **React Router DOM** – client-side routing
- **React Hook Form** – form management
