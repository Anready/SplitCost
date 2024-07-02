# SplitCost

SplitCost is an Android application designed for convenient and efficient expense tracking for shared use. This app helps you manage shared expenses among friends, family, or colleagues by allowing you to create and track multiple projects, visualize spending, and maintain a detailed record of all transactions.

<table>
  <tbody>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/Anready"><img src="https://avatars.githubusercontent.com/u/104269567?v=4" width="100px;" alt="Anready"/><br /><sub><b>Anready</b></sub></a><br /><a href="https://github.com/Anready/SplitCost/tree/master/app" title="code">💻</a><a href="https://github.com/Anready/" title="owner">👑</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/coderGtm"><img src="https://avatars.githubusercontent.com/u/66418526?v=4?s=100" width="100px;" alt="Gautam Mehta"/><br /><sub><b>Gautam Mehta</b></sub></a><br /> <a href="#minor_code" title="Reviewed clean code">Minor Code</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/marco-tuzza"><img src="https://avatars.githubusercontent.com/u/62022949?v=4" width="100px;" alt="marco-tuzza"/><br /><sub><b>marco-tuzza</b></sub></a><br /><a href="#features" title="features">Features</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://www.instagram.com/ky_sonnya"><img src="https://instagram.fnic3-1.fna.fbcdn.net/v/t51.2885-19/435862575_1639616420176784_937014843031793923_n.jpg?stp=dst-jpg_s150x150&_nc_ht=instagram.fnic3-1.fna.fbcdn.net&_nc_cat=108&_nc_ohc=cd737EmdsqIQ7kNvgFLqKxf&edm=ADW0ovcBAAAA&ccb=7-5&oh=00_AYB_CwQSc6UGK8CsuGm7nMd2ZQdQu0AbAxH5YJVF8DszAw&oe=6689BBC8&_nc_sid=db7772" width="100px;" alt="ky_sonnya"/><br /><sub><b>ky_sonnya</b></sub></a><br /><a href="#design" title="design">Design</a></td>
      <td align="center" valign="top" width="14.28%"><a href=""><img src="https://cdn.discordapp.com/avatars/1018064591391047680/a0a68be8c0334c1dd05f5d9499cd0e28.webp?size=160" width="100px;" alt="h.h"/><br /><sub><b>H.H</b></sub></a><br /><a href="#logo" title="logo">Logo</a></td>
    </tr>
  </tbody>
</table>

<div id="logo" style="justify-content: center">
   <img src="https://raw.githubusercontent.com/Anready/SplitCost/master/app/src/main/ic_launcher-playstore.png" />
</div>

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
   In file ```local.properties``` add this strings: 
   ```bash
   PASS.FOR.ZIP=YOUR_PASS
   URL.WITH.UPDATES=YOUR_URL
   ```
4. **Set-up an update system:**
   You need to create a file which can be accessed with a GET request without authentication (As example on GitHub), after that you should add this to this file:
   ```json
   {
      "com.codersanx.splitcost": {
        "name": "SplitCost",
        "version": "YOUR_VERSION",
        "version_code": "YOUR_VERSION_CODE",
        "description": "YOUR_DESCRIPTION",
        "stores": {
          "YOUR_LINK": ""
        }
      }
   }
   ```
   In this code:
   
   ```version```: you need to put actual version of your modified app (in build.gradle)
   
   ```version_code```: you need to put actual version code of your modified app (in build.gradle)
   
   ```description```: you need to put description what's new in new update
   
   ```stores```: you need to put link on your new github update - AS KEY, NOT AS VALUE
    
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

<div id="minor_code"/>
  
## Minor Code 
Thanks [CoderGTM](https://github.com/coderGTM/) for code review and recommendations, also some parts of the code were copied from his projects, thanks

## Design
Big thanks [ky_sonnya](https://www.instagram.com/ky_sonnya) for design

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
