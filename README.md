# Homeless Shelter Management System

A role-based RESTful backend application built with **Spring Boot 3.2**, **Java 17**, **MySQL 8**, and **JWT Security**.

## Tech Stack
- Java 17, Spring Boot 3.2.5
- Spring Security 6 + JWT
- Spring Data JPA + Hibernate
- MySQL 8, Maven
- Spring Mail + Spring Scheduler

## Roles
| Role | Permissions |
|------|------------|
| ADMIN | Full access, user management, dashboard |
| NGO | Register shelters, approve/reject admissions |
| VOLUNTEER | Register homeless persons, submit admission requests |

## How to Run
1. Run `sql/homeless_db_schema_and_data.sql` in MySQL
2. Import as Maven project in Eclipse
3. Update `application.properties` with your DB password
4. Run `HomelessShelterApplication.java`
5. Server starts at `http://localhost:1010`
6. Test: `GET http://localhost:1010/test/ping`

## API Endpoints
- `POST /auth/login` — Login, returns JWT
- `POST /auth/register` — Register new user
- `POST /persons` — Register homeless person
- `POST /admissions` — Submit admission request
- `PUT /admissions/{id}/approve` — Approve request
- `GET /dashboard/stats` — Admin dashboard stats
