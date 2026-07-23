# Role Seeding Fix - Progress

## Problem
Both Student and Employer registration fail with "Invalid role" errors on fresh deployments because the `roles` table is never seeded.

## Root Cause
- No `CommandLineRunner`, `data.sql`, or `@PostConstruct` method exists to insert roles on startup
- With a fresh PostgreSQL database, the `roles` table is empty
- `roleRepository.findByName("ROLE_JOBSEEKER")` returns empty, throwing `BadRequestException`

## Files Modified (2)

### ✅ `src/main/java/com/onlinejobportal/JobportalApplication.java`
- Added `@Slf4j` annotation
- Added `seedRoles(CommandLineRunner)` bean
- On startup, checks if each role exists via `existsByName()`
- If missing, inserts: `ROLE_ADMIN`, `ROLE_EMPLOYER`, `ROLE_JOBSEEKER`
- Fully idempotent: safe for repeated restarts

### ✅ `src/main/resources/application.properties`
- Added `spring.jpa.defer-datasource-initialization=true`
- Ensures Hibernate DDL creates tables BEFORE the `CommandLineRunner` runs
- Prevents timing issues where roles try to insert before the roles table exists

## No Other Files Changed
- All registration logic, controllers, services, security config, HTML templates already use consistent role names: `ROLE_JOBSEEKER` and `ROLE_EMPLOYER`
- No other modifications needed

