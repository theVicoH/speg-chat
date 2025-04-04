# Speg Chat - Architecture Documentation

## Overview

Speg Chat is a modern chat application built with a Spring Boot backend and a WPF .NET frontend. The application follows a client-server architecture where the backend provides RESTful API endpoints that the frontend consumes.

## System Architecture

### High-Level Architecture

## Backend Architecture

The backend is built using Spring Boot and follows a layered architecture pattern:

### Layers

1. **Controller Layer**: Handles HTTP requests and responses
2. **Service Layer**: Contains business logic
3. **Repository Layer**: Manages data access
4. **Entity Layer**: Represents database tables
5. **DTO Layer**: Data Transfer Objects for API communication

### Key Components

#### Controllers
REST controllers that expose API endpoints for the frontend to consume.

#### Services
Business logic implementation that processes data and coordinates between controllers and repositories.

#### Repositories
JPA repositories that provide data access methods for entities.

#### Entities
JPA entities that map to database tables:
- User
- Room
- RoomType
- UserRoom
- Role
- Message

#### DTOs
Data Transfer Objects used for API communication:
- UserDto
- RoomDto
- RoomTypeDto
- UserRoomDto
- MessageDto

#### Mappers
Components that convert between entities and DTOs:
- UserMapper
- RoomMapper
- RoomTypeMapper
- UserRoomMapper
- MessageMapper

## Frontend Architecture

The frontend is built using WPF .NET and follows the MVVM (Model-View-ViewModel) pattern:

### Layers

1. **View Layer**: XAML UI components
2. **ViewModel Layer**: Connects the View with the Model
3. **Model Layer**: Represents data and business logic
4. **Service Layer**: Handles API communication with the backend

### Key Components

#### Views
XAML UI components that display the user interface.

#### ViewModels
Classes that handle UI logic and state management.

#### Models
Classes that represent data structures.

#### Services
Classes that handle API communication with the backend.

## Database Schema

## API Endpoints

The backend exposes the following RESTful API endpoints:

### Authentication
- `POST /api/auth/login`: User login
- `POST /api/auth/register`: User registration

### Users
- `GET /api/users`: Get all users
- `GET /api/users/{id}`: Get user by ID
- `PUT /api/users/{id}`: Update user
- `DELETE /api/users/{id}`: Delete user

### Rooms
- `GET /api/rooms`: Get all rooms
- `GET /api/rooms/{id}`: Get room by ID
- `POST /api/rooms`: Create new room
- `PUT /api/rooms/{id}`: Update room
- `DELETE /api/rooms/{id}`: Delete room

### Messages
- `GET /api/rooms/{roomId}/messages`: Get messages for a room
- `POST /api/rooms/{roomId}/messages`: Send message to a room

### Room Types
- `GET /api/room-types`: Get all room types
- `GET /api/room-types/{id}`: Get room type by ID

### User Rooms
- `GET /api/user-rooms`: Get all user rooms
- `POST /api/user-rooms`: Add user to room
- `DELETE /api/user-rooms/{userId}/{roomId}`: Remove user from room

## Security

The application implements security using Spring Security with JWT (JSON Web Tokens) for authentication and authorization.

## Technologies Used

### Backend
- Java 21
- Spring Boot
- Spring Security
- JPA/Hibernate
- Maven
- Swagger/OpenAPI for API documentation

### Frontend
- C#
- WPF .NET
- MVVM pattern

### Database
- SQL Database (configurable)

## Deployment

The application can be deployed using Docker containers:

This will start both the backend and frontend services along with the database.