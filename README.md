# Project Starlight – Java Game Project 🚀

[![Build Status](https://github.com/Neo-Studios/project-starlight/actions/workflows/maven.yml/badge.svg)](https://github.com/Neo-Studios/project-starlight/actions)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

<p align="center">
  <img src="https://raw.githubusercontent.com/Neo-Studios/project-starlight/main/assets/logo.png" alt="Project Starlight Logo" width="200"/>
</p>

# Welcome to Project Starlight! 🌟

A modern, open-source Java game starter kit designed for creativity, learning, and fun. Whether you're a beginner or a seasoned developer, Project Starlight gives you the tools to build, test, and share your own games with ease.

---

## ✨ Features

- ⚡ **Maven Build System**: Easy dependency management and builds.
- ☕ **Modern Java (17+)**: Leverage the latest Java features.
- 🧩 **Ready-to-Extend Structure**: Organize your code for scalability.
- 🧪 **Unit Testing**: JUnit included for test-driven development.
- 🖥️ **VS Code & Dev Container Support**: Instant setup for development.
- 🎮 **Game-Ready Boilerplate**: Start coding your game logic right away!

---

## 📂 Project Structure

```text
game/
├── pom.xml
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── neostudios/
│   │               └── starlight/
│   │                   ├── App.java
│   │                   ├── GameState.java
│   │                   └── Player.java
│   └── test/
│       └── java/
│           └── com/
│               └── neostudios/
│                   └── starlight/
│                       └── AppTest.java
└── target/
```

- **Main Logic:** [`src/main/java/com/neostudios/starlight/`](game/src/main/java/com/neostudios/starlight/)
- **Tests:** [`src/test/java/com/neostudios/starlight/`](game/src/test/java/com/neostudios/starlight/)

---

## ⚡ Quick Start

1. **Clone & Enter the Repo:**
   ```sh
   git clone https://github.com/Neo-Studios/project-starlight.git
   cd project-starlight
   ```
2. **Build & Run:**
   ```sh
   mvn clean package
   mvn exec:java -Dexec.mainClass="com.neostudios.starlight.App"
   ```
3. **See your game in action!**

---

## 💡 Why Project Starlight?

- Beginner-friendly, but powerful enough for advanced users
- Modular, clean codebase for easy expansion
- Active, welcoming community
- Open to contributions and new ideas

---

## 🛠️ Getting Started

### 1. Clone the Repository

```sh
git clone https://github.com/Neo-Studios/project-starlight.git
cd project-starlight
```

### 2. Build the Project

```sh
mvn clean package
```

### 3. Run the Game

```sh
mvn exec:java -Dexec.mainClass="com.neostudios.starlight.App"
```

### 4. Run Tests

```sh
mvn test
```

---

## 🖥️ Running in a Dev Container or Headless Environment

If you are running in a dev container or headless environment, use Xvfb to provide a display for the game window:

```sh
sudo apt-get update && sudo apt-get install -y xvfb
xvfb-run mvn exec:java -Dexec.mainClass="com.neostudios.starlight.App"
```

You can also add this as a script or alias for convenience.

---

## 🌐 Community & Help

- 💬 [Discussions](https://github.com/Neo-Studios/project-starlight/discussions)
- 🐞 [Report a Bug](.github/ISSUE_TEMPLATE/bug_report.yml)
- 🚀 [Request a Feature](.github/ISSUE_TEMPLATE/feature_request.yml)
- 📚 [Guide to Java](guide-to-java.md)

---

## 🤝 Contributing & Community

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
