# CIS School Admission System

Java 17 · Spring Boot · Maven Wrapper · Spring Data JPA · Thymeleaf · MySQL

## Local setup

1. Create the development schema `4701gp_dev` if needed. For a new empty schema, run `src/main/resources/schema.sql` once in MySQL Workbench. Skip this step if the four tables are already present.
2. Select the same schema in MySQL Workbench and run `src/main/resources/data.sql` to add one sample programme and six grade rows.
3. Open PowerShell in this project folder. Set your own credentials for MySQL and, if you need to check U13, an initial local administrator password.

```powershell
$env:SAS_DB_USERNAME = 'your-mysql-user'
$env:SAS_DB_PASSWORD = 'your-mysql-password'
$env:SAS_ADMIN_PASSWORD = 'choose-a-local-admin-password'
.\mvnw.cmd spring-boot:run
```

The password in `SAS_ADMIN_PASSWORD` is hashed when the account is first created. The optional `SAS_ADMIN_EMAIL` defaults to `admin@cis.example`. An existing admin account is left as is on later starts. `SAS_DB_URL` defaults to `jdbc:mysql://localhost:3306/4701gp_dev`. Hibernate validates the schema on startup.

## First integration check

Open `http://localhost:8080/school/admission-requirements`. With a fresh seed the page should display `CIS Demo Programme — S1 seat limit: 30`. Change the sample S1 seat limit in MySQL and refresh the page to confirm the value is read from the database.

To check the protected page, open `http://localhost:8080/admin/admissions` in a private window. It should redirect to sign-in. Sign in with the local admin account to reach the empty U13 page.

## UI routes

| UI | GET route | Thymeleaf template |
|---|---|---|
| U01 | `/school/about` | `school/about.html` |
| U02 | `/school/facilities` | `school/facilities.html` |
| U03 | `/school/programmes` | `school/programmes.html` |
| U04 | `/school/courses` | `school/courses.html` |
| U05 | `/school/admission-requirements` | `school/admission-requirements.html` |
| U06 | `/register` | `identity/register.html` |
| U07 | `/verify-email` | `identity/verify-email.html` |
| U08 | `/login` | `identity/login.html` |
| U09 | `/profile` | `identity/profile.html` |
| U10 | `/applications/new` | `application/new.html` |
| U11 | `/applications` | `application/list.html` |
| U12 | `/applications/{id}` | `application/detail.html` |
| U13 | `/admin/admissions` | `admissions/list.html` |

## Work split

- Framework owner owns `config/`, `model/`, the database scripts and shared integration.
- A owns `identity/` and the identity templates. The login form already submits to Spring Security. Keep the input names `email` and `password`.
- B owns `school/` and the school templates. Replace the one-row U05 example with the final school requirements page.
- C owns `application/` and the application templates. Implement the member-ID check in application queries before returning any data.
- D owns `admissions/` and the admin template.
- E owns `offer/` and `notification/`. Agree on the U12 form action with C. Put member-only offer routes under `/applications/`.

Most HTML templates are empty placeholders. U05 displays the database sample, and U08 contains the basic login form. Registration, submissions, review, offer replies and email remain team implementation work.

##test submit
