# Speg Chat

A modern chat application with a Spring Boot backend and WPF .NET frontend.

## Project Overview

Speg Chat is a full-stack chat application that allows users to create and join different types of chat rooms, send messages, and manage their profiles. The application supports both public and private conversations.

## Architecture

The project is structured as follows:

- **Backend**: Java Spring Boot REST API
- **Frontend**: WPF .NET desktop application

## Features

- User authentication and authorization
- Public and private chat rooms
- Real-time messaging
- User profile management
- Room creation and management

## Backend

The backend is built with Spring Boot and provides a RESTful API for the frontend to consume.

### Technologies Used

- Java 21
- Spring Boot
- Spring Security
- JPA/Hibernate
- Maven

### API Endpoints

The backend provides various endpoints for:

- User authentication
- Room management
- Message handling
- User profile management

### Database Structure

The application uses a relational database with the following main entities:

- Users
- Rooms
- Messages
- Room Types
- User-Room relationships (with roles)

## Frontend

The frontend is a WPF .NET desktop application that provides a user-friendly interface for interacting with the chat system.

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven
- .NET Framework (for the WPF client)
- Docker and Docker Compose
- MariaDB

### Environment Setup

1. Clone the repository:
```bash
git clone https://github.com/yourusername/speg-chat.git
cd speg-chat
```
2. Copy the .env.example file to .env and configure your environment variables.
```
cp .env.example .env
```
3. Start the docker containers:
```bash
docker-compose up --build
```
4. Run the application:
- `http://localhost:8080/` : The backend API
- `http://localhost:8080/docs` : Swagger UI for the backend API
- `http://localhost:1333/` : Adminer

### Credits
- [Hassan Aidibe](https://github.com/HascoVice)
- [Victor Huang](https://github.com/theVicoH)
- [Jugurta Mahious](https://github.com/jugurtamahious)
- [Rayane Mabrouki](https://github.com/Hikyy)
- [Vitomir Laces](https://github.com/vitolinho)