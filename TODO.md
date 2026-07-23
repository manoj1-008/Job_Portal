# Role & Job Seeding Fix - Progress

## Completed (2 tasks)

### ✅ Task 1: Auto-seed roles on startup
**Files modified:**
- `JobportalApplication.java` — Added `seedRoles` CommandLineRunner (`@Order(1)`) that inserts `ROLE_ADMIN`, `ROLE_EMPLOYER`, `ROLE_JOBSEEKER` if missing
- `application.properties` — Added `spring.jpa.defer-datasource-initialization=true`

### ✅ Task 2: Auto-seed 20 realistic IT jobs on startup
**Files modified:**
- `JobportalApplication.java` — Added `seedJobs` CommandLineRunner (`@Order(2)`) that runs after role seeding

**How it works:**
1. Only seeds if `jobRepository.count() == 0` (idempotent)
2. Checks if a default employer exists (`placements@jobportal.com`); if not, creates one with BCrypt-encoded password
3. Inserts 20 realistic Indian IT jobs from companies: TCS, Infosys, Wipro, Accenture, Cognizant, HCLTech, IBM, Deloitte, Amazon, Microsoft, Google, Oracle, Cisco, Capgemini, Tech Mahindra, Flipkart, Zomato, Unacademy
4. Uses `@Transactional` on the internal seed method
5. No entities or repositories modified — reuses existing `Job`, `User`, `Role` entities and their repositories

## On Startup Flow
```
1. Hibernate creates tables (ddl-auto=update)
2. seedRoles (@Order=1): inserts ROLE_ADMIN, ROLE_EMPLOYER, ROLE_JOBSEEKER
3. seedJobs (@Order=2): creates default employer (if needed), inserts 20 jobs (if empty)
4. Application ready — roles & jobs available for registration/browsing
```

