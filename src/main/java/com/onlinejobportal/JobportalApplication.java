package com.onlinejobportal;

import com.onlinejobportal.entity.Job;
import com.onlinejobportal.entity.Role;
import com.onlinejobportal.entity.User;
import com.onlinejobportal.repository.JobRepository;
import com.onlinejobportal.repository.RoleRepository;
import com.onlinejobportal.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@SpringBootApplication
@Slf4j
public class JobportalApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobportalApplication.class, args);
    }

    @Bean
    @Order(1)
    public CommandLineRunner seedRoles(RoleRepository roleRepository) {
        return args -> {
            String[] roleNames = {"ROLE_ADMIN", "ROLE_EMPLOYER", "ROLE_JOBSEEKER"};
            for (String roleName : roleNames) {
                if (!roleRepository.existsByName(roleName)) {
                    Role role = Role.builder()
                            .name(roleName)
                            .build();
                    roleRepository.save(role);
                    log.info("Seeded role: {}", roleName);
                } else {
                    log.debug("Role already exists: {}", roleName);
                }
            }
            log.info("Role seeding completed successfully.");
        };
    }

    @Bean
    @Order(2)
    public CommandLineRunner seedJobs(JobRepository jobRepository,
                                       UserRepository userRepository,
                                       RoleRepository roleRepository,
                                       PasswordEncoder passwordEncoder) {
        return args -> {
            log.info("Checking for jobs to seed...");

            // Ensure a default employer exists for job seeding
            Role employerRole = roleRepository.findByName("ROLE_EMPLOYER")
                    .orElseThrow(() -> new IllegalStateException("ROLE_EMPLOYER not found. Role seeding must run first."));

            User defaultEmployer = userRepository.findByEmail("placements@jobportal.com")
                    .orElseGet(() -> {
                        User employer = User.builder()
                                .fullName("Placements Team")
                                .email("placements@jobportal.com")
                                .password(passwordEncoder.encode("password123"))
                                .phone("+91-9876543210")
                                .enabled(true)
                                .role(employerRole)
                                .build();
                        User saved = userRepository.save(employer);
                        log.info("Created default employer user: placements@jobportal.com");
                        return saved;
                    });

            seedJobsInternal(jobRepository, defaultEmployer);
            log.info("Job seeding completed successfully. 20 jobs inserted.");
        };
    }

    @Transactional
    protected void seedJobsInternal(JobRepository jobRepository, User employer) {
        LocalDate baseDeadline = LocalDate.now().plusDays(45);

        Object[][] jobsData = {
                // {title, company, location, description, employmentType, experienceLevel, salaryMin, salaryMax, skills, deadlineDays}
                {"Senior Java Developer", "Tata Consultancy Services (TCS)", "Mumbai, Maharashtra",
                        "Design and develop scalable enterprise applications using Java 17 and Spring Boot 3. Lead a team of 5 developers, conduct code reviews, and drive microservices architecture adoption across the organization.",
                        "FULL_TIME", "SENIOR", 1200000.0, 1800000.0,
                        "Java, Spring Boot, Microservices, Hibernate, Kafka, Docker, Kubernetes, AWS", 45},

                {"Cloud Architect", "Infosys", "Bangalore, Karnataka",
                        "Design and implement multi-cloud strategies for enterprise clients. Evaluate cloud readiness, create migration roadmaps, and establish governance frameworks for AWS, Azure, and GCP environments.",
                        "FULL_TIME", "LEAD", 2000000.0, 3000000.0,
                        "AWS, Azure, GCP, Terraform, Cloud Migration, Kubernetes, Docker, CI/CD", 50},

                {"Junior Frontend Developer", "Wipro", "Pune, Maharashtra",
                        "Build responsive and accessible web interfaces using React 18 and TypeScript. Collaborate with UX designers to implement pixel-perfect designs and integrate with RESTful APIs.",
                        "FULL_TIME", "JUNIOR", 400000.0, 700000.0,
                        "React, TypeScript, JavaScript, CSS3, HTML5, REST APIs, Git", 30},

                {"DevSecOps Engineer", "Accenture", "Hyderabad, Telangana",
                        "Integrate security practices into CI/CD pipelines. Automate security scanning, implement infrastructure-as-code security controls, and conduct security architecture reviews.",
                        "FULL_TIME", "MID", 1400000.0, 2200000.0,
                        "Docker, Kubernetes, Jenkins, Terraform, Python, SAST, DAST, AWS Security", 40},

                {"Data Scientist", "Cognizant", "Chennai, Tamil Nadu",
                        "Apply machine learning and deep learning techniques to solve complex business problems. Build predictive models, recommendation systems, and NLP solutions for global clients.",
                        "FULL_TIME", "MID", 1000000.0, 1600000.0,
                        "Python, TensorFlow, PyTorch, SQL, NLP, Machine Learning, Statistics", 35},

                {"React Native Developer", "HCLTech", "Noida, Uttar Pradesh",
                        "Develop cross-platform mobile applications for enterprise clients. Build reusable components, optimize performance, and ensure smooth integration with backend services.",
                        "FULL_TIME", "MID", 600000.0, 1000000.0,
                        "React Native, JavaScript, Redux, Firebase, REST APIs, Android, iOS", 35},

                {"AI/ML Engineer", "IBM", "Bangalore, Karnataka",
                        "Research and implement cutting-edge AI/ML solutions for enterprise clients. Work with Watson AI platform, build custom ML models, and deploy them at scale in production environments.",
                        "FULL_TIME", "SENIOR", 1800000.0, 2800000.0,
                        "Python, Machine Learning, Deep Learning, IBM Watson, Docker, Kubernetes, SQL", 45},

                {"Cybersecurity Analyst", "Deloitte", "Gurgaon, Haryana",
                        "Perform security assessments, penetration testing, and vulnerability management for Fortune 500 clients. Develop security policies and incident response procedures.",
                        "FULL_TIME", "JUNIOR", 800000.0, 1400000.0,
                        "Network Security, Kali Linux, OWASP, SIEM, Python, Incident Response", 30},

                {"Python Developer", "Amazon", "Bangalore, Karnataka",
                        "Build and maintain high-traffic backend services powering Amazon's e-commerce platform. Design scalable APIs, optimize database queries, and implement robust payment processing systems.",
                        "FULL_TIME", "MID", 1600000.0, 2600000.0,
                        "Python, Django, AWS, PostgreSQL, Redis, Microservices, REST APIs", 40},

                {"Software Engineer", "Microsoft", "Hyderabad, Telangana",
                        "Develop next-generation cloud services for Microsoft Azure. Work on distributed systems handling millions of requests per second with high availability and low latency requirements.",
                        "FULL_TIME", "SENIOR", 2200000.0, 3500000.0,
                        "C#, .NET Core, Azure, Distributed Systems, Kubernetes, SQL, NoSQL", 50},

                {"Site Reliability Engineer", "Google", "Bangalore, Karnataka",
                        "Ensure 99.99% availability of Google's production services. Build monitoring systems, automate incident response, and drive reliability improvements across the infrastructure.",
                        "FULL_TIME", "SENIOR", 2800000.0, 4500000.0,
                        "Go, Python, Kubernetes, Prometheus, Grafana, Distributed Systems, Linux", 45},

                {"Database Administrator", "Oracle", "Bangalore, Karnataka",
                        "Manage and optimize enterprise-scale Oracle and PostgreSQL databases. Implement backup strategies, performance tuning, and disaster recovery solutions for mission-critical systems.",
                        "FULL_TIME", "MID", 1000000.0, 1600000.0,
                        "Oracle, PostgreSQL, Performance Tuning, PL/SQL, RMAN, Data Guard, Linux", 35},

                {"Network Engineer", "Cisco", "Bangalore, Karnataka",
                        "Design, implement, and maintain enterprise network infrastructure. Configure Cisco routers/switches, implement SD-WAN solutions, and ensure network security compliance.",
                        "FULL_TIME", "JUNIOR", 800000.0, 1400000.0,
                        "Cisco Routing, Switching, Firewalls, VPN, TCP/IP, SD-WAN, CCNP", 30},

                {"SAP Consultant", "Capgemini", "Pune, Maharashtra",
                        "Implement and support SAP S/4HANA modules for manufacturing and retail clients. Configure SAP FI/CO, MM, SD modules and provide end-user training and post-go-live support.",
                        "FULL_TIME", "MID", 1200000.0, 1800000.0,
                        "SAP S/4HANA, ABAP, FI/CO, MM, SD, SAP Fiori, CDS Views", 40},

                {"Full Stack Developer", "Tech Mahindra", "Mumbai, Maharashtra",
                        "Build end-to-end web applications using Angular and Spring Boot. Develop RESTful APIs, implement responsive UIs, and deploy applications on cloud platforms.",
                        "FULL_TIME", "MID", 900000.0, 1500000.0,
                        "Angular, Spring Boot, Java, PostgreSQL, Docker, AWS, TypeScript", 35},

                {"Automation Testing Engineer", "Tata Consultancy Services (TCS)", "Chennai, Tamil Nadu",
                        "Design and implement automated test frameworks using Selenium and Cypress. Create comprehensive test suites, integrate with CI/CD pipelines, and ensure 95%+ code coverage.",
                        "FULL_TIME", "JUNIOR", 500000.0, 900000.0,
                        "Selenium, Cypress, Java, TestNG, Jenkins, API Testing, Git", 30},

                {"Blockchain Developer", "Infosys", "Bangalore, Karnataka",
                        "Build decentralized applications (dApps) on Ethereum and Hyperledger Fabric. Develop and audit smart contracts, implement consensus mechanisms, and ensure security best practices.",
                        "CONTRACT", "MID", 1400000.0, 2200000.0,
                        "Solidity, Ethereum, Hyperledger, Web3.js, Node.js, Smart Contracts, Go", 60},

                {"Product Manager", "Flipkart", "Bangalore, Karnataka",
                        "Own product roadmap for Flipkart's e-commerce platform. Drive product strategy with data-driven decisions, manage cross-functional teams, and deliver features for 100M+ users.",
                        "FULL_TIME", "LEAD", 3000000.0, 4500000.0,
                        "Product Strategy, Analytics, A/B Testing, Agile, SQL, User Research", 45},

                {"Data Engineer", "Zomato", "Gurgaon, Haryana",
                        "Build real-time data pipelines processing millions of orders daily. Design and maintain data warehouses, implement streaming analytics, and support data-driven decision making.",
                        "FULL_TIME", "MID", 1400000.0, 2200000.0,
                        "Spark, Kafka, Hadoop, Python, SQL, Airflow, AWS, Data Warehousing", 35},

                {"Technical Content Writer", "Unacademy", "Remote, India",
                        "Create engaging technical content for India's leading edtech platform. Write tutorials, documentation, and blog posts on programming, cloud computing, and emerging technologies.",
                        "PART_TIME", "ENTRY", 400000.0, 700000.0,
                        "Technical Writing, SEO, Content Strategy, Research, Communication, Markdown", 25}
        };

        for (Object[] jobData : jobsData) {
            String title = (String) jobData[0];
            String company = (String) jobData[1];

            if (jobRepository.existsByTitleAndCompany(title, company)) {
                log.debug("Job already exists, skipping: {} at {}", title, company);
                continue;
            }

            Job job = Job.builder()
                    .title(title)
                    .company(company)
                    .location((String) jobData[2])
                    .description((String) jobData[3])
                    .employmentType((String) jobData[4])
                    .experienceLevel((String) jobData[5])
                    .salaryMin((Double) jobData[6])
                    .salaryMax((Double) jobData[7])
                    .skills((String) jobData[8])
                    .deadline(baseDeadline.plusDays((Integer) jobData[9]))
                    .active(true)
                    .employer(employer)
                    .build();
            jobRepository.save(job);
            log.info("Seeded job: {} at {}", title, company);
        }
    }

}

