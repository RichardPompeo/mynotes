# MyNotes - Full Stack Learning Project

A full-stack application built to learn Java, Spring Boot, Discord bot development, and React. This project demonstrates real-time communication between a Discord bot, a REST/WebSocket API, and a modern web interface.

## 📚 About This Project

This is a learning-focused open source project that combines multiple technologies:

- **Backend API**: Java with Spring Boot, JPA, and WebSocket support
- **Discord Bot**: Java Discord bot using JDA (Java Discord API)
- **Web Frontend**: React with Vite, TypeScript, and TailwindCSS
- **Monorepo**: Managed with Turborepo and pnpm

The application allows users to create and manage notes through both a web interface and Discord commands, with real-time synchronization via WebSockets.

## 🏗️ Project Structure

```
mynotes/
├── apps/
│   ├── backend/          # Spring Boot API with WebSocket support
│   ├── discord_bot/      # Discord bot built with JDA
│   └── web/              # React web application
└── packages/             # Shared packages and configurations
```

## 🛠️ Tech Stack

### Backend (Java)
- **Spring Boot 3.5.6** - REST API framework
- **Spring Data JPA** - Database ORM
- **Spring WebSocket** - Real-time communication
- **PostgreSQL** - Database
- **Maven** - Dependency management

### Discord Bot (Java)
- **JDA 5.0.0-beta.11** - Java Discord API
- **Apache HttpClient 5** - HTTP requests to backend
- **Jackson** - JSON processing
- **Maven** - Dependency management

### Web Frontend (React)
- **React 19** - UI framework
- **TypeScript** - Type safety
- **Vite** - Build tool and dev server
- **TailwindCSS 4** - Styling
- **Axios** - HTTP client
- **SockJS** - WebSocket client

### Monorepo Tools
- **Turborepo** - Build system and task runner
- **pnpm** - Package manager

## 🚀 Getting Started

### Prerequisites

- **Node.js** >= 18
- **pnpm** 9.0.0 or higher
- **Java** 17 or higher
- **Maven** 3.6+
- **PostgreSQL** (for backend database)

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd mynotes
   ```

2. **Install dependencies**
   ```bash
   pnpm install
   ```

3. **Set up PostgreSQL database**
   ```bash
   # Create a database for the application
   createdb mynotes
   ```

4. **Configure environment variables**

   For the backend (`apps/backend`):
   - Create `src/main/resources/application.yml` with your database credentials:
     ```yaml
     spring:
       datasource:
         url: jdbc:postgresql://localhost:5432/mynotes
         username: your_username
         password: your_password
       jpa:
         hibernate:
           ddl-auto: update
         show-sql: true
     ```

   For the Discord bot (`apps/discord_bot`):
   - Copy `src/main/resources/config.properties.example` to `config.properties`
   - Add your Discord bot token:
     ```properties
     DISCORD_BOT_TOKEN=your_discord_bot_token_here
     ```
   - Get your Discord bot token from the [Discord Developer Portal](https://discord.com/developers/applications)

### Running the Applications

#### Development Mode (All Apps)
```bash
# Run all applications in development mode
pnpm dev
```

#### Run Individual Apps

**Backend API:**
```bash
cd apps/backend
mvn spring-boot:run
```

**Discord Bot:**
```bash
cd apps/discord_bot
mvn compile exec:java
```

**Web Frontend:**
```bash
cd apps/web
pnpm dev
```

#### Build for Production
```bash
# Build all apps
pnpm build

# Or build specific app
pnpm build --filter=web
```

## 📖 Features

- ✅ RESTful API with Spring Boot
- ✅ Real-time updates via WebSocket
- ✅ Discord bot integration
- ✅ CRUD operations for notes
- ✅ Modern React UI with TailwindCSS
- ✅ Type-safe frontend with TypeScript
- ✅ Monorepo architecture

## 🧪 Available Scripts

```bash
pnpm dev          # Start all apps in development mode
pnpm build        # Build all apps
pnpm lint         # Lint all code
pnpm format       # Format code with Prettier
pnpm check-types  # Type check TypeScript code
```

## 📝 Learning Goals

This project was created to learn and practice:

- Java fundamentals and Spring Boot framework
- Building RESTful APIs with Spring
- WebSocket real-time communication
- JPA and database interactions
- Discord bot development with JDA
- React and modern frontend development
- Monorepo architecture and tooling
- Full-stack application integration

## 🤝 Contributing

This is a learning project, but contributions are welcome! Feel free to:

- Report bugs
- Suggest new features
- Submit pull requests
- Share your learning experiences

## 📄 License

This project is open source and available for learning purposes.

## 🔗 Useful Resources

### Spring Boot
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring WebSocket Guide](https://spring.io/guides/gs/messaging-stomp-websocket)

### Discord Bot Development
- [JDA Documentation](https://github.com/discord-jda/JDA)
- [Discord Developer Portal](https://discord.com/developers/docs)

### React & Frontend
- [React Documentation](https://react.dev)
- [Vite Guide](https://vitejs.dev/guide/)
- [TailwindCSS](https://tailwindcss.com)

### Monorepo
- [Turborepo Documentation](https://turbo.build/repo/docs)
- [pnpm Workspaces](https://pnpm.io/workspaces)

## 💡 Tips for Learning

1. Start by exploring each app independently
2. Understand how the Discord bot communicates with the backend API
3. Trace how data flows from the backend through WebSockets to the frontend
4. Experiment with adding new features to each component
5. Study the integration points between different technologies

---

**Happy Learning!** 🎓
