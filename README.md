# SplitCost

SplitCost is an Android application designed for convenient and efficient expense tracking for shared use. This app helps you manage shared expenses among friends, family, or colleagues by allowing you to create and track multiple projects, visualize spending, and maintain a detailed record of all transactions.

![Logo](https://raw.githubusercontent.com/Anready/SplitCost/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp)

## Features

### Project Management
- [x] **Create New Projects:** Start new expense tracking projects for different groups or events.
- [x] **Edit and Delete Projects:** Modify existing projects or remove them when no longer needed.

### Data Entry and Management
- [x] **Add Expenses:** Easily add new expenses, specifying the amount, description, and involved participants.
- [x] **View Expenses:** Browse through a detailed list of all expenses for each project.
- [x] **Edit and Delete Expenses:** Update or remove expenses as needed.

### Visualization
- [x] **Charts:** Visualize your expenses with pie charts and bar graphs to get insights into your spending patterns.
- [x] **Summary View:** Get a quick overview of total expenses and individual contributions.

### Utilities
- [x] **Calculator:** Built-in calculator for quick calculations without leaving the app.
- [x] **Settings:** Customize app settings including themes, currency, and notifications.

### Database Management
- [x] **Multiple Databases:** Manage multiple databases for different purposes.
- [x] **Import/Export:** Import existing data from external sources or export your data for backup.

### Connectivity
- [ ] **Database Connection:** Seamlessly connect to and interact with your databases.
- [ ] **Synchronization:** Keep your data synchronized across multiple devices (future feature).

## Installation

### Prerequisites
- Android Studio
- Java Development Kit (JDK) 8 or higher

### Steps
1. **Clone the repository:**
   ```bash
   git clone https://github.com/Anready/SplitCost.git
   ```
2. **Open the project in Android Studio:**
   Launch Android Studio and select 'Open an existing Android Studio project', then navigate to the cloned repository.
3. **Set Variables:**
4. In file ```local.properties``` add this strings: 
   ```bash
   PASS.FOR.ZIP=YOUR_PASS
   URL.WITH.UPDATES=YOUR_URL
   ```
5. **Build the project:**
   Click on 'Build' in the top menu and select 'Rebuild Project'.
6. **Run the app:**
   Connect an Android device or use an emulator, then click 'Run' to launch the application.

## Contributing

We welcome contributions from the community! To contribute:
1. **Fork the repository:**
   Click on the 'Fork' button at the top right of the GitHub page.
2. **Create a branch:**
   ```bash
   git checkout -b feature-branch
   ```
3. **Make your changes:**
   Implement your feature or fix.
4. **Submit a pull request:**
   Push your changes to your fork and submit a pull request to the main repository.

### Coding Standards

- Follow Java and Android development best practices.
- Write clear, concise comments and documentation.
- Ensure your code passes existing tests and add new tests for your changes.

## License

This project is licensed under the Eclipse Public License 2.0 (EPL-2.0). See the [LICENSE](LICENSE) file for details.

## Feedback and Future Plans

We value your feedback and ideas for improving SplitCost. Join our [Discord server](https://discord.gg/8HrYtdQQqZ) to discuss new features and report issues.

### Planned Features
- Enhanced synchronization options.
- Advanced charting and reporting tools.
- Improved UI/UX for a better user experience.

## Acknowledgements

- Thanks to all our contributors for their hard work and dedication.
- Special thanks to the open-source community for their valuable tools and libraries.

## Contact

For more information or support, please open an issue on our [GitHub Issues page](https://github.com/Anready/SplitCost/issues).

---

Happy expense tracking!
