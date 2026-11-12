# CrDrReport (`0.0.9e-SNAPSHOT`)

Multi-company web accounting & financial reporting application built with **Java 8** and **Spring Boot 2.7.18**. Supports importing company ledgers from **MS Access (`.mdb`)** databases or **CSV** files, selecting companies/financial years, generating **Daybook**, **Ledger**, **Trial Balance**, **Trading & Profit & Loss**, and **Balance Sheet** reports, and exporting ledgers to **PDF** via JasperReports.

---

## Framework & Library Versions

| Component | Version |
|---|---|
| **Java** | `1.8` (`source` & `target` `1.8`) |
| **Spring Boot** | `2.7.18` |
| **Spring Framework / Security** | Managed by Boot `2.7.18` (`5.3.x` / `5.7.x` with `SecurityFilterChain`) |
| **Spring Data JPA / Hibernate** | Managed by Boot `2.7.18` (`5.6.x`) |
| **Thymeleaf** | Managed by Boot `2.7.18` (`3.0.15.RELEASE`) |
| **Database Driver (Runtime)** | `com.mysql:mysql-connector-j` |
| **MS Access Import** | `net.sf.ucanaccess:ucanaccess` `5.0.1` |
| **PDF Reporting** | `net.sf.jasperreports:jasperreports` `6.18.1` |
| **Number & Currency Formatting** | `com.ibm.icu:icu4j` `65.1` (`en-IN` locale) |
| **CSV Parsing** | `org.apache.commons:commons-csv` `1.9.0` |
| **Logging** | `spring-boot-starter-log4j2` (via Boot `2.7.18`, `RollingFile` in `logs/`) |
| **Test Database** | `com.h2database:h2` (`<scope>test</scope>`, MySQL compatibility mode) |

---

## Configuration & Environment Variables

Database credentials are externalized via environment variables in `src/main/resources/application.properties` (and `application-deploy.properties` / `application-aws.properties`):

| Variable | Default (`application.properties`) | Purpose |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/crdr2` | MySQL JDBC URL |
| `DB_USERNAME` | `root` | MySQL username |
| `DB_PASSWORD` | *(empty)* | MySQL password |
| `AWS_DB_URL`, `AWS_DB_USERNAME`, `AWS_DB_PASSWORD`, `AWS_DDL_AUTO`, `PORT` | — | Overrides used when the `aws` Maven/Spring profile is active |

> [!NOTE]
> Spring Boot Actuator exposes only `health` and `info` publicly (`/actuator/health`, `/actuator/info`). Other actuator endpoints and user-administration routes require `admin` or `developer` authority.

---

## Build & Test

Use the included Maven Wrapper (`mvnw.cmd` on Windows, `./mvnw` on Linux/macOS):

1. **Run the self-contained H2 test suite (no MySQL required):**
   ```powershell
   .\mvnw.cmd test
   ```
2. **Build the executable JAR:**
   ```powershell
   .\mvnw.cmd clean package
   ```
   Output artifact: `target/CrDrReport-0.0.12-SNAPSHOT.jar`

   *Available Maven/Spring profiles:* `dev` (default), `deploy` (initializes schema/data via `data.sql`), `aws`. Example:
   ```powershell
   .\mvnw.cmd clean package -Pdeploy
   ```

---

## Start & Run

1. **Default H2 Database (Zero External Setup):**
   By default, the application uses a persistent file-based H2 database in MySQL compatibility mode (`./data/crdr.mv.db`), seeds the initial schema/data, and enables the H2 Console at `/h2-console` (`admin`/`developer` roles).

   Configurable via environment variables:
   - `DB_URL` (default: `jdbc:h2:file:./data/crdr;MODE=MySQL;NON_KEYWORDS=YEAR;AUTO_SERVER=TRUE`)
   - `DB_USERNAME` (default: `sa`)
   - `DB_PASSWORD` (default: empty)
   - `DB_DDL_AUTO` (default: `update`)
   - `SQL_INIT_MODE` (default: `always`)
   - `H2_CONSOLE_ENABLED` (default: `true`; set `false` in production)

2. **Optional MySQL Override:**
   Activate the `mysql` profile (`--spring.profiles.active=mysql`) or set your MySQL environment variables:
   ```powershell
   $env:DB_URL = "jdbc:mysql://localhost:3306/crdr2"
   $env:DB_USERNAME = "your_user"
   $env:DB_PASSWORD = "your_password"
   $env:DB_DDL_AUTO = "none"
   ```
3. Start the application using either the packaged JAR or Maven:
   ```powershell
   java -jar target/CrDrReport-0.0.12-SNAPSHOT.jar
   ```
   *(or `.\mvnw.cmd spring-boot:run`)*
4. Open `http://localhost:8080` in your browser (port `5000` when running the `aws` profile unless `PORT` is set).

---

## How to Use

1. **Sign In:** Go to `/login` and authenticate with your user email and password.
2. **Upload Data:**
   - **MS Access (`.mdb`):** Go to `/upload` (`Upload Company`) to upload an `.mdb` database file; companies, account heads, opening/closing balances, daybook entries, and schedules are imported under your user account.
   - **CSV:** Go to `/uploadcsv` to upload Daybook CSV files (`Sno,TDate,Narration,acccode,dramt,cramt,stkvalue`) for the selected company.
3. **Select Company & Financial Year:** On `/company_selection`, choose your company and year, then proceed to `/reports`.
4. **Generate Reports:**
   - **Daybook** (`/findDaybook` → `/daybooks`)
   - **Ledger** (`/findLedger` → `/ledger`, or `/pdfLedger` for JasperReports PDF generation under `pdf/`)
   - **Trial Balance** (`/findTrialBal` → `/trial`)
   - **Trading & Profit and Loss** (`/findTradingPL` → `/tradingPL`)
   - **Balance Sheet** (`/findBalSheet` → `/BalanceSheet`)
5. **Administration (`admin` / `developer` roles):**
   - Create users (`/create_user`), list users (`/users`), delete users (`/delete_user`), and delete companies (`/delete_company`).

---

## MS Access Export to MySQL (Manual Reference)

If exporting `.mdb` tables manually via ODBC:
1. Open the database and table in MS Access.
2. Select **External Data → More → Export to ODBC Database**.
3. Choose **Machine Data Source** → select your MySQL DSN (or configure a User DSN in Windows **ODBC Data Sources**).
