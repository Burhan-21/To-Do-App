# ToDoApp
Advanced Java 17+ Swing To‑Do List app with add/remove/schedule, reminders, search/filter, double‑click completion, and persistent storage. Pop‑up notifications for scheduled tasks. Modern UI and keyboard shortcuts.
The project lets you manage tasks with modern features like scheduled reminders, real-time search, double‑click completion, and persistent storage — all built with clean Java 17+ code.

---

## Features
[](https://github.com/YourUsername/AdvancedToDoApp#features)

- ➕ **Add tasks** instantly via button or Enter key  
- 🗑 **Remove tasks** using button or Delete key  
- ⏰ **Schedule tasks** with specific reminder times (HH:mm) and get pop‑up notifications when due  
- ✔ **Mark tasks complete** by double‑clicking (strike‑through style)  
- 🔍 **Real‑time search/filter** via top search bar  
- 💾 **Persistent storage** in `tasks.txt` — tasks auto‑load on startup  
- 🎨 Modern Swing UI with emojis, fonts, and spacing  
- ⌨ Keyboard shortcuts for faster workflow  

---

## Getting Started
[](https://github.com/YourUsername/AdvancedToDoApp#getting-started)

### Prerequisites
[](https://github.com/YourUsername/AdvancedToDoApp#prerequisites)
- Java 17 or later installed (tested with Java 17, Java 18+)
- Code editor/IDE such as IntelliJ IDEA CE, Eclipse, or VS Code
- Terminal or Command Prompt access

---

## How to Compile & Run
[](https://github.com/YourUsername/AdvancedToDoApp#how-to-compile--run)

1. Clone this repository or download `ToDoApp.java` and place it in a folder.  
2. Open a Terminal/Command Prompt in that folder.
3. Compile:
4. Run:


💡 **Tip:** Ensure your terminal’s current directory matches the folder containing `ToDoApp.java` before compiling/running.

---

## Usage
[](https://github.com/YourUsername/AdvancedToDoApp#usage)

When the app starts:
- **Add a task:** Type in the bottom field → click `➕ Add` or press **Enter**
- **Remove a task:** Select it → click `🗑 Remove` or press **Delete**
- **Schedule a task:** Click `⏰ Schedule` → enter task name → set time (HH:mm)
- **Mark complete:** Double‑click a task to toggle ✔ status
- **Search/filter:** Use the 🔍 search bar to filter results live
- **Notifications:** At scheduled times, a pop‑up reminder appears

---

## Code Structure
[](https://github.com/YourUsername/AdvancedToDoApp#code-structure)

- **`ToDoApp`** — Main JFrame app class, handles UI and events
- **`Task` (inner class)** — Represents a task with text, optional time, and completion status
- **Persistence:** Saved in `tasks.txt` in the project root
- **Reminders:** Timer checks every 30 secs for scheduled tasks and pops notifications

---

## Modern Java Concepts Used
[](https://github.com/YourUsername/AdvancedToDoApp#modern-java-concepts-used)

- **Java 17 var** — Local variable type inference
- **Streams** — Filtering tasks for search
- **Lambda expressions** — Event handling
- **Inner class model** — Task representation  
- **Enhanced switch** _(optional improvements)_ — Could be used in event logic

---

## What I Learned
[](https://github.com/YourUsername/AdvancedToDoApp#what-i-learned)

- Building feature‑rich Swing apps with event‑driven programming
- Implementing time‑based reminders in desktop apps
- Persisting state between app runs  
- Structuring GUI with layout managers for a clean look
- Applying modern Java 17 syntax for conciseness and clarity

---

## Example Session
[](https://github.com/YourUsername/AdvancedToDoApp#example-session)

1. Add "Go to GYM" scheduled at `18:00`  
2. Keep the app running until the scheduled time  
3. At 18:00 → Pop‑up reminder:  
⏰ Reminder: Go to GYM at 18:00


---

## License
[](https://github.com/YourUsername/AdvancedToDoApp#license)

This project was created as part of the Elevate Labs Java Developer Internship and is free to use, modify, and share.
