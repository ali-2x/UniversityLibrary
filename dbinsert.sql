PROMPT DROP TABLES;
DROP TABLE BOOK CASCADE CONSTRAINT;
DROP TABLE STUDENT CASCADE CONSTRAINT;
DROP TABLE BORROW CASCADE CONSTRAINT;
DROP TABLE RENEW CASCADE CONSTRAINT;
DROP TABLE RESERVE CASCADE CONSTRAINT;

Create Table Book (
Call_no Char(20),
ISBN Char(20),
Title Char(20),
Author Char(20),
Amount Integer,
Location Char(30),
Primary Key (Call_no),
Unique (ISBN) 
);

Create Table Student (
Student_no CHAR(20),
Name Char(20),
Gender Char(10),
Major Char(20),
Primary Key (Student_no)
);

Create Table Borrow (
Borrower CHAR(20),
Book Char(20),
Borrow_date Date,
Due_date Date,
Primary Key (Borrower, Book),
Foreign Key (Borrower) References Student(Student_no),
Foreign Key (Book) References Book (Call_no)
);

Create Table Renew (
Student_number CHAR(20),
Renewed_book Char(20),
Primary Key(Student_number, Renewed_book),
Foreign Key(Student_number) References Student(Student_no),
Foreign Key(Renewed_book) References Book(Call_no)
);

Create Table Reserve (
Student_number CHAR(20),
Reserved_book Char(20),
Request_date Date,
Primary Key(Student_number),
Foreign Key(Student_number) References Student(Student_no),
Foreign Key (Reserved_book) References Book(Call_no)
);

COMMIT; 



PROMPT INSERT BOOK TABLE;

INSERT INTO Book VALUES ( 'A0000', '0-306-40615-1', 'AA', 'XX', 0, 'S1E01');
INSERT INTO Book VALUES ( 'B0000', '0-306-40615-2', 'BB', 'YY', 0, 'S2E02');
INSERT INTO Book VALUES ( 'C1111', '0-306-40615-3', 'CC', 'ZZ', 2, 'D1E11');
INSERT INTO Book VALUES ( 'B0001', '0-306-40615-4', 'DD', 'UU', 2, 'G1E00');
INSERT INTO Book VALUES ( 'A1111', '0-306-40615-5', 'EE', 'VV', 2, 'B1E00');
INSERT INTO Book VALUES ( 'D0101', '0-306-40615-6', 'FF', 'WW', 1, 'B2E11');
INSERT INTO Book VALUES ( 'E0000', '0-306-40615-7', 'GG', 'PP', 0, 'X0E22');
INSERT INTO Book VALUES ( 'E0100', '0-306-40615-8', 'HH', 'QQ', 2, 'X0E21');
INSERT INTO Book VALUES ( 'E0111', '0-306-40615-9', 'II', 'RR', 0, 'X0E44');


PROMPT INSERT STUDENT TABLE;

INSERT INTO Student VALUES ('12345678', 'A', 'M', 'Comp');
INSERT INTO Student VALUES ('11111111', 'B', 'M', 'Math');
INSERT INTO Student VALUES ('22222222', 'C', 'F', 'COMM');
INSERT INTO Student VALUES ('33333333', 'D', 'F', 'COMM');
INSERT INTO Student VALUES ('44444444', 'E', 'M', 'Comp');
INSERT INTO Student VALUES ('55555555', 'F', 'M', 'COMM');
INSERT INTO Student VALUES ('66666666', 'G', 'F', 'Math');
INSERT INTO Student VALUES ('77777777', 'H', 'M', 'Comp');

PROMPT INSERT BORROW TABLE;

INSERT INTO Borrow VALUES('11111111', 'D0101', to_date('24/Mar/2022', 'dd/mon/yyyy'), to_date('21/Apr/2022', 'dd/mon/yyyy'));
INSERT INTO Borrow VALUES('55555555', 'A1111', to_date('23/Mar/2022', 'dd/mon/yyyy'), to_date('20/Apr/2022', 'dd/mon/yyyy'));
INSERT INTO Borrow VALUES('22222222', 'B0000', to_date('31/Mar/2022', 'dd/mon/yyyy'), to_date('12/May/2022', 'dd/mon/yyyy'));
INSERT INTO Borrow VALUES('11111111', 'A0000', to_date('1/Apr/2022', 'dd/mon/yyyy'), to_date('29/Apr/2022', 'dd/mon/yyyy'));
INSERT INTO Borrow VALUES('33333333', 'A0000', to_date('3/Apr/2022', 'dd/mon/yyyy'), to_date('1/May/2022', 'dd/mon/yyyy'));
INSERT INTO Borrow VALUES('11111111', 'B0000', to_date('3/Apr/2022', 'dd/mon/yyyy'), to_date('15/May/2022', 'dd/mon/yyyy'));
INSERT INTO Borrow VALUES('44444444', 'C1111', to_date('4/Apr/2022', 'dd/mon/yyyy'), to_date('16/May/2022', 'dd/mon/yyyy'));
INSERT INTO Borrow VALUES('44444444', 'A0000', to_date('6/Apr/2022', 'dd/mon/yyyy'), to_date('4/May/2022', 'dd/mon/yyyy'));
INSERT INTO Borrow VALUES('33333333', 'C1111', to_date('6/Apr/2022', 'dd/mon/yyyy'), to_date('4/May/2022', 'dd/mon/yyyy'));
INSERT INTO Borrow VALUES('33333333', 'A1111', to_date('6/Apr/2022', 'dd/mon/yyyy'), to_date('4/May/2022', 'dd/mon/yyyy'));
INSERT INTO Borrow VALUES('33333333', 'B0001', to_date('6/Apr/2022', 'dd/mon/yyyy'), to_date('4/May/2022', 'dd/mon/yyyy'));
INSERT INTO Borrow VALUES('44444444', 'D0101', to_date('10/Apr/2022', 'dd/mon/yyyy'), to_date('8/May/2022', 'dd/mon/yyyy'));
INSERT INTO Borrow VALUES('33333333', 'D0101', to_date('10/Apr/2022', 'dd/mon/yyyy'), to_date('8/May/2022', 'dd/mon/yyyy'));
INSERT INTO Borrow VALUES('44444444', 'A1111', to_date('14/Apr/2022', 'dd/mon/yyyy'), to_date('12/May/2022', 'dd/mon/yyyy'));
INSERT INTO Borrow VALUES('55555555', 'C1111', to_date('18/Apr/2022', 'dd/mon/yyyy'), to_date('16/May/2022', 'dd/mon/yyyy'));
INSERT INTO Borrow VALUES('22222222', 'E0111', to_date('19/Apr/2022', 'dd/mon/yyyy'), to_date('17/May/2022', 'dd/mon/yyyy'));
INSERT INTO Borrow VALUES('11111111', 'E0000', to_date('20/Apr/2022', 'dd/mon/yyyy'), to_date('18/May/2022', 'dd/mon/yyyy'));
INSERT INTO Borrow VALUES('44444444', 'B0001', to_date('21/Apr/2022', 'dd/mon/yyyy'), to_date('19/May/2022', 'dd/mon/yyyy'));


PROMPT INSERT RESERVE TABLE;

INSERT INTO Reserve VALUES('12345678', 'A0000', to_date('20/Apr/2022', 'dd/mon/yyyy'));
INSERT INTO Reserve VALUES('66666666', 'E0000', to_date('22/Apr/2022', 'dd/mon/yyyy'));

PROMPT INSERT RENEW TABLE;

INSERT INTO Renew VALUES('22222222', 'B0000');
INSERT INTO Renew VALUES('11111111', 'B0000');
INSERT INTO Renew VALUES('44444444', 'C1111');

COMMIT;


CREATE OR REPLACE TRIGGER BORROW_UPDATE_CONSTRAINT
AFTER INSERT ON BORROW
FOR EACH ROW
BEGIN
	UPDATE BOOK SET Amount = Amount - 1 WHERE Call_no = :new.Book;
END;
.
/

CREATE OR REPLACE TRIGGER RETURN_UPDATE_CONSTRAINT
AFTER DELETE ON BORROW
FOR EACH ROW
BEGIN
	UPDATE BOOK SET Amount = Amount + 1 WHERE Call_no = :old.Book;
END;
.
/


CREATE OR REPLACE TRIGGER RENEWED_BOOK_CONSTRAINT
AFTER INSERT ON Renew
FOR EACH ROW
BEGIN
	UPDATE BORROW
	SET Due_date = (to_date(Due_date)+14)
	WHERE Borrower = :NEW.Student_number AND Book = :NEW.Renewed_book;
END;
.
/

CREATE OR REPLACE TRIGGER BORROW_RESERVEDBOOK_CONSTRAINT
AFTER INSERT ON BORROW 
FOR EACH ROW
DECLARE 
	A INTEGER;
BEGIN 
    SELECT COUNT(*) INTO A FROM Reserve WHERE Reserved_Book = :new.Book AND Student_Number = :new.Borrower;
	IF (A > 0) THEN
		DELETE FROM RESERVE WHERE Student_Number = :new.Borrower;
	END IF;
END;
.
/


SET AUTOCOMMIT ON