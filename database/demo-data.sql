-- =============================================
-- JOBPORTAL - DEMO SEED DATA
-- Realistic Indian Data for Production Demo
-- =============================================
-- IMPORTANT: Run this AFTER Spring Boot creates the tables (ddl-auto=update)
-- Passwords: All users use BCrypt hash of 'password123'
-- =============================================

-- Clean existing data (in correct FK order)
DELETE FROM saved_jobs;
DELETE FROM job_applications;
DELETE FROM resumes;
DELETE FROM jobs;
DELETE FROM users;
DELETE FROM roles;

-- =============================================
-- 1. ROLES
-- =============================================
INSERT INTO roles (id, name) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_EMPLOYER'),
(3, 'ROLE_JOBSEEKER');

-- Reset sequences
ALTER SEQUENCE roles_id_seq RESTART WITH 4;

-- =============================================
-- 2. USERS (1 Admin + 10 Employers + 25 Job Seekers = 36 total)
-- BCrypt hash for 'password123'
-- =============================================
-- Admin
INSERT INTO users (id, full_name, email, password, phone, enabled, created_at, updated_at, role_id) VALUES
(1, 'Admin User', 'admin@jobportal.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9876543210', true, '2024-01-01 00:00:00', '2024-01-01 00:00:00', 1);

-- 10 Employers
INSERT INTO users (id, full_name, email, password, phone, enabled, created_at, updated_at, role_id) VALUES
(2, 'Rajesh Sharma', 'rajesh@tcs.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9876543201', true, '2024-01-02 00:00:00', '2024-01-02 00:00:00', 2),
(3, 'Priya Patel', 'priya@infosys.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9876543202', true, '2024-01-03 00:00:00', '2024-01-03 00:00:00', 2),
(4, 'Amit Verma', 'amit@wipro.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9876543203', true, '2024-01-04 00:00:00', '2024-01-04 00:00:00', 2),
(5, 'Sneha Reddy', 'sneha@hcl.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9876543204', true, '2024-01-05 00:00:00', '2024-01-05 00:00:00', 2),
(6, 'Vikram Singh', 'vikram@techmahindra.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9876543205', true, '2024-01-06 00:00:00', '2024-01-06 00:00:00', 2),
(7, 'Anjali Gupta', 'anjali@lnt.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9876543206', true, '2024-01-07 00:00:00', '2024-01-07 00:00:00', 2),
(8, 'Rahul Joshi', 'rahul@flipkart.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9876543207', true, '2024-01-08 00:00:00', '2024-01-08 00:00:00', 2),
(9, 'Neha Kapoor', 'neha@zomato.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9876543208', true, '2024-01-09 00:00:00', '2024-01-09 00:00:00', 2),
(10, 'Deepak Mishra', 'deepak@swiggy.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9876543209', true, '2024-01-10 00:00:00', '2024-01-10 00:00:00', 2),
(11, 'Kavita Desai', 'kavita@paytm.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9876543211', true, '2024-01-11 00:00:00', '2024-01-11 00:00:00', 2);

-- 25 Job Seekers
INSERT INTO users (id, full_name, email, password, phone, enabled, created_at, updated_at, role_id) VALUES
(12, 'Arun Kumar', 'arun.kumar@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9000000001', true, '2024-02-01 00:00:00', '2024-02-01 00:00:00', 3),
(13, 'Bhavna Singh', 'bhavna.singh@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9000000002', true, '2024-02-02 00:00:00', '2024-02-02 00:00:00', 3),
(14, 'Chirag Shah', 'chirag.shah@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9000000003', true, '2024-02-03 00:00:00', '2024-02-03 00:00:00', 3),
(15, 'Divya Nair', 'divya.nair@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9000000004', true, '2024-02-04 00:00:00', '2024-02-04 00:00:00', 3),
(16, 'Esha Mehta', 'esha.mehta@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9000000005', true, '2024-02-05 00:00:00', '2024-02-05 00:00:00', 3),
(17, 'Farhan Khan', 'farhan.khan@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9000000006', true, '2024-02-06 00:00:00', '2024-02-06 00:00:00', 3),
(18, 'Gauri Joshi', 'gauri.joshi@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9000000007', true, '2024-02-07 00:00:00', '2024-02-07 00:00:00', 3),
(19, 'Harsh Vardhan', 'harsh.vardhan@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9000000008', true, '2024-02-08 00:00:00', '2024-02-08 00:00:00', 3),
(20, 'Isha Agarwal', 'isha.agarwal@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9000000009', true, '2024-02-09 00:00:00', '2024-02-09 00:00:00', 3),
(21, 'Jatin Das', 'jatin.das@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9000000010', true, '2024-02-10 00:00:00', '2024-02-10 00:00:00', 3),
(22, 'Kriti Sen', 'kriti.sen@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9000000011', true, '2024-02-11 00:00:00', '2024-02-11 00:00:00', 3),
(23, 'Lokesh Yadav', 'lokesh.yadav@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9000000012', true, '2024-02-12 00:00:00', '2024-02-12 00:00:00', 3),
(24, 'Meera Choudhury', 'meera.choudhury@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9000000013', true, '2024-02-13 00:00:00', '2024-02-13 00:00:00', 3),
(25, 'Nitin Bajaj', 'nitin.bajaj@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9000000014', true, '2024-02-14 00:00:00', '2024-02-14 00:00:00', 3),
(26, 'Pooja Iyer', 'pooja.iyer@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9000000015', true, '2024-02-15 00:00:00', '2024-02-15 00:00:00', 3),
(27, 'Ravi Teja', 'ravi.teja@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9000000016', true, '2024-02-16 00:00:00', '2024-02-16 00:00:00', 3),
(28, 'Sanya Malhotra', 'sanya.malhotra@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9000000017', true, '2024-02-17 00:00:00', '2024-02-17 00:00:00', 3),
(29, 'Tanmay Bhat', 'tanmay.bhat@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9000000018', true, '2024-02-18 00:00:00', '2024-02-18 00:00:00', 3),
(30, 'Urmila Deshmukh', 'urmila.deshmukh@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9000000019', true, '2024-02-19 00:00:00', '2024-02-19 00:00:00', 3),
(31, 'Varun Gandhi', 'varun.gandhi@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9000000020', true, '2024-02-20 00:00:00', '2024-02-20 00:00:00', 3),
(32, 'Yash Thakur', 'yash.thakur@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9000000021', true, '2024-02-21 00:00:00', '2024-02-21 00:00:00', 3),
(33, 'Zara Sheikh', 'zara.sheikh@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9000000022', true, '2024-02-22 00:00:00', '2024-02-22 00:00:00', 3),
(34, 'Aditya Narayan', 'aditya.narayan@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9000000023', true, '2024-02-23 00:00:00', '2024-02-23 00:00:00', 3),
(35, 'Bhavika Shah', 'bhavika.shah@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9000000024', true, '2024-02-24 00:00:00', '2024-02-24 00:00:00', 3),
(36, 'Chetan Bhagat', 'chetan.bhagat@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+91-9000000025', true, '2024-02-25 00:00:00', '2024-02-25 00:00:00', 3);

ALTER SEQUENCE users_id_seq RESTART WITH 37;


(5, 'Data Scientist', 'Apply machine learning and statistical modeling to solve complex business problems. Experience with NLP and computer vision preferred.', 'Tata Consultancy Services', 'Chennai, Tamil Nadu', 'FULL_TIME', 'SENIOR', 2000000, 2800000, 'Python, TensorFlow, PyTorch, SQL, ML, NLP', '2025-03-25', '2025-01-25 14:00:00', true, 2),

-- Infosys (employer_id=3) - 5 jobs
(6, 'React Native Developer', 'Develop cross-platform mobile applications for banking and finance clients. Must have published apps on Play Store/App Store.', 'Infosys', 'Bangalore, Karnataka', 'FULL_TIME', 'MID', 1000000, 1600000, 'React Native, JavaScript, Redux, Firebase, REST APIs', '2025-03-18', '2025-01-16 09:30:00', true, 3),
(7, 'Business Analyst', 'Bridge the gap between business stakeholders and technical teams. Gather requirements and create detailed BRDs and FRDs.', 'Infosys', 'Pune, Maharashtra', 'FULL_TIME', 'MID', 800000, 1200000, 'Agile, JIRA, SQL, Excel, UML, Communication', '2025-03-22', '2025-01-19 10:00:00', true, 3),
(8, 'Python Developer Intern', 'Great internship opportunity for final year students. Work on real-world AI/ML projects under expert mentorship.', 'Infosys', 'Mysore, Karnataka', 'INTERNSHIP', 'ENTRY', 240000, 360000, 'Python, Django, SQL, Machine Learning, Git', '2025-03-30', '2025-01-23 11:30:00', true, 3),
(9, 'Cybersecurity Analyst', 'Protect enterprise systems from cyber threats. Conduct penetration testing and security audits.', 'Infosys', 'Trivandrum, Kerala', 'FULL_TIME', 'JUNIOR', 700000, 1100000, 'Network Security, Kali Linux, OWASP, SIEM, Python', '2025-04-05', '2025-01-26 13:00:00', true, 3),
(10, 'SAP Consultant', 'Implement and support SAP S/4HANA modules for manufacturing clients. Travel to client sites required.', 'Infosys', 'Hyderabad, Telangana', 'FULL_TIME', 'SENIOR', 1500000, 2200000, 'SAP S/4HANA, ABAP, FI/CO, MM, SD, SAP Fiori', '2025-03-28', '2025-01-28 15:30:00', true, 3),

-- Wipro (employer_id=4) - 5 jobs
(11, 'Full Stack Developer', 'End-to-end development using Angular and Node.js. Work on greenfield projects for US-based clients.', 'Wipro', 'Bangalore, Karnataka', 'FULL_TIME', 'MID', 900000, 1500000, 'Angular, Node.js, MongoDB, Express, TypeScript, CSS', '2025-03-15', '2025-01-17 09:00:00', true, 4),
(12, 'UI/UX Designer', 'Design intuitive user interfaces for web and mobile applications. Create wireframes, prototypes, and design systems.', 'Wipro', 'Chennai, Tamil Nadu', 'FULL_TIME', 'JUNIOR', 600000, 1000000, 'Figma, Adobe XD, Sketch, Prototyping, User Research', '2025-03-25', '2025-01-20 10:30:00', true, 4),
(13, 'Database Administrator', 'Manage and optimize PostgreSQL and Oracle databases. Ensure high availability and disaster recovery.', 'Wipro', 'Pune, Maharashtra', 'FULL_TIME', 'SENIOR', 1600000, 2400000, 'PostgreSQL, Oracle, MongoDB, Performance Tuning, PL/SQL', '2025-04-10', '2025-01-22 11:00:00', true, 4),
(14, 'Technical Support Engineer', 'Provide L2/L3 support for enterprise applications. Work in rotational shifts including night shift.', 'Wipro', 'Kolkata, West Bengal', 'FULL_TIME', 'JUNIOR', 350000, 600000, 'Linux, SQL, Networking, Troubleshooting, Communication', '2025-03-20', '2025-01-24 12:00:00', true, 4),
(15, 'Blockchain Developer', 'Build decentralized applications on Ethereum and Hyperledger. Smart contract development experience required.', 'Wipro', 'Bangalore, Karnataka', 'CONTRACT', 'MID', 1400000, 2000000, 'Solidity, Ethereum, Hyperledger, Web3.js, Node.js', '2025-04-15', '2025-01-27 14:30:00', true, 4),

-- HCL (employer_id=5) - 5 jobs
(16, 'QA Automation Engineer', 'Design and implement automated test frameworks. Experience with Selenium and Cypress required.', 'HCL Technologies', 'Noida, Uttar Pradesh', 'FULL_TIME', 'MID', 800000, 1300000, 'Selenium, Cypress, Java, TestNG, CI/CD, API Testing', '2025-03-18', '2025-01-16 08:00:00', true, 5),
(17, 'Salesforce Developer', 'Develop and customize Salesforce solutions. Must have Salesforce certification.', 'HCL Technologies', 'Lucknow, Uttar Pradesh', 'FULL_TIME', 'MID', 1000000, 1600000, 'Salesforce, Apex, Visualforce, LWC, SOQL', '2025-04-05', '2025-01-19 09:30:00', true, 5),
(18, 'Network Engineer', 'Design and maintain enterprise network infrastructure. CCNP certification preferred.', 'HCL Technologies', 'Gurgaon, Haryana', 'FULL_TIME', 'JUNIOR', 500000, 900000, 'Cisco, Routing, Switching, Firewalls, VPN, TCP/IP', '2025-03-12', '2025-01-21 10:00:00', true, 5),
(19, 'AI Research Scientist', 'Conduct cutting-edge research in natural language processing. PhD or MTech in AI/ML preferred.', 'HCL Technologies', 'Bangalore, Karnataka', 'FULL_TIME', 'LEAD', 3000000, 4000000, 'NLP, Deep Learning, Transformers, BERT, Python, Research', '2025-04-20', '2025-01-24 11:30:00', true, 5),
(20, 'Project Manager', 'Manage delivery of large-scale IT projects. PMP certification and 10+ years experience required.', 'HCL Technologies', 'Chennai, Tamil Nadu', 'FULL_TIME', 'EXECUTIVE', 2200000, 3200000, 'Project Management, Agile, Scrum, Budgeting, Risk Mgmt', '2025-03-30', '2025-01-27 13:00:00', true, 5),

-- Tech Mahindra (employer_id=6) - 5 jobs
(21, '5G Network Engineer', 'Work on cutting-edge 5G network deployment and optimization projects for telecom clients.', 'Tech Mahindra', 'Delhi, Delhi', 'FULL_TIME', 'MID', 1100000, 1700000, '5G, LTE, Network Optimization, Python, RF Planning', '2025-03-22', '2025-01-18 09:00:00', true, 6),
(22, 'IoT Solutions Architect', 'Design end-to-end IoT solutions for smart manufacturing and smart city projects.', 'Tech Mahindra', 'Pune, Maharashtra', 'FULL_TIME', 'SENIOR', 1800000, 2600000, 'IoT, MQTT, AWS IoT, Embedded Systems, Node.js', '2025-04-08', '2025-01-20 10:30:00', true, 6),
(23, 'ServiceNow Developer', 'Implement and customize ServiceNow platform for IT Service Management.', 'Tech Mahindra', 'Hyderabad, Telangana', 'FULL_TIME', 'JUNIOR', 600000, 1000000, 'ServiceNow, JavaScript, REST API, ITIL, CMDB', '2025-03-28', '2025-01-23 11:00:00', true, 6),
(24, 'Data Engineer', 'Build and maintain data pipelines for big data analytics platforms.', 'Tech Mahindra', 'Bangalore, Karnataka', 'FULL_TIME', 'MID', 1200000, 1800000, 'Spark, Hadoop, Python, Airflow, Kafka, SQL', '2025-04-12', '2025-01-26 14:00:00', true, 6),
(25, 'Content Writer', 'Create technical documentation, blog posts, and marketing content for IT services.', 'Tech Mahindra', 'Remote, India', 'PART_TIME', 'ENTRY', 300000, 500000, 'Technical Writing, SEO, Content Strategy, Research', '2025-03-15', '2025-01-29 15:00:00', true, 6),

-- L&T (employer_id=7) - 5 jobs
(26, 'Structural Engineer', 'Design and analyze steel and concrete structures for industrial projects using STAAD Pro.', 'Larsen & Toubro', 'Mumbai, Maharashtra', 'FULL_TIME', 'MID', 900000, 1500000, 'STAAD Pro, AutoCAD, ETABS, Structural Analysis, IS Codes', '2025-03-20', '2025-01-17 09:00:00', true, 7),
(27, 'Mechanical Design Engineer', 'Create 3D models and drawings for heavy machinery using SolidWorks and CATIA.', 'Larsen & Toubro', 'Surat, Gujarat', 'FULL_TIME', 'MID', 700000, 1200000, 'SolidWorks, CATIA, AutoCAD, FEA, GD&T', '2025-04-02', '2025-01-19 10:00:00', true, 7),
(28, 'Site Supervisor', 'Supervise construction activities at project sites. Ensure quality and safety compliance.', 'Larsen & Toubro', 'Ahmedabad, Gujarat', 'CONTRACT', 'JUNIOR', 400000, 700000, 'Construction Management, Safety, Quality Control, Planning', '2025-03-25', '2025-01-22 11:30:00', true, 7),
(29, 'Electrical Engineer', 'Design electrical systems for industrial and commercial buildings. Knowledge of PLC/SCADA preferred.', 'Larsen & Toubro', 'Chennai, Tamil Nadu', 'FULL_TIME', 'JUNIOR', 500000, 900000, 'PLC, SCADA, AutoCAD Electrical, Switchgear, Power Systems', '2025-04-10', '2025-01-25 13:00:00', true, 7),
(30, 'HR Business Partner', 'Manage HR operations for engineering project teams. Handle recruitment, performance mgmt, and employee relations.', 'Larsen & Toubro', 'Mumbai, Maharashtra', 'FULL_TIME', 'MID', 800000, 1300000, 'HR Operations, Recruitment, Performance Mgmt, Labor Laws', '2025-03-30', '2025-01-28 14:30:00', true, 7),

-- Flipkart (employer_id=8) - 5 jobs
(31, 'Product Manager', 'Own product roadmap for e-commerce platform features. Drive product strategy with data-driven decisions.', 'Flipkart', 'Bangalore, Karnataka', 'FULL_TIME', 'SENIOR', 2500000, 3500000, 'Product Strategy, Analytics, A/B Testing, Agile, SQL', '2025-03-15', '2025-01-15 10:00:00', true, 8),
(32, 'Android Developer', 'Build and maintain the Flipkart Android app with 100M+ downloads. Kotlin-first development.', 'Flipkart', 'Bangalore, Karnataka', 'FULL_TIME', 'MID', 1400000, 2200000, 'Kotlin, Android SDK, Jetpack, MVVM, RxJava', '2025-03-28', '2025-01-18 11:00:00', true, 8),
(33, 'Supply Chain Analyst', 'Optimize supply chain operations using data analytics and machine learning.', 'Flipkart', 'Delhi, Delhi', 'FULL_TIME', 'JUNIOR', 600000, 1000000, 'Python, SQL, Supply Chain, Analytics, Excel', '2025-04-05', '2025-01-21 09:30:00', true, 8),
(34, 'Backend Engineer II', 'Build scalable microservices for Flipkart''s core commerce platform. Handle 100K+ requests per second.', 'Flipkart', 'Bangalore, Karnataka', 'FULL_TIME', 'SENIOR', 2000000, 3000000, 'Java, Spring Boot, Kafka, Redis, Cassandra, NoSQL', '2025-03-20', '2025-01-24 10:30:00', true, 8),
(35, 'Data Analyst', 'Analyze user behavior data to drive business decisions. Create dashboards and reports.', 'Flipkart', 'Bangalore, Karnataka', 'FULL_TIME', 'ENTRY', 500000, 800000, 'SQL, Python, Tableau, Excel, Statistics, Communication', '2025-04-15', '2025-01-27 12:00:00', true, 8),

-- Zomato (employer_id=9) - 5 jobs
(36, 'iOS Developer', 'Build the next-gen Zomato iOS app. Must have experience with SwiftUI and UIKit.', 'Zomato', 'Gurgaon, Haryana', 'FULL_TIME', 'MID', 1200000, 1800000, 'Swift, SwiftUI, UIKit, CoreData, REST APIs', '2025-03-18', '2025-01-16 09:00:00', true, 9),
(37, 'Data Platform Engineer', 'Build real-time data pipelines for food delivery analytics. Handle 10M+ orders daily.', 'Zomato', 'Gurgaon, Haryana', 'FULL_TIME', 'SENIOR', 1800000, 2600000, 'Spark, Flink, Kafka, Hadoop, Scala, Python', '2025-04-02', '2025-01-19 10:00:00', true, 9),
(38, 'Operations Manager', 'Manage restaurant onboarding and partnership operations. Coordinate with cross-functional teams.', 'Zomato', 'Mumbai, Maharashtra', 'FULL_TIME', 'MID', 800000, 1300000, 'Operations, Vendor Management, Negotiation, Excel', '2025-03-22', '2025-01-22 11:30:00', true, 9),
(39, 'Machine Learning Engineer', 'Build recommendation systems for personalized food discovery. Work with 50M+ users.', 'Zomato', 'Gurgaon, Haryana', 'FULL_TIME', 'SENIOR', 2200000, 3200000, 'Python, TensorFlow, Recommender Systems, SQL, ML', '2025-04-10', '2025-01-25 13:00:00', true, 9),
(40, 'Graphic Designer', 'Design marketing collateral, social media graphics, and branding materials for Zomato.', 'Zomato', 'Gurgaon, Haryana', 'FULL_TIME', 'JUNIOR', 400000, 700000, 'Photoshop, Illustrator, Canva, Typography, Branding', '2025-03-30', '2025-01-28 14:00:00', true, 9),

-- Swiggy (employer_id=10) - 5 jobs
(41, 'Software Development Engineer II', 'Build and scale Swiggy''s food delivery platform. Work on high-impact features for 80M+ users.', 'Swiggy', 'Bangalore, Karnataka', 'FULL_TIME', 'MID', 1500000, 2400000, 'Java, Spring, Hibernate, MySQL, Redis, Kafka', '2025-03-25', '2025-01-17 10:00:00', true, 10),
(42, 'Delivery Experience PM', 'Improve delivery experience through route optimization and real-time tracking features.', 'Swiggy', 'Bangalore, Karnataka', 'FULL_TIME', 'SENIOR', 2000000, 3000000, 'Product Management, Analytics, User Research, A/B Testing', '2025-04-08', '2025-01-20 11:00:00', true, 10),
(43, 'Site Reliability Engineer', 'Ensure 99.99% uptime for Swiggy''s core platform. Incident response and automation focus.', 'Swiggy', 'Bangalore, Karnataka', 'FULL_TIME', 'MID', 1600000, 2400000, 'AWS, Terraform, Prometheus, Grafana, Go, Incident Mgmt', '2025-03-18', '2025-01-23 09:30:00', true, 10),
(44, 'Customer Support Lead', 'Lead a team of 20+ support agents. Implement AI-powered chatbot solutions for customer queries.', 'Swiggy', 'Hyderabad, Telangana', 'FULL_TIME', 'MID', 700000, 1100000, 'Team Management, CRM, Zendesk, Analytics, Communication', '2025-04-12', '2025-01-26 10:30:00', true, 10),
(45, 'Finance Analyst', 'Manage financial planning and analysis for Swiggy''s cloud kitchen business unit.', 'Swiggy', 'Bangalore, Karnataka', 'FULL_TIME', 'JUNIOR', 500000, 900000, 'Financial Modeling, Excel, SQL, Budgeting, Forecasting', '2025-03-28', '2025-01-29 12:00:00', true, 10),

-- Paytm (employer_id=11) - 5 jobs
(46, 'Payment Gateway Developer', 'Build and maintain Paytm''s payment gateway processing 1B+ transactions monthly.', 'Paytm', 'Noida, Uttar Pradesh', 'FULL_TIME', 'SENIOR', 1800000, 2800000, 'Java, Spring Boot, Payment Gateways, Security, SQL', '2025-03-20', '2025-01-16 10:30:00', true, 11),
(47, 'Flutter Developer', 'Develop cross-platform fintech apps using Flutter. Experience with financial dashboards preferred.', 'Paytm', 'Noida, Uttar Pradesh', 'FULL_TIME', 'MID', 1000000, 1600000, 'Flutter, Dart, Firebase, REST APIs, Git', '2025-04-01', '2025-01-19 11:00:00', true, 11),
(48, 'Risk Analyst', 'Identify and mitigate financial fraud risks using machine learning models.', 'Paytm', 'Noida, Uttar Pradesh', 'FULL_TIME', 'MID', 900000, 1500000, 'Python, SQL, ML, Fraud Detection, Statistics', '2025-03-22', '2025-01-22 09:00:00', true, 11),
(49, 'Compliance Officer', 'Ensure regulatory compliance for payments banking platform. Handle RBI and IRDAI requirements.', 'Paytm', 'Mumbai, Maharashtra', 'FULL_TIME', 'SENIOR', 1500000, 2200000, 'RBI Compliance, KYC, AML, Risk Management, Legal', '2025-04-15', '2025-01-25 10:00:00', true, 11),
(50, 'UX Writer', 'Write clear, user-friendly copy for Paytm app interfaces. Create microcopy and error messages.', 'Paytm', 'Noida, Uttar Pradesh', 'FULL_TIME', 'JUNIOR', 500000, 800000, 'UX Writing, Content Design, Information Architecture', '2025-03-30', '2025-01-28 11:30:00', true, 11);

ALTER SEQUENCE jobs_id_seq RESTART WITH 51;

-- =============================================
-- 4. JOB APPLICATIONS (40 applications)
-- Multiple job seekers applying to various jobs
-- =============================================
INSERT INTO job_applications (id, applied_at, status, cover_letter, job_id, job_seeker_id) VALUES

-- Job 1 (Senior Java Developer) - 4 applicants
(1, '2025-01-20 10:30:00', 'SHORTLISTED', 'I have 8 years of experience in Java development with a strong focus on Spring Boot microservices. I led a team of 5 developers at my previous company and successfully migrated legacy monoliths to cloud-native architectures.', 1, 12),
(2, '2025-01-21 14:00:00', 'PENDING', 'As a Senior Java Developer with 6 years of experience at Amazon, I have deep expertise in building scalable microservices handling 50K+ TPS. Eager to bring my experience to TCS.', 1, 14),
(3, '2025-01-22 09:15:00', 'REVIEWING', 'Experienced Java developer with expertise in Spring Boot and cloud technologies. Worked on multiple enterprise projects for Fortune 500 clients.', 1, 17),
(4, '2025-01-25 11:00:00', 'PENDING', 'I have strong experience with Java 17, Spring Boot 3, and Kubernetes. Led migration of 20+ microservices to AWS EKS.', 1, 20),

-- Job 3 (Junior Frontend Developer) - 3 applicants
(5, '2025-01-25 16:00:00', 'PENDING', 'Recent B.Tech graduate from IIT Bombay with strong passion for frontend development. Built several React projects during college.', 3, 16),
(6, '2025-01-26 10:00:00', 'REVIEWING', 'Completed a 6-month internship in React development. Proficient in JavaScript, TypeScript, and modern CSS frameworks.', 3, 19),
(7, '2025-01-28 09:30:00', 'PENDING', 'Self-taught developer with 2 years of freelance experience in React development. Looking for my first full-time role.', 3, 22),

-- Job 6 (React Native) - 3 applicants
(8, '2025-01-20 11:00:00', 'HIRED', 'Built 3 cross-platform apps using React Native with 100K+ downloads combined. Strong understanding of mobile performance optimization.', 6, 13),
(9, '2025-01-22 15:30:00', 'REJECTED', '2 years experience in React Native development. Published 2 apps on Play Store.', 6, 18),
(10, '2025-01-25 08:45:00', 'PENDING', 'Mobile developer with expertise in React Native and Flutter. Previously worked at a fintech startup building payment apps.', 6, 25),

-- Job 11 (Full Stack Developer Wipro) - 3 applicants
(11, '2025-01-22 10:00:00', 'REVIEWING', 'Full stack developer with 4 years experience in Angular and Node.js. Built 5 enterprise applications from scratch.', 11, 15),
(12, '2025-01-24 14:00:00', 'PENDING', 'MEAN stack developer with experience in building scalable web applications. Strong problem-solving skills.', 11, 21),
(13, '2025-01-27 11:30:00', 'PENDING', 'Full stack developer passionate about building clean, testable code. Experience with Angular 15+ and Node.js.', 11, 28),

-- Job 16 (QA Automation) - 3 applicants
(14, '2025-01-20 09:00:00', 'SHORTLISTED', 'QA automation expert with 5 years experience in Selenium and Cypress. Reduced regression test time by 80% at my previous company.', 16, 24),
(15, '2025-01-23 12:00:00', 'PENDING', 'ISTQB certified tester with strong automation skills. Experience with CI/CD integration for automated test suites.', 16, 27),
(16, '2025-01-26 15:30:00', 'REJECTED', 'Manual tester looking to transition into automation. Basic knowledge of Selenium.', 16, 30),

-- Job 21 (5G Network Engineer) - 3 applicants
(17, '2025-01-22 10:30:00', 'PENDING', 'Telecom engineer with 4 years experience in LTE/5G network deployment. Worked with Airtel and Jio on pan-India rollout.', 21, 26),
(18, '2025-01-25 14:00:00', 'REVIEWING', 'RF engineer with expertise in 5G NR planning and optimization. Hands-on experience with ATOLL and Planet tools.', 21, 29),
(19, '2025-01-28 09:00:00', 'PENDING', 'Recent MTech in Wireless Communications. Thesis on 5G network slicing optimization.', 21, 32),

-- Job 26 (Structural Engineer L&T) - 3 applicants
(20, '2025-01-20 11:30:00', 'SHORTLISTED', 'Structural engineer with 6 years experience in industrial projects. Designed steel structures for 3 major power plants.', 26, 23),
(21, '2025-01-24 10:00:00', 'PENDING', 'Experience with STAAD Pro and ETABS for design of commercial buildings. Knowledge of Indian and International codes.', 26, 31),
(22, '2025-01-27 14:30:00', 'PENDING', 'Civil engineer with project experience in bridge and flyover design using MIDAS Civil.', 26, 34),

-- Job 31 (Product Manager Flipkart) - 3 applicants
(23, '2025-01-18 10:00:00', 'HIRED', 'Product manager with 7 years experience in e-commerce. Led search and discovery features resulting in 25% conversion improvement.', 31, 12),
(24, '2025-01-20 15:00:00', 'PENDING', 'Senior PM from Amazon Pay. Expertise in payment products and growth strategies. MBA from ISB.', 31, 15),
(25, '2025-01-23 11:00:00', 'REVIEWING', 'Product leader with experience scaling B2C platforms from 1M to 50M users. Strong data analytics background.', 31, 18),

-- Job 36 (iOS Developer Zomato) - 3 applicants
(26, '2025-01-20 09:30:00', 'PENDING', 'iOS developer with 4 years experience in Swift. Published 6 apps with total 500K+ downloads.', 36, 14),
(27, '2025-01-23 13:00:00', 'REVIEWING', 'Senior iOS developer experienced in building food delivery apps. Strong SwiftUI and UIKit skills.', 36, 17),
(28, '2025-01-26 10:30:00', 'PENDING', 'iOS developer passionate about clean architecture and smooth animations. Contributed to open source Swift packages.', 36, 20),

-- Job 41 (SDE II Swiggy) - 4 applicants
(29, '2025-01-20 11:00:00', 'PENDING', 'Backend engineer with 5 years experience in Java and Spring Boot. Built systems handling 10K+ TPS at Ola.', 41, 13),
(30, '2025-01-22 14:00:00', 'SHORTLISTED', 'SDE II at Uber with expertise in distributed systems. Led migration of legacy services to microservices architecture.', 41, 16),
(31, '2025-01-25 09:00:00', 'REVIEWING', 'Full stack developer with strong system design skills. Experience with high-traffic e-commerce platforms.', 41, 19),
(32, '2025-01-28 16:00:00', 'PENDING', 'Java developer with focus on performance optimization. Reduced API latency by 60% at my current role.', 41, 22),

-- Job 46 (Payment Gateway Developer Paytm) - 4 applicants
(33, '2025-01-20 10:00:00', 'PENDING', 'Payment gateway specialist with 6 years experience. Built UPI and wallet solutions processing 500M+ transactions.', 46, 21),
(34, '2025-01-23 15:30:00', 'REVIEWING', 'Senior backend engineer with deep expertise in secure transaction processing and PCI DSS compliance.', 46, 24),
(35, '2025-01-26 11:00:00', 'PENDING', 'Fintech developer experienced with Razorpay and Stripe APIs. Strong understanding of payment workflows.', 46, 27),
(36, '2025-01-29 09:30:00', 'PENDING', 'Java developer with experience building scalable financial systems. Knowledge of ISO 8583 messaging standard.', 46, 30),

-- Job 2 (Cloud Architect TCS) - 2 applicants
(37, '2025-01-22 10:00:00', 'PENDING', 'Cloud architect with AWS Solutions Architect certification. Led 10+ cloud migration projects.', 2, 25),
(38, '2025-01-25 14:30:00', 'PENDING', 'Multi-cloud architect with experience in AWS, Azure, and GCP. Designed hybrid cloud solutions for banking clients.', 2, 28),

-- Job 8 (Python Intern Infosys) - 2 applicants
(39, '2025-01-28 10:00:00', 'PENDING', 'Final year B.Tech student from VIT Vellore. Completed 3 ML projects using Python and TensorFlow.', 8, 33),
(40, '2025-01-29 15:00:00', 'PENDING', 'Computer Science student with internship experience at a startup. Built REST APIs using Django.', 8, 36);

ALTER SEQUENCE job_applications_id_seq RESTART WITH 41;

-- =============================================
-- 5. SAVED JOBS (30 saved jobs)
-- =============================================
INSERT INTO saved_jobs (id, saved_at, user_id, job_id) VALUES
(1, '2025-01-22 10:00:00', 12, 2),
(2, '2025-01-22 10:30:00', 12, 11),
(3, '2025-01-23 14:00:00', 12, 34),
(4, '2025-01-20 11:00:00', 13, 1),
(5, '2025-01-21 09:00:00', 13, 21),
(6, '2025-01-25 16:30:00', 13, 41),
(7, '2025-01-22 10:00:00', 14, 6),
(8, '2025-01-24 11:30:00', 14, 32),
(9, '2025-01-26 15:00:00', 14, 46),
(10, '2025-01-20 10:30:00', 15, 11),
(11, '2025-01-22 14:00:00', 15, 31),
(12, '2025-01-27 09:00:00', 15, 41),
(13, '2025-01-25 16:00:00', 16, 3),
(14, '2025-01-26 10:00:00', 16, 11),
(15, '2025-01-28 11:00:00', 16, 35),
(16, '2025-01-22 10:30:00', 17, 1),
(17, '2025-01-24 13:00:00', 17, 36),
(18, '2025-01-27 15:30:00', 17, 39),
(19, '2025-01-23 12:00:00', 18, 6),
(20, '2025-01-25 09:00:00', 18, 16),
(21, '2025-01-28 14:00:00', 18, 31),
(22, '2025-01-24 10:00:00', 19, 3),
(23, '2025-01-26 11:30:00', 19, 11),
(24, '2025-01-29 08:00:00', 19, 25),
(25, '2025-01-25 14:00:00', 20, 1),
(26, '2025-01-27 10:30:00', 20, 36),
(27, '2025-01-29 12:00:00', 20, 47),
(28, '2025-01-26 09:00:00', 21, 11),
(29, '2025-01-28 15:00:00', 21, 34),
(30, '2025-01-29 11:00:00', 21, 46);

ALTER SEQUENCE saved_jobs_id_seq RESTART WITH 31;

-- =============================================
-- 6. RESUMES (for 15 job seekers who have uploaded)
-- file_path references the uploads directory
-- =============================================
INSERT INTO resumes (id, file_name, file_path, file_type, uploaded_at, user_id) VALUES
(1, 'Arun_Kumar_Resume_2025.pdf', 'uploads/resumes/arun_kumar_resume.pdf', 'application/pdf', '2025-01-20 10:00:00', 12),
(2, 'Bhavna_Singh_Resume.pdf', 'uploads/resumes/bhavna_singh_resume.pdf', 'application/pdf', '2025-01-19 14:30:00', 13),
(3, 'Chirag_Shah_Resume.pdf', 'uploads/resumes/chirag_shah_resume.pdf', 'application/pdf', '2025-01-21 09:00:00', 14),
(4, 'Divya_Nair_Resume_2025.pdf', 'uploads/resumes/divya_nair_resume.pdf', 'application/pdf', '2025-01-18 11:00:00', 15),
(5, 'Esha_Mehta_Resume.pdf', 'uploads/resumes/esha_mehta_resume.pdf', 'application/pdf', '2025-01-22 16:00:00', 16),
(6, 'Farhan_Khan_Resume.pdf', 'uploads/resumes/farhan_khan_resume.pdf', 'application/pdf', '2025-01-20 15:30:00', 17),
(7, 'Gauri_Joshi_Resume.pdf', 'uploads/resumes/gauri_joshi_resume.pdf', 'application/pdf', '2025-01-23 10:00:00', 18),
(8, 'Harsh_Vardhan_Resume.pdf', 'uploads/resumes/harsh_vardhan_resume.pdf', 'application/pdf', '2025-01-21 12:00:00', 19),
Now I understand the full schema. Let me create the demo data SQL file first.

<execute_command>
<command>mkdir database</command>
</execute_command>
