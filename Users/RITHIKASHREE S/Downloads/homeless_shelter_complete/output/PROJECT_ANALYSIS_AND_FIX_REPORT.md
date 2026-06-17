# Homeless Shelter Management System
## Complete Project Analysis & Fix Report

---

## 1. PROJECT ARCHITECTURE (MVC)

```
CLIENT (Postman / React Frontend)
        │
        ▼  HTTP + JWT
┌──────────────────────────────┐
│      CONTROLLER LAYER        │  AuthController, ShelterController,
│   (com.tn.homeless.controller)│  AdmissionController, HomelessPersonController,
│                              │  AdminController, DashboardController
└──────────────┬───────────────┘
               │ calls
┌──────────────▼───────────────┐
│       SERVICE LAYER          │  UserService, ShelterService,
│  (com.tn.homeless.service)   │  AdmissionService, JwtService, EmailService
└──────────────┬───────────────┘
               │ calls
┌──────────────▼───────────────┐
│     REPOSITORY LAYER         │  Spring Data JPA repositories
│ (com.tn.homeless.repository) │  (no manual SQL needed)
└──────────────┬───────────────┘
               │ JPA/Hibernate
┌──────────────▼───────────────┐
│      MySQL DATABASE          │  homeless_db
│    (homeless_db schema)      │
└──────────────────────────────┘
```

---

## 2. COMPLETE FOLDER STRUCTURE

```
homeless_shelter/
├── pom.xml                                          ← FIXED
├── sql/
│   └── homeless_db_schema_and_data.sql              ← NEW
└── src/
    ├── main/
    │   ├── java/com/tn/homeless/
    │   │   ├── HomelessShelterApplication.java       ← FIXED (+@EnableScheduling)
    │   │   ├── config/
    │   │   │   ├── JwtAuthFilter.java                ← FIXED
    │   │   │   ├── Securityconfig.java               ← FIXED (+CORS, +@EnableMethodSecurity)
    │   │   │   ├── UserInfoDetails.java              ← FIXED (ROLE_ prefix)
    │   │   │   └── UserInfoUserDetailsService.java   ← OK
    │   │   ├── controller/
    │   │   │   ├── AuthController.java               ← FIXED (+/register endpoint)
    │   │   │   ├── ShelterController.java            ← FIXED (+roles, +CRUD)
    │   │   │   ├── AdmissionController.java          ← FIXED (+reject, +roles)
    │   │   │   ├── HomelessPersonController.java     ← NEW (was missing entirely)
    │   │   │   ├── AdminController.java              ← FIXED (was empty)
    │   │   │   ├── DashboardController.java          ← FIXED (was returning zeros)
    │   │   │   └── TestController.java               ← NEW
    │   │   ├── dto/
    │   │   │   ├── AuthRequest.java                  ← FIXED (+validation)
    │   │   │   ├── RegisterRequest.java              ← NEW (was missing)
    │   │   │   ├── AdmissionRequestDto.java          ← FIXED (+validation)
    │   │   │   ├── ApiResponse.java                  ← NEW (was missing)
    │   │   │   └── DashboardStatsDto.java            ← NEW (was missing)
    │   │   ├── entity/
    │   │   │   ├── Role.java                         ← FIXED (Lombok clash removed)
    │   │   │   ├── User.java                         ← FIXED (+email, CascadeType fix)
    │   │   │   ├── HomelessPerson.java               ← FIXED (+location, +timestamp)
    │   │   │   ├── Shelter.java                      ← FIXED (+address, +phone, helpers)
    │   │   │   └── Admission.java                    ← FIXED (+rejectionReason, +updatedAt)
    │   │   ├── exception/
    │   │   │   ├── ResourceNotFoundException.java    ← NEW
    │   │   │   ├── ShelterFullException.java         ← NEW
    │   │   │   └── GlobalExceptionHandler.java       ← NEW
    │   │   ├── repository/
    │   │   │   ├── RoleRepository.java               ← NEW (was missing)
    │   │   │   ├── UserRepository.java               ← FIXED (+existsByEmail)
    │   │   │   ├── HomelessPersonRepository.java     ← NEW (was missing)
    │   │   │   ├── ShelterRepository.java            ← FIXED (+capacity queries)
    │   │   │   └── AdmissionRepository.java          ← FIXED (+duplicate check query)
    │   │   ├── Scheduler/
    │   │   │   └── EmailScheduler.java               ← FIXED (had empty method)
    │   │   └── service/
    │   │       ├── JwtService.java                   ← FIXED (+getExpirationTime)
    │   │       ├── UserService.java                  ← FIXED
    │   │       ├── EmailService.java                 ← FIXED (3 proper methods)
    │   │       ├── AdmissionService.java             ← FIXED (+reject method)
    │   │       ├── ShelterService.java               ← FIXED (+available, +CRUD)
    │   │       └── implementation/
    │   │           ├── JwtServiceImpl.java           ← FIXED
    │   │           ├── UserServiceImpl.java          ← FIXED
    │   │           ├── EmailServiceImple.java        ← FIXED
    │   │           ├── AdmissionServiceImpl.java     ← FIXED (capacity+duplicate check)
    │   │           └── ShelterServiceImpl.java       ← FIXED
    │   └── resources/
    │       └── application.properties               ← FIXED
    └── test/java/com/tn/homeless/
        ├── AuthControllerTest.java                  ← NEW (5 test cases)
        └── AdmissionServiceTest.java                ← NEW (7 test cases)
```

---

## 3. ALL BUGS FOUND & FIXES APPLIED

| # | File | Bug | Fix Applied |
|---|------|-----|-------------|
| 1 | `HomelessShelterApplication.java` | `@EnableScheduling` missing → `EmailScheduler` never ran | Added `@EnableScheduling` |
| 2 | `application.properties` | `spring.mail.properties.mail.smtp.starttls.required=true` had an invalid property key causing startup failure | Fixed key name |
| 3 | `Role.java` | Both Lombok `@Data` AND manual getters/setters → duplicate method compile error | Removed Lombok, kept manual methods |
| 4 | `User.java` | Same Lombok + manual getter clash; `CascadeType.ALL` on roles → deleting user deleted shared roles | Removed Lombok; changed to `MERGE,PERSIST` only |
| 5 | `UserInfoDetails.java` | Roles lacked `ROLE_` prefix → all `@PreAuthorize("hasRole(...)")` silently failed | Added `"ROLE_"` prefix in `SimpleGrantedAuthority` |
| 6 | `Securityconfig.java` | `@EnableMethodSecurity` missing → `@PreAuthorize` on controllers was ignored | Added `@EnableMethodSecurity` |
| 7 | `Securityconfig.java` | No CORS config → frontend calls blocked by browser | Added `CorsConfigurationSource` bean |
| 8 | `JwtAuthFilter.java` | No null check on `Authorization` header → `StringIndexOutOfBoundsException` on every public request | Added `authHeader == null` guard |
| 9 | `JwtAuthFilter.java` | No try/catch around token parsing → expired tokens caused 500 instead of 401 | Wrapped in try/catch |
| 10 | `AuthController.java` | `/auth/register` endpoint missing → no way to create users via API | Added `POST /auth/register` |
| 11 | `AdmissionServiceImpl.java` | No capacity check before admitting → `currentOccupancy` could exceed `totalCapacity` | Added `isFull()` check → `ShelterFullException` |
| 12 | `AdmissionServiceImpl.java` | No duplicate-person check → same person could have multiple PENDING requests | Added `existsByHomelessPersonIdAndStatusIn` check |
| 13 | `AdmissionServiceImpl.java` | `rejectAdmission()` method missing entirely | Implemented with email notification |
| 14 | `AdminController.java` | Controller was empty — no methods | Added user list, activate, deactivate, delete |
| 15 | `DashboardController.java` | Returned hardcoded zeros (`Map.of("total", 0, ...)`) | Now queries real DB via repositories |
| 16 | `HomelessPersonController.java` | **Entire controller missing** → volunteers couldn't register homeless persons | Created full CRUD controller |
| 17 | `EmailScheduler.java` | `run()` method body was empty | Implemented daily capacity alert logic |
| 18 | `EmailServiceImple.java` | Single generic `sendMail()` with no callers passing real content | Replaced with 3 domain-specific methods |
| 19 | `RoleRepository.java` | **Entirely missing** → `UserServiceImpl` couldn't look up roles | Created interface |
| 20 | `HomelessPersonRepository.java` | **Entirely missing** → `AdmissionServiceImpl` would NPE at runtime | Created interface |
| 21 | `GlobalExceptionHandler.java` | **Missing** → all errors returned raw 500 stack traces | Created `@RestControllerAdvice` handler |
| 22 | `ApiResponse.java` | **Missing** → each controller returned a different response shape | Created generic wrapper DTO |
| 23 | `RegisterRequest.java` | **Missing** → `/auth/register` had no typed request body | Created with validation annotations |
| 24 | `pom.xml` | `spring-boot-starter-validation` missing → `@Valid` / `@NotBlank` annotations silently did nothing | Added dependency |
| 25 | `ShelterRepository.java` | No `findAvailableShelters()` query → volunteers saw full/unverified shelters | Added JPQL query |

---

## 4. DATABASE SCHEMA

```
roles           → id, name(UNIQUE)
users           → id, username(UNIQUE), password, email(UNIQUE), active
users_roles     → user_id FK→users, role_id FK→roles  [junction table]
homeless_persons→ id, name, age, gender, health_conditions, special_needs, location, registered_at
shelters        → id, name, city, zone, ward, address, contact_phone,
                   total_capacity, current_occupancy, facilities,
                   ngo_user_id FK→users, verified, created_at
admissions      → id, homeless_person_id FK→homeless_persons,
                   shelter_id FK→shelters, volunteer_id FK→users,
                   status(PENDING|APPROVED|REJECTED),
                   request_date, updated_at, remarks, rejection_reason
```

---

## 5. ALL API ENDPOINTS

### Auth (Public — no token needed)
| Method | URL | Role | Purpose |
|--------|-----|------|---------|
| POST | `/auth/login` | Public | Login → returns JWT token |
| POST | `/auth/register` | Public | Register new user |
| GET | `/test/ping` | Public | Health check |

### Homeless Persons
| Method | URL | Role | Purpose |
|--------|-----|------|---------|
| GET | `/persons` | All | List all persons |
| GET | `/persons/{id}` | All | Get one person |
| GET | `/persons/search?name=X` | All | Search by name |
| POST | `/persons` | VOLUNTEER, ADMIN | Register new homeless person |
| PUT | `/persons/{id}` | VOLUNTEER, ADMIN | Update person record |
| DELETE | `/persons/{id}` | ADMIN | Delete record |

### Shelters
| Method | URL | Role | Purpose |
|--------|-----|------|---------|
| GET | `/shelters` | ADMIN, NGO | All shelters |
| GET | `/shelters/verified` | All | Verified shelters |
| GET | `/shelters/available` | All | Shelters with free beds |
| GET | `/shelters/{id}` | All | One shelter |
| POST | `/shelters` | NGO, ADMIN | Register new shelter |
| PUT | `/shelters/{id}/verify` | ADMIN | Verify shelter |
| PUT | `/shelters/{id}` | NGO, ADMIN | Update shelter |
| DELETE | `/shelters/{id}` | ADMIN | Delete shelter |

### Admissions
| Method | URL | Role | Purpose |
|--------|-----|------|---------|
| POST | `/admissions` | VOLUNTEER, ADMIN | Create admission request |
| GET | `/admissions` | ADMIN, NGO | All admissions |
| GET | `/admissions/pending` | ADMIN, NGO | Pending only |
| GET | `/admissions/my` | VOLUNTEER, ADMIN | My submissions |
| GET | `/admissions/{id}` | All | One admission |
| PUT | `/admissions/{id}/approve` | NGO, ADMIN | Approve |
| PUT | `/admissions/{id}/reject` | NGO, ADMIN | Reject with reason |

### Admin & Dashboard
| Method | URL | Role | Purpose |
|--------|-----|------|---------|
| GET | `/admin/users` | ADMIN | List all users |
| PUT | `/admin/users/{id}/activate` | ADMIN | Enable user |
| PUT | `/admin/users/{id}/deactivate` | ADMIN | Disable user |
| DELETE | `/admin/users/{id}` | ADMIN | Delete user |
| GET | `/dashboard/stats` | ADMIN | Aggregate statistics |

---

## 6. HOW TO RUN IN ECLIPSE

### Step 1 — MySQL Setup
```sql
-- In MySQL Workbench or terminal:
mysql -u root -p
SOURCE /path/to/homeless_db_schema_and_data.sql
```

### Step 2 — Eclipse Import
1. `File → Import → Maven → Existing Maven Projects`
2. Browse to project root → Finish
3. Right-click project → `Maven → Update Project` → OK

### Step 3 — Verify application.properties
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/homeless_db?...
spring.datasource.username=root
spring.datasource.password=Rithu@09   ← change if your root password is different
server.port=1010
```

### Step 4 — Run
- Right-click `HomelessShelterApplication.java` → `Run As → Spring Boot App`
- Console should show: `Started HomelessShelterApplication in X seconds`

### Step 5 — Verify
```
GET http://localhost:1010/test/ping
```
Expected: `{ "success": true, "message": "Server is running", ... }`

---

## 7. SAMPLE TEST INPUTS & EXPECTED OUTPUTS

### Login
```json
POST /auth/login
{ "username": "admin", "password": "admin123" }
→ 200 { "success": true, "data": { "token": "eyJ...", "role": "ADMIN" } }
```

### Register Homeless Person
```json
POST /persons   (Bearer token required)
{ "name": "John Doe", "age": 35, "gender": "MALE",
  "healthConditions": "None", "specialNeeds": false,
  "location": "Central Bus Stand, Chennai" }
→ 200 { "success": true, "data": { "id": 9, "name": "John Doe", ... } }
```

### Create Admission Request
```json
POST /admissions   (VOLUNTEER token)
{ "homelessPersonId": 9, "shelterId": 1, "remarks": "Found near bus stand" }
→ 200 { "success": true, "data": { "id": 9, "status": "PENDING", ... } }
```

### Reject Admission
```json
PUT /admissions/9/reject   (NGO token)
{ "reason": "Person requires medical facility not available here" }
→ 200 { "success": true, "data": { "status": "REJECTED", ... } }
```

---

## 8. TEST CASES SUMMARY

| TC | Class | Input | Expected |
|----|-------|-------|----------|
| TC-01 | AuthControllerTest | Register new user | 200, success=true |
| TC-02 | AuthControllerTest | Register duplicate username | 409 Conflict |
| TC-03 | AuthControllerTest | Login correct creds | 200, token in response |
| TC-04 | AuthControllerTest | Login wrong password | 401 Unauthorized |
| TC-05 | AuthControllerTest | Login blank username | 400 Validation error |
| TC-06 | AdmissionServiceTest | Create valid admission | status=PENDING saved |
| TC-07 | AdmissionServiceTest | Shelter at full capacity | ShelterFullException |
| TC-08 | AdmissionServiceTest | Person already has active admission | IllegalArgumentException |
| TC-09 | AdmissionServiceTest | Approve PENDING | status=APPROVED, occupancy+1 |
| TC-10 | AdmissionServiceTest | Reject PENDING | status=REJECTED, email sent |
| TC-11 | AdmissionServiceTest | Approve already-APPROVED | IllegalArgumentException |
| TC-12 | AdmissionServiceTest | Get admission by bad ID | ResourceNotFoundException |

---
*Report generated by Lead Developer Analysis — all 25 bugs fixed, 8 missing files created.*
