package za.ac.cput.forreal.databaseManager;

import java.sql.*;

public class DBInitializer {

    public static void initializeDB() {
        try (Connection con = DBConnection.connect(); Statement stmt = con.createStatement()) {
            dropTables(stmt);
            createTables(stmt);
            insertStudents(stmt);

            System.out.println("Database tables created successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    private static void dropTables(Statement stmt) {
        // Drop in reverse order of dependencies
        String[] tables = {
            "mixed_class_members",
            "mixed_classes",
            "sub_courses",
            "courses",
            "users",
            "otps",
            "marks",
            "modules",
            "students"
        };

        for (String table : tables) {
            try {
                stmt.execute("DROP TABLE IF EXISTS " + table);
                System.out.println("Dropped table: " + table);
            } catch (SQLException e) {
                System.out.println("Table " + table + " doesn't exist or couldn't be dropped: " + e.getMessage());
            }
        }
    }

    
    private static void createTables(Statement stmt) throws SQLException {
    // COURSES
    stmt.execute(
        "CREATE TABLE courses (" +
        "course_id VARCHAR(10) PRIMARY KEY, " + // removed AUTO_INCREMENT
        "name VARCHAR(50) NOT NULL UNIQUE)"
    );
    System.out.println("Created table: courses");

    // SUB_COURSES
    stmt.execute(
        "CREATE TABLE sub_courses (" +
        "sub_course_id INT PRIMARY KEY AUTO_INCREMENT, " +
        "course_id VARCHAR(10) NOT NULL, " +
        "name VARCHAR(50) NOT NULL, " +
        "FOREIGN KEY (course_id) REFERENCES courses(course_id), " +
        "UNIQUE(course_id, name))"
    );
    System.out.println("Created table: sub_courses");

    // STUDENTS
    stmt.execute(
        "CREATE TABLE students (" +
        "student_number VARCHAR(20) PRIMARY KEY, " +
        "email VARCHAR(100) NOT NULL UNIQUE, " +
        "full_name VARCHAR(100) NOT NULL, " +
        "course_id VARCHAR(10) NOT NULL, " +
        "sub_course_id VARCHAR(10), " +
        "year_of_study INT NOT NULL, " +
        "FOREIGN KEY (course_id) REFERENCES courses(course_id), " +
        "FOREIGN KEY (sub_course_id) REFERENCES sub_courses(sub_course_id))"
    );
    System.out.println("Created table: students");

    // MODULES
    stmt.execute(
        "CREATE TABLE modules (" +
        "module_code VARCHAR(20) PRIMARY KEY, " +
        "module_name VARCHAR(100) NOT NULL, " +
        "course_id VARCHAR(10) NOT NULL, " + // changed to VARCHAR to match courses
        "academic_year INT NOT NULL, " +
        "FOREIGN KEY (course_id) REFERENCES courses(course_id))"
    );
    System.out.println("Created table: modules");

    // MARKS
    stmt.execute(
        "CREATE TABLE marks (" +
        "id INT PRIMARY KEY AUTO_INCREMENT, " +
        "student_number VARCHAR(20) NOT NULL, " +
        "module_code VARCHAR(20) NOT NULL, " +
        "mark INT NOT NULL CHECK (mark >= 0 AND mark <= 100), " +
        "FOREIGN KEY (student_number) REFERENCES students(student_number), " +
        "FOREIGN KEY (module_code) REFERENCES modules(module_code), " +
        "UNIQUE(student_number, module_code))"
    );
    System.out.println("Created table: marks");

    // USERS
    stmt.execute(
        "CREATE TABLE users (" +
        "user_id INT PRIMARY KEY AUTO_INCREMENT, " +
        "student_number VARCHAR(20) UNIQUE, " +
        "username VARCHAR(50) UNIQUE NOT NULL, " +
        "password VARCHAR(100) NOT NULL, " +
        "full_name VARCHAR(100) NOT NULL, " +
        "phone VARCHAR(20), " +
        "is_tutor BOOLEAN DEFAULT FALSE, " +
        "registration_complete BOOLEAN DEFAULT FALSE, " +
        "reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
        "last_login TIMESTAMP, " +
        "FOREIGN KEY (student_number) REFERENCES students(student_number))"
    );
    System.out.println("Created table: users");

    // OTPS
    stmt.execute(
        "CREATE TABLE otps (" +
        "id INT PRIMARY KEY AUTO_INCREMENT, " +
        "email VARCHAR(100) NOT NULL, " +
        "code VARCHAR(4) NOT NULL, " +
        "created TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
        "expires TIMESTAMP, " +
        "used BOOLEAN DEFAULT FALSE)"
    );
    System.out.println("Created table: otps");

    // MIXED_CLASSES
    stmt.execute(
        "CREATE TABLE mixed_classes (" +
        "mixed_class_id INT PRIMARY KEY AUTO_INCREMENT, " + // numeric AUTO_INCREMENT
        "course_id VARCHAR(10) NOT NULL, " +
        "name VARCHAR(100) NOT NULL, " +
        "year_of_study INT NOT NULL, " +
        "FOREIGN KEY (course_id) REFERENCES courses(course_id))"
    );
    System.out.println("Created table: mixed_classes");

    // MIXED_CLASS_MEMBERS
    stmt.execute(
        "CREATE TABLE mixed_class_members (" +
        "id INT PRIMARY KEY AUTO_INCREMENT, " +
        "mixed_class_id INT NOT NULL, " + // numeric foreign key
        "user_id INT NOT NULL, " +
        "FOREIGN KEY (mixed_class_id) REFERENCES mixed_classes(mixed_class_id), " +
        "FOREIGN KEY (user_id) REFERENCES users(user_id), " +
        "UNIQUE(mixed_class_id, user_id))"
    );
    System.out.println("Created table: mixed_class_members");
}

    
    private static void insertStudents(Statement stmt) throws SQLException {
        String[] students = {
            "('2301238872', '230123872@mycput.ac.za', 'Lyle','ICT', '2')"
            
        };
        for (String student : students) {
            stmt.execute("INSERT INTO students (student_number, email, full_name, course, year_of_study) VALUES " + student);
        }
        System.out.println("Inserted sample students");
    }

}
