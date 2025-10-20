# My Notes - Web Application Documentation

## Overview

This documentation provides an overview of the web application for managing notes. The application is built with React and TypeScript on the frontend, communicating with a Java Spring Boot backend. The application features a modern, dark-themed UI with responsive design and full CRUD functionality for managing personal notes.

## Architecture

The application follows a client-server architecture:

- **Frontend**: React/TypeScript web application
- **Backend**: Java Spring Boot REST API
- **Authentication**: Discord OAuth 2.0 integration

## Features

- User authentication with Discord
- Create, read, update, and delete notes
- Responsive design optimized for desktop and mobile
- Dark mode UI with minimalist design
- Set visibility (private/public) for notes
- Set reminders for notes

## Technical Stack

### Frontend
- React 19.x
- TypeScript
- React Router
- Tailwind CSS 
- Axios for API communication
- Lucide-React for icons

### Backend
- Java 17+
- Spring Boot 3.5.x
- Spring Data JPA
- PostgreSQL Database
- Spring Security with OAuth2

## Application Structure

### Components

- **Layout**: Provides consistent page structure with header and footer
- **NoteCard**: Displays individual note with actions
- **NoteForm**: Form for creating and editing notes
- **Spinner**: Loading indicator
- **ProtectedRoute**: Route wrapper for authenticated users
- **PublicRoute**: Route wrapper for unauthenticated users

### Pages

- **Login**: Discord authentication entry point
- **AuthCallback**: Handles OAuth callback from Discord
- **Dashboard**: Main page with note listing and management
- **NoteDetail**: Detailed view of a single note

### Context

- **AuthContext**: Manages authentication state and user information

### Services

- **API Service**: Handles communication with the backend API

## Authentication Flow

1. User clicks "Sign in with Discord" on the Login page
2. User is redirected to Discord for authentication
3. After successful authentication, Discord redirects to the AuthCallback page
4. The application exchanges the authorization code for tokens
5. User information is stored in AuthContext and localStorage
6. The user is redirected to the Dashboard

## Data Models

### Note
- `id`: Unique identifier
- `title`: Note title
- `content`: Note content
- `createdAt`: Creation timestamp
- `updatedAt`: Last update timestamp
- `serverId`: Associated Discord server ID
- `channelId`: Associated Discord channel ID
- `discordUserId`: Discord user ID of the owner
- `visibility`: Note visibility (private/public)
- `alertAt`: Optional reminder timestamp

### User
- `id`: Discord user ID
- `username`: Discord username
- `discriminator`: Discord discriminator
- `avatar`: Discord avatar URL
- `global_name`: Discord global name

## Routes

- `/login`: Authentication page
- `/auth/callback`: OAuth callback handler
- `/dashboard`: Main notes dashboard
- `/notes/:id`: Individual note detail page

## API Integration

The frontend communicates with the backend using RESTful endpoints:

- GET `/notes/{discordUserId}`: Retrieve all notes for a user
- GET `/notes/{id}`: Retrieve a specific note by ID
- POST `/notes`: Create a new note
- PUT `/notes/{id}`: Update an existing note
- DELETE `/notes/{id}`: Delete a note

## Design System

- Dark theme with blue accents
- Responsive layout using Tailwind CSS
- Lucide React icons for UI elements
- Card-based UI for notes display
- Form components with consistent styling
- Loading indicators for async operations

## User Experience Improvements

- Interactive note cards with hover states
- Confirmation dialogs for destructive actions
- Visual feedback for loading states
- Responsive design adapts to different screen sizes
- Intuitive navigation between pages
- Form validation for note creation and editing

## Performance Considerations

- Efficient API service with request interceptors
- Optimized component rendering with proper state management
- Token-based authentication with local storage persistence
- Responsive image loading for Discord avatars

## Security Features

- Protected routes require authentication
- Token-based authorization for API requests
- Discord OAuth for secure authentication
- Input sanitization for form submissions

## Future Improvements

- Add note search functionality
- Implement note categories/tags
- Add note sharing capabilities
- Enhance the editor with rich text functionality
- Add notification system for reminders
- Implement note filtering options