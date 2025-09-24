package za.ac.cput.forreal.databaseManager;

import java.sql.*;

public class DBInitializer {

    public static void initializeDB() {
        try (Connection con = DBConnection.connect(); Statement stmt = con.createStatement()) {
            dropTables(stmt);
            createTables(stmt);
            insertCourses(stmt);
            insertSub(stmt);
            insertStudents(stmt);
            insertSampleModules(stmt);
            insertSampleMarks(stmt);

            System.out.println("Database tables created successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void dropTables(Statement stmt) {
        String[] tables = {"marks", "users", "modules", "students", "sub_courses", "courses", "otps"};

        for (String table : tables) {
            try {
                stmt.execute("DROP TABLE IF EXISTS " + table);
                System.out.println("Dropped table: " + table);
            } catch (SQLException e) {
                System.out.println("Table " + table + " couldn't be dropped: " + e.getMessage());
            }
        }
    }

    private static void createTables(Statement stmt) throws SQLException {
        // COURSES
        stmt.execute(
            "CREATE TABLE courses (" +
            "course_id VARCHAR(10) PRIMARY KEY, " +
            "course_name VARCHAR(50) NOT NULL UNIQUE)"
        );
        System.out.println("Created table: courses");

        // SUB_COURSES
        stmt.execute(
            "CREATE TABLE sub_courses (" +
            "sub_course_id VARCHAR(10) PRIMARY KEY, " +
            "course_id VARCHAR(10) NOT NULL, " +
            "sub_name VARCHAR(50) NOT NULL, " +
            "FOREIGN KEY (course_id) REFERENCES courses(course_id), " +
            "UNIQUE(course_id, sub_name))"
        );
        System.out.println("Created table: sub_courses");

        // STUDENTS (no foreign key)
        stmt.execute(
            "CREATE TABLE students (" +
            "student_number VARCHAR(15) PRIMARY KEY, " +
            "email VARCHAR(100) NOT NULL UNIQUE, " +
            "full_name VARCHAR(100) NOT NULL, " +
            "sub_name VARCHAR(50) NOT NULL, " +
            "year_of_study INT NOT NULL)"
        );
        System.out.println("Created table: students");

        // MODULES (no foreign key)
        stmt.execute(
            "CREATE TABLE modules (" +
            "module_code VARCHAR(10) PRIMARY KEY, " +
            "module_name VARCHAR(100) NOT NULL, " +
            "sub_name VARCHAR(50) NOT NULL, " +
            "academic_year INT NOT NULL)"
        );
        System.out.println("Created table: modules");

        // MARKS
        stmt.execute(
            "CREATE TABLE marks (" +
            "id INT PRIMARY KEY AUTO_INCREMENT, " +
            "student_number VARCHAR(15) NOT NULL, " +
            "module_code VARCHAR(10) NOT NULL, " +
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
            "student_number VARCHAR(15) UNIQUE, " +
            "username VARCHAR(50) UNIQUE, " +
            "password VARCHAR(100), " +
            "full_name VARCHAR(100) NOT NULL, " +
            "phone VARCHAR(20) UNIQUE, " +
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
    }

    private static void insertCourses(Statement stmt) throws SQLException {
        String[] courses = {
            "('DPICT', 'Dip: Information Communication Technology')"
        };
        for (String course : courses) {
            stmt.execute("INSERT INTO courses (course_id, course_name) VALUES " + course);
        }
        System.out.println("Inserted sample courses");
    }

    private static void insertSub(Statement stmt) throws SQLException {
        String[] sub_courses = {
            "('DPICTA', 'DPICT', 'ICT: Application Development')",
            "('DPICTC', 'DPICT', 'ICT: Communication Networking')",
            "('DPICTM', 'DPICT', 'ICT: Multimedia')"
        };
        for (String subs : sub_courses) {
            stmt.execute("INSERT INTO sub_courses (sub_course_id, course_id, sub_name) VALUES " + subs);
        }
        System.out.println("Inserted sample sub courses");
    }

    private static void insertStudents(Statement stmt) throws SQLException {
        String[] students = {
            "('230123872', '230123872@mycput.ac.za', 'Lyle','ICT: Application Development', 2)"
        };
        for (String student : students) {
            stmt.execute("INSERT INTO students (student_number, email, full_name, sub_name, year_of_study) VALUES " + student);
        }
        System.out.println("Inserted sample students");
    }

    private static void insertSampleModules(Statement stmt) throws SQLException {
        String[] modules = {
            "('ADF1', 'Application Development Fundamentals', 'ICT: Application Development', 1)",
            "('MUF1', 'Multimedia Fundamentals', 'ICT: Application Development', 1)",
            "('PRG1', 'Programming', 'ICT: Application Development', 1)",
            "('ADP2', 'Application Development Practice', 'ICT: Application Development', 2)",
            "('PRT2', 'Projects', 'ICT: Application Development', 2)",
            "('INM2', 'Information Management', 'ICT: Application Development', 2)"
        };
        for (String module : modules) {
            stmt.execute("INSERT INTO modules (module_code, module_name, sub_name, academic_year) VALUES " + module);
        }
        System.out.println("Inserted sample modules");
    }

    private static void insertSampleMarks(Statement stmt) throws SQLException {
        String[] marks = {
            "('230123872', 'ADF1', 76)",
            "('230123872', 'MUF1', 63)",
            "('230123872', 'PRG1', 88)"
        };
        for (String mark : marks) {
            stmt.execute("INSERT INTO marks (student_number, module_code, mark) VALUES " + mark);
        }
        System.out.println("Inserted sample marks");
    }
}
