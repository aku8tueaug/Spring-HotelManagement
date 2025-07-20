# Hotel Management Application (Microservices-based)

## Overview

This is a Hotel Management System built using a **microservices architecture**. It provides functionality to manage hotels, rooms, inventory, bookings, and billing.

Each module is implemented as an independent microservice, making the system scalable, maintainable, and easy to extend.

---

## Microservices

### 1. **Hotel Service**
- Create, update, delete hotels
- Fetch hotel information

### 2. **Room Service**
- Manage room creation and updates
- Define room types (e.g., Deluxe, Standard)
- Retrieve room availability by hotel and type

### 3. **Inventory Service**
- Track room inventory by hotel and room type
- Update available room counts based on bookings

### 4. **Booking Service**
- Book rooms for users
- Validate hotel and room availability
- Update inventory upon booking confirmation

### 5. **Billing Service**
- Generate bills for confirmed bookings
- Integrate Dynamic pricing and payment logic

---

## Tech Stack

- **Language**: Java
- **Framework**: Spring Boot
- **Database**: MySQL
- **Communication**: REST APIs, HTTP Clients
- **Containerization**: Docker
- **Service Registry (planned)**: Eureka

---

## Getting Started

### Prerequisites

- Java 17+
- Maven
- MySQL
- Docker & Docker Compose

### Setup Steps

1. **Clone the repository:**
   ```bash
   git clone https://github.com/aku8tueaug/Spring-HotelManagement.git
   
