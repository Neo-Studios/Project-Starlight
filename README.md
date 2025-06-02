# Project Starlight – Java Game Project

Welcome to **Project Starlight**, a modern Java game starter kit using Maven! This template provides a clean foundation for building your own Java games, following best practices and a modular structure.

---

## 🚀 Features

- **Maven Build System**: Easy dependency management and builds.
- **Modern Java (17+)**: Leverage the latest Java features.
- **Ready-to-Extend Structure**: Organize your code for scalability.
- **Unit Testing**: JUnit included for test-driven development.
- **VS Code & Dev Container Support**: Instant setup for development.

---

## 📂 Project Structure

```
game/
├── pom.xml
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── example/
│   │               └── game/
│   │                   └── App.java
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── game/
│                       └── AppTest.java
└── target/
```

- **Main Logic:** `src/main/java/com/example/game/`
- **Tests:** `src/test/java/com/example/game/`

---

## 🛠️ Getting Started

### 1. **Clone the Repository**

```sh
git clone https://github.com/Neo-Studios/project-starlight.git
```
### 2. **Build the Project**

```sh
cd project-starlight
mvn clean package
```

### 2. **Run the Game**

```sh
mvn exec:java -Dexec.mainClass="com.neostudios.starlight.App"
```

### 3. **Run Tests**

```sh
mvn test
```


---

# Run the Java game with a virtual display (Xvfb)

If you are running in a dev container or headless environment, use Xvfb to provide a display for the game window:

```sh
sudo apt-get update && sudo apt-get install -y xvfb
xvfb-run mvn exec:java -Dexec.mainClass="com.neostudios.starlight.App"
```

You can also add this as a script or alias for convenience.



## 🤝 Contributing

Pull requests and suggestions are welcome! Please open an issue to discuss changes.

---

## 📄 License

MIT License. See the [LICENSE](LICENSE) file for details.

---
