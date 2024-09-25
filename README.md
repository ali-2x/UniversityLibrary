# University Library Management System

**Overview**

This project is a University Library Management System developed. The system manages the borrowing, returning, renewing, and reserving of books for a university library, integrating a relational database with a Java application. The application uses JDBC driver to access an Oracle database and is implemented with PL/SQL triggers to enforce various rules.

**Features**

* Book Search: Search for books by ISBN and view availability, title, author, amount of copies available, and location.
* Book Borrow/Return: Students can borrow books if they meet the requirements. Books can be returned, updating the records automatically.
* Book Renewal: Books can be renewed once during the second half of the borrowing period.
* Book Reservation: Students can reserve books if all copies are borrowed.
* Automatic Updates: A trigger in the database automatically updates the availability of books when a book is borrowed or returned.

**Database**

*Tables*:
  - Books: Stores book information including title, author, ISBN, number of copies, and location.
  * Students: Stores student information including name, gender, major, and student number.
  * Borrow: Logs all borrowing transactions including student number, book call number, borrow date, and due date.
  * Reserve: Logs book reservation details.
  * Renew: Logs the records when a student re-borrows a book.

**Setup**
1.) Before running the program, make sure to execute the sql files in your Oracle database to drop tables & create the necessary tables, triggers, and initial values for the Book and Student tables.
* Terminal:
@ ./group3_dbdrop.sql

@ ./group3_dbinsert.sql

2.) Change your login and password for Oracle Database in LibraryManager.java:
```
public boolean loginDB() {
		String username = "yourUSERNAME";// Replace your username
		String password = "yourPASSWORD";// Replace your password
		...
		}
```


**Usage**
To run the program, simply compile and run UniversityBookshop.java. You will be prompted to select an option from the menu to perform the desired operation. Follow the on-screen instructions to complete each operation.

* Search for Books: Search for a book by its ISBN to check availability and details.
* Borrow a Book: Borrow a book if available and if the student has no overdue books and fewer than 5 books borrowed.
* Return a Book: Return a borrowed book, updating the book's availability in the system.
* Renew a Book: Renew a book during the second half of the borrowing period if no reservations exist.
* Reserve a Book: Reserve a book if all copies are borrowed, with no current reservations by the student.

With these options, the University Library manager can effectively manage the book inventory and provide timely and efficient service to its students.

**Acknowledgments**

This project was developed Database Management course at HKBU.
Special thanks to Dr. Jianliang Xu, Head & Chair Professor, for guidance and support.

