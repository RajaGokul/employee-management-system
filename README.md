# Employee Management System - End-to-End DevOps CI/CD Project

## Project Overview

This project demonstrates an end-to-end DevOps implementation for deploying a containerized Java Spring Boot application on AWS.

The project covers the complete software delivery lifecycle including source code management, continuous integration, containerization, deployment, load balancing, database management, monitoring, and backup automation.

The application is deployed using Docker containers on AWS EC2 and exposed through Nginx and an Application Load Balancer.

Infrastructure monitoring is implemented using Prometheus, Grafana, and Node Exporter.

Database backups are automated using shell scripting and scheduled using Cron jobs, with backup files stored securely in Amazon S3.

---

# Architecture Overview

```
                          Developer
                              |
                              |
                              v
                       GitHub Repository
                     (Spring Boot Source Code)
                              |
                              |
                              v
                       Jenkins EC2 Server
                         CI/CD Pipeline
                              |
        ------------------------------------------------
        |              |               |               |
        v              v               v               v
   Checkout       Maven Build     Docker Build    Security Scan
        |              |               |               |
        ------------------------------------------------
                              |
                              v
                    Docker Image Push
                       Docker Hub
                              |
                              |
                              v
                    Application EC2 Server
                              |
                              |
                              v
                         Nginx Proxy
                           Port 80
                              |
                              |
                              v
                  Spring Boot Docker Container
                           Port 8080
                              |
                              |
                              v
                         Database Server


User Traffic Flow:

User
 |
 v
AWS Application Load Balancer
 |
 v
Target Group
 |
 v
Nginx EC2 Instance :80
 |
 v
Spring Boot Container :8080


Monitoring Flow:

Application EC2
      |
      v
Node Exporter
      |
      v
Prometheus
      |
      v
Grafana Dashboard


Database Backup Flow:

Database Server
      |
      v
Bash Backup Script
      |
      v
Cron Scheduler
      |
      v
Database Dump File
      |
      v
Amazon S3 Bucket
```

---

# Technology Stack

| Category | Technology |
|---|---|
| Programming Language | Java |
| Framework | Spring Boot |
| Build Tool | Maven |
| Source Control | Git, GitHub |
| CI/CD Tool | Jenkins |
| Containerization | Docker |
| Container Registry | Docker Hub |
| Cloud Platform | AWS EC2 |
| Load Balancer | AWS Application Load Balancer |
| Reverse Proxy | Nginx |
| Database | MySQL / PostgreSQL |
| Database Backup | mysqldump / pg_dump |
| Backup Storage | Amazon S3 |
| Monitoring | Prometheus |
| Visualization | Grafana |
| Metrics Collection | Node Exporter |
| Automation | Bash Script + Cron |
| Operating System | Ubuntu Linux |

---

# Application Details

## Spring Boot Application

The application is developed using Spring Boot and packaged as an executable JAR file.

Application Port:

```
8080
```

Build command:

```bash
mvn clean package
```

Generated artifact:

```
target/*.jar
```

---

# Local Application Setup

## Clone Repository

```bash
git clone <repository-url>
```

Navigate:

```bash
cd employee-management-system
```

---

## Build Application

```bash
mvn clean package
```

---

## Run Application

```bash
java -jar target/*.jar
```

Application URL:

```
http://localhost:8080
```

---

# Docker Implementation

The Spring Boot application is packaged inside a Docker container.

## Dockerfile

```dockerfile
FROM eclipse-temurin:21-jre

LABEL maintainer="Raja Gokul"
LABEL application="employee-management-system"

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
```

---

## Build Docker Image

```bash
docker build -t employee-management-system .
```

---

## Run Docker Container

```bash
docker run -d \
--name employee-management-system \
-p 8080:8080 \
employee-management-system
```

Verify:

```bash
docker ps
```

---

# CI/CD Pipeline

## Pipeline Workflow

1. Developer pushes code to GitHub.
2. Jenkins pipeline is triggered.
3. Jenkins checks out the latest source code.
4. Maven builds the Spring Boot application.
5. Unit tests are executed.
6. Docker image is created.
7. Docker image security scan is performed.
8. Docker image is pushed to Docker Hub.
9. Jenkins deploys the latest container to Application EC2.
10. Application becomes available through Nginx and ALB.

---

# Jenkins Pipeline Stages

## 1. Checkout

Fetches source code from GitHub.

---

## 2. Maven Build

Compiles source code and packages application.

Command:

```bash
mvn clean package
```

---

## 3. Testing

Executes application test cases.

Command:

```bash
mvn test
```

---

## 4. Docker Image Build

Creates application container image.

Example:

```bash
docker build -t employee-management-system:$BUILD_NUMBER .
```

---

## 5. Security Scan

Docker image vulnerability scanning using Trivy.

Example:

```bash
trivy image employee-management-system:$BUILD_NUMBER
```

---

## 6. Push Image

Pushes Docker image to Docker Hub.

Example:

```bash
docker push <dockerhub-user>/employee-management-system:$BUILD_NUMBER
```

---

## 7. Deployment

Application EC2 pulls the latest image and runs the container.

Deployment steps:

```bash
docker pull image-name

docker stop employee-management-system || true

docker rm employee-management-system || true

docker run -d \
--name employee-management-system \
-p 8080:8080 \
image-name
```

---

# Nginx Reverse Proxy

Nginx acts as a reverse proxy between the Application Load Balancer and Spring Boot container.

Traffic flow:

```
ALB
 |
 v
Nginx :80
 |
 v
Spring Boot Container :8080
```

Nginx configuration:

```nginx
server {

    listen 80;

    server_name _;

    location / {

        proxy_pass http://localhost:8080;

        proxy_set_header Host $host;

        proxy_set_header X-Real-IP $remote_addr;

        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;

    }

}
```

---

# AWS Load Balancer Configuration

## Application Load Balancer

ALB distributes incoming traffic across healthy targets.

Listener:

```
Protocol: HTTP
Port: 80
```

Target Group:

```
Protocol: HTTP
Port: 80
```

Health Check:

```
Protocol: HTTP
Path: /health
Port: 80
Success Code: 200
```

Health Check Flow:

```
ALB
 |
 v
Nginx :80
 |
 v
/health endpoint
 |
 v
HTTP 200 Response
```

---

# Database Implementation

The Spring Boot application connects with a relational database.

Database responsibilities:

- Store application data
- Support CRUD operations
- Maintain application persistence

Database connection flow:

```
Spring Boot Application
          |
          v
     Database Server
          |
          v
     Application Data
```

Database configuration is managed using application properties.

Example:

```properties
spring.datasource.url=<database-url>

spring.datasource.username=<username>

spring.datasource.password=<password>
```

---

# Database Backup Automation

Database backups are automated using Bash scripts and Cron.

Backup process:

```
Database
   |
   v
Database Dump Script
   |
   v
SQL Backup File
   |
   v
Amazon S3 Bucket
```

---

## Backup Script

Example:

```bash
#!/bin/bash

DATE=$(date +"%Y-%m-%d")

BACKUP_DIR="/backup"

DB_NAME="employee_db"

BACKUP_FILE="$BACKUP_DIR/employee_db_$DATE.sql"


mysqldump \
-u username \
-p password \
$DB_NAME > $BACKUP_FILE


aws s3 cp \
$BACKUP_FILE \
s3://employee-db-backup-bucket/


echo "Database backup completed"
```

---

# Cron Automation

Database backup runs automatically using Cron.

Example:

```bash
0 1 * * * /home/ubuntu/scripts/db_backup.sh
```

Execution:

```
Daily at 01:00 AM
```

---

# Monitoring Implementation

## Node Exporter

Node Exporter collects server-level metrics.

Metrics:

- CPU utilization
- Memory usage
- Disk utilization
- Network statistics

Port:

```
9100
```

---

## Prometheus

Prometheus collects metrics from Node Exporter.

Configuration:

```yaml
scrape_configs:

- job_name: "node_exporter"

  static_configs:

  - targets:

    - "<server-ip>:9100"
```

Prometheus URL:

```
http://<prometheus-ip>:9090
```

---

## Grafana

Grafana provides monitoring dashboards.

Implemented dashboards:

- CPU Usage
- Memory Usage
- Disk Usage
- Network Monitoring
- Server Availability

Grafana URL:

```
http://<grafana-ip>:3000
```

---

# Automation Scripts

Repository contains:

```
scripts/

├── db_backup.sh
├── cleanup.sh
```

Responsibilities:

- Database backup
- Log cleanup
- Scheduled maintenance tasks

---

# Repository Structure

```
employee-management-system

|
├── src/
|
├── pom.xml
|
├── Dockerfile
|
├── Jenkinsfile
|
├── scripts/
|     |
|     ├── db_backup.sh
|     └── cleanup.sh
|
├── screenshots/
|
└── README.md
```

---

# Challenges Faced & Solutions

## Jenkins Java Version Issue

### Problem

Jenkins failed because the installed Java version was not supported.

### Solution

Installed Java 21 and configured Jenkins JAVA_HOME.

---

## Node Exporter Architecture Issue

### Problem

Node Exporter failed due to incorrect binary architecture.

### Solution

Downloaded the correct Linux AMD64 binary.

---

## Prometheus Target Down

### Problem

Prometheus could not scrape Node Exporter metrics.

### Solution

Validated:

- Security Group rules
- Port 9100 connectivity
- Node Exporter service
- Prometheus configuration

---

## Grafana Datasource Issue

### Problem

Grafana dashboard showed no metrics.

### Solution

Configured Prometheus datasource correctly.

---

# Future Enhancements

- Implement Terraform for AWS provisioning
- Deploy application using Kubernetes

The following improvements can be implemented to make the application more scalable, secure, and highly available.

## 1. CDN and DNS Integration
- Integrate **AWS CloudFront** for faster content delivery and caching.
- Configure **Route 53** for custom domain management and DNS routing.

## 2. High Availability & Scalability
- Deploy application servers across multiple Availability Zones.
- Implement **Auto Scaling Group (ASG)** for automatic scaling based on traffic/load.

## 3. Disaster Recovery (DR)
- Enable automated database backups with **S3 Cross-Region Replication**.
- Implement multi-region deployment for business continuity.

## 4. Security Enhancement
- Configure HTTPS using **AWS Certificate Manager (ACM)**.
- Implement secure communication between application components.

## 5. Infrastructure Automation
- Provision AWS infrastructure using **Terraform**.
- Implement Kubernetes deployment for container orchestration.

## 6. Advanced Monitoring
- Add centralized logging using ELK Stack.
- Implement Prometheus Alert Manager for advanced alerting.
---

# Author

Raja Gokul

DevOps Engineer

Skills:
AWS | Jenkins | Docker | Linux | Spring Boot | Maven | Nginx | Prometheus | Grafana
