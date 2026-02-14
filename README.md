# 🎮 Escape Room Business System

A comprehensive desktop application for managing an escape room business, built with Java and JavaFX. This system provides a complete solution for managing escape rooms, bookings, players, clues, and business operations.

## 📋 Table of Contents

- [Features](#-features)
- [Technologies Used](#-technologies-used)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Usage](#-usage)
- [Project Structure](#-project-structure)
- [User Roles](#-user-roles)
- [Database Schema](#-database-schema)
- [Building and Running](#-building-and-running)
- [Contributing](#-contributing)
- [License](#-license)

## ✨ Features

### 🎯 Core Functionality
- **Escape Room Management**: Create, update, and manage escape rooms with different difficulty levels
- **Booking System**: Complete booking management with time slot validation and conflict detection
- **User Management**: Three-tier user system (Admin, Staff, Customer) with role-based access
- **Clue & Puzzle System**: Manage clues and puzzles for each escape room
- **Player Tracking**: Track player progress, solved clues, and time elapsed
- **Reporting System**: Generate business reports with comprehensive data analysis
- **Secure Authentication**: Password hashing using BCrypt for secure user authentication

### 👥 Role-Based Dashboards
- **Admin Dashboard**: Full system control including room management, user management, booking oversight, and player monitoring
- **Staff Dashboard**: Manage bookings, view rooms, and assist customers
- **Customer Dashboard**: Browse available rooms, make bookings, and view booking history

### 🔒 Security Features
- Secure password hashing with jBCrypt
- Environment variable-based database configuration
- Role-based access control

## 🛠 Technologies Used

- **Java 23**: Core programming language
- **JavaFX 17.0.6**: Modern UI framework for desktop applications
- **Maven**: Dependency management and build automation
- **PostgreSQL**: Database (via Supabase)
- **MySQL Connector**: Database connectivity
- **jBCrypt 0.4**: Password hashing library
- **Gson 2.10.1**: JSON processing
- **Dotenv Java 3.2.0**: Environment variable management
- **JUnit 5.10.2**: Unit testing framework

## 📦 Prerequisites

Before running this application, ensure you have the following installed:

- **Java Development Kit (JDK) 23** or higher
- **Maven 3.6+**
- **PostgreSQL Database** (or Supabase account)
- **Git** (for cloning the repository)

## 🚀 Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd Escape-Room
   ```

2. **Install dependencies**
   ```bash
   mvn clean install
   ```

3. **Set up environment variables**
   - Create a `.env` file in the root directory
   - Add your database credentials (see Configuration section)

## ⚙️ Configuration

Create a `.env` file in the project root with the following variables:

```env
SUPABASE_DB_URL=jdbc:postgresql://db.[project-ref].supabase.co:5432/postgres
SUPABASE_DB_USER=postgres
SUPABASE_DB_PASSWORD=your_password_here
```

**Note**: Replace `[project-ref]` with your actual Supabase project reference.

## 💻 Usage

### Running the Application

**Option 1: Using Maven**
```bash
mvn clean javafx:run
```

**Option 2: Using Java directly**
```bash
mvn clean compile
java --module-path <path-to-javafx> --add-modules javafx.controls,javafx.fxml -cp target/classes com.example.escaperoombusinesssystem.App
```

### Application Workflow

1. **Login**: Start by logging in with your credentials
2. **Navigate**: Use the dashboard navigation buttons to access different sections
3. **Manage**: Based on your role, you can:
   - **Admins**: Manage rooms, users, bookings, and view reports
   - **Staff**: View and manage bookings, assist customers
   - **Customers**: Browse rooms, make bookings, view booking history

## 📁 Project Structure

```
Escape-Room/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/escaperoombusinesssystem/
│   │   │       ├── App.java                    # Main application entry point
│   │   │       ├── TitleBar.java               # Custom title bar component
│   │   │       ├── controller/                 # MVC Controllers
│   │   │       │   ├── AdminController.java
│   │   │       │   ├── CustomerController.java
│   │   │       │   ├── LoginController.java
│   │   │       │   └── StaffController.java
│   │   │       ├── database/
│   │   │       │   └── DBConnector.java        # Database connection handler
│   │   │       └── model/                      # Data models
│   │   │           ├── Booking.java
│   │   │           ├── BookingStatus.java
│   │   │           ├── Business.java
│   │   │           ├── Clue.java
│   │   │           ├── EscapeRoom.java
│   │   │           ├── Player.java
│   │   │           ├── PuzzleClue.java
│   │   │           ├── Report.java
│   │   │           ├── Solvable.java
│   │   │           └── user/
│   │   │               ├── Admin.java
│   │   │               ├── Customer.java
│   │   │               ├── Staff.java
│   │   │               └── User.java
│   │   └── resources/
│   │       ├── com/example/escaperoombusinesssystem/
│   │       │   ├── view/                       # FXML UI files
│   │       │   │   ├── adminDashboard.fxml
│   │       │   │   ├── customerDashboard.fxml
│   │       │   │   ├── loginView.fxml
│   │       │   │   ├── registerView.fxml
│   │       │   │   └── staffDashboard.fxml
│   │       │   ├── bg.jpg                      # Background image
│   │       │   └── icon.png                    # Application icon
│   │       └── css/
│   │           └── styles.css                  # Stylesheet
│   └── module-info.java                        # Java module configuration
├── pom.xml                                      # Maven configuration
├── mvnw                                         # Maven wrapper (Unix)
├── mvnw.cmd                                     # Maven wrapper (Windows)
└── README.md                                    # This file
```

## 👤 User Roles

### 🔴 Admin
- Full system access
- Create, update, and delete escape rooms
- Manage all users (create, update, delete)
- View and manage all bookings
- Monitor player activities
- Generate business reports
- Activate/deactivate rooms

### 🟡 Staff
- View available escape rooms
- Manage bookings (view, update status)
- Assist customers with bookings
- View booking history

### 🟢 Customer
- Browse available escape rooms
- Make new bookings
- View personal booking history
- Cancel own bookings
- View room details and difficulty levels

## 🗄 Database Schema

The application uses PostgreSQL (Supabase) with the following main entities:

- **Users**: Admin, Staff, Customer accounts
- **Escape Rooms**: Room details, difficulty, max players
- **Bookings**: Customer bookings with time slots
- **Players**: Player information and progress
- **Clues**: Clues and puzzles for each room
- **Reports**: Generated business reports

## 🔨 Building and Running

### Build the project
```bash
mvn clean package
```

### Run tests
```bash
mvn test
```

### Create executable JAR
```bash
mvn clean package
java -jar target/EscapeRoomBusinessSystem-1.0-SNAPSHOT.jar
```

## 🎨 UI Features

- Modern JavaFX interface with custom styling
- Responsive table views for data management
- Form dialogs for data entry
- Navigation between different views
- Custom title bar with application branding
- Professional color scheme and layout

## 🔍 Key Algorithms

- **Binary Search**: Efficient room searching by ID (O(log n))
- **Selection Sort**: Sorting rooms by ID for optimized search
- **Time Slot Validation**: Prevents booking conflicts
- **Password Hashing**: Secure BCrypt implementation

## 📝 Notes

- Working hours: 10:00 AM - 10:00 PM
- Minimum players per booking: 2
- Maximum players determined by room capacity
- Bookings cannot be made for past dates/times
- Room status can be toggled (active/inactive)

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👨‍💻 Development

### Code Style
- Follow Java naming conventions
- Use meaningful variable and method names
- Add comments for complex logic
- Maintain MVC architecture pattern

### Testing
- Unit tests for business logic
- Integration tests for database operations
- UI tests for critical user flows

## 🐛 Known Issues

- Ensure `.env` file is properly configured before running
- Database connection requires active internet (for Supabase)
- JavaFX runtime must be available in classpath

## 📞 Support

For issues, questions, or contributions, please open an issue in the repository.

---

**Built with ❤️ using Java and JavaFX**

