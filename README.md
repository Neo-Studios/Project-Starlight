# Project Starlight – Java Game Project 🚀

[![Build Status](https://github.com/Neo-Studios/project-starlight/actions/workflows/maven.yml/badge.svg)](https://github.com/Neo-Studios/project-starlight/actions)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

<p align="center">
  <img src="https://raw.githubusercontent.com/Neo-Studios/project-starlight/main/assets/logo.png" alt="Project Starlight Logo" width="200"/>
</p>

>[!NOTE] 
>This project uses a custom game engine, NeoLight, built by us.

# Welcome to Project Starlight! 🌟

Project Starlight is a modern, modular Java game starter kit and engine, built for learning, creativity, and rapid prototyping. It features the custom **NeoLight** engine, asset management, audio support, and a clean, extensible codebase. Whether you're a beginner or an experienced developer, this repo gives you the tools to build, test, and share your own Java games with ease.

---

## ✨ Key Features

- ⚡ **NeoLight Game Engine**: Custom, extensible engine with lifecycle hooks, asset/audio management, and game state handling.
- 🗂️ **Asset Management**: Centralized image and audio loading/caching for easy resource use.
- 🔊 **Audio Support**: Play sound effects and music with simple API calls.
- ☕ **Modern Java (17+)**: Uses the latest Java features and best practices.
- 🧩 **Modular Structure**: Clean separation of engine, game logic, and assets for easy expansion.
- 🧪 **JUnit 5 Testing**: Modern unit testing out of the box.
- 🖥️ **VS Code & Dev Container Ready**: Instant setup for development.
- 🚀 **Ready-to-Extend**: Add new features, scenes, or systems with minimal friction.

---

## 📂 Project Structure

```text
game/
├── pom.xml
├── assets/                # Game configuration and static assets
│   └── game.properties
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── neostudios/
│   │               └── starlight/
│   │                   ├── App.java
│   │                   ├── NeoLightEngine.java
│   │                   ├── NeoLightGame.java
│   │                   ├── Renderer.java
│   │                   ├── GameState.java
│   │                   ├── GameStateManager.java
│   │                   ├── ConfigManager.java
│   │                   ├── InputManager.java
│   │                   ├── Player.java
│   │                   └── assets/
│   │                       └── AssetManager.java
│   └── test/
│       └── java/
│           └── com/
│               └── neostudios/
│                   └── starlight/
│                       └── AppTest.java
└── target/
```

- **Engine & Game Logic:** [`src/main/java/com/neostudios/starlight/`](game/src/main/java/com/neostudios/starlight/)
- **Assets & Config:** [`game/assets/`](game/assets/)
- **Tests:** [`src/test/java/com/neostudios/starlight/`](game/src/test/java/com/neostudios/starlight/)

---

## 🚀 Quick Start

1. **Clone the Repo:**
   ```sh
   git clone https://github.com/Neo-Studios/project-starlight.git
   cd project-starlight
   ```
2. **Build the Project:**
   ```sh
   mvn clean package
   ```
3. **Run the Game (NeoLight Engine):**
   ```sh
   mvn exec:java -Dexec.mainClass="com.neostudios.starlight.NeoLightEngine"
   ```
4. **Run Tests:**
   ```sh
   mvn test
   ```

---

## 🛠️ What Makes This Repo Special?

- **NeoLight Engine:** A simple, extensible Java game engine with a real game loop, asset/audio management, and lifecycle events.
- **Asset Management:** Load images and audio from resources or JARs with a single call.
- **Audio:** Play sound effects and music easily.
- **Modern Java:** Uses Java 17+, Maven, and VS Code dev containers.
- **Clean, Modular Code:** Easy to read, extend, and maintain.
- **Beginner Friendly:** Great for learning Java game dev, but powerful enough for real projects.

---

## 🖥️ Dev Container & Headless Support

If running in a dev container or headless environment, use Xvfb to provide a display for the game window:

```sh
sudo apt-get update && sudo apt-get install -y xvfb
xvfb-run mvn exec:java -Dexec.mainClass="com.neostudios.starlight.NeoLightEngine"
```

---

## 🌐 Community & Help

- 💬 [Discussions](https://github.com/Neo-Studios/project-starlight/discussions)
- 🐞 [Report a Bug](.github/ISSUE_TEMPLATE/bug_report.yml)
- 🚀 [Request a Feature](.github/ISSUE_TEMPLATE/feature_request.yml)
- 📚 [Guide to Java](guide-to-java.md)

---

## 🤝 Contributing

Pull requests and suggestions are welcome! Please see our [Contributing Guide](CONTRIBUTING.md) and [Code of Conduct](CODE_OF_CONDUCT.md).

- [How to Contribute](CONTRIBUTING.md)
- [Code of Conduct](CODE_OF_CONDUCT.md)
- [Security Policy](SECURITY.md)
- [Support](SUPPORT.md)

---

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

> Made with ❤️ by [Neo Studios](https://github.com/Neo-Studios) and contributors. Join us and help shape the future of open-source Java games!
