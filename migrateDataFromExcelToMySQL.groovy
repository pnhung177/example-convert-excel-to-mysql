@Grapes([
    @GrabConfig(systemClassLoader=true),
    @Grab(group='net.sourceforge.jexcelapi', module='jxl', version='2.6.12'),
    @Grab(group='mysql', module='mysql-connector-java', version='5.1.13')
])

import groovy.sql.Sql
import java.util.UUID
import jxl.*
import jxl.write.*

def logEnabled = true;

def db_driverClassName = "com.mysql.jdbc.Driver"
def db_url = "jdbc:mysql://127.0.0.1:3306/bookstore?characterEncoding=utf-8"
def db_username = "bookstore"
def db_password = "n0passw0rd"

def sql = Sql.newInstance(db_url, db_username, db_password, db_driverClassName)
sql.execute("DROP TABLE IF EXISTS `books`;");
sql.execute("CREATE TABLE `books` (" +
    "`f_id` VARCHAR( 36 ) NOT NULL ," +
    "`f_title` VARCHAR( 255 ) NOT NULL ," +
    "`f_isbn` VARCHAR( 16 ) NULL ," +
    "`f_author` VARCHAR( 128 ) NULL ," +
    "`f_publisher` VARCHAR( 64 ) NULL ," +
    "`f_pages` INT NULL ," +
    "`f_price` VARCHAR( 10 ) NULL ," +
    "`f_date` VARCHAR( 10 ) NULL ," +
    "PRIMARY KEY (  `f_id` )" +
    ") ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_general_ci;");

// CHANGE PATH TO YOUR EXCEL DATA FILE
def excelFile = "/home/pnhung177/projects/example-convert-excel-to-mysql/data/bookstore.xls";

def maxRows = 156;
def maxCols = 10;

def startRow = 3;
def startCol = 0;

if (logEnabled) {
    println "=@ Transfer data from excel to mysql ..."
}

def wb = Workbook.getWorkbook(new File(excelFile));
def sheet = wb.getSheet(0);

for(int j=0; j<maxRows; j++) {
    logEnabled ? print("=@${j + 1}|") : null
    
    def RV = [];
    for(int l=0; l<maxCols; l++) {
        RV.add(sheet.getCell(startCol + l, startRow + j).getContents());
        logEnabled ? print("${RV[startCol + l]}|") : null
    }
    
    sqlString = "INSERT INTO books " + 
        "(f_id, f_title, f_isbn, f_author, f_publisher, f_pages, f_price, f_date)" + 
        " VALUES ('${UUID.randomUUID().toString()}', '${RV[1]}', '${RV[6]}', " +
            "'${RV[2]}', '${RV[5]}', ${RV[3]?RV[3]:null}, '${RV[7]}', '${RV[8]}')";
    
    sql.execute(sqlString);
    
    logEnabled ? println() : null;
}

logEnabled ? println("=@ Closing workbook") : null
wb.close();

logEnabled ? println("=@ Closing database") : null
sql.close();

logEnabled ? println("=@ Finished!") : null
