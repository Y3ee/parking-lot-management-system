# Parking Lot Management System

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![SQLite](https://img.shields.io/badge/sqlite-%2307405e.svg?style=for-the-badge&logo=sqlite&logoColor=white)

This application simulates a real-world, multi-floor parking facility with features like various types of vehicle entries and exits, VIP whitelisting, dynamic fee and fine calculations, and real-time visual monitoring.

## ✨ Key Features

### 🅿️ Core Parking System
* **Visual Interactive Grid:** Real-time visual representation of a 5-floor parking lot (18 spots per floor).
* **Smart Spot Allocation:** Validates vehicle types (Car, Motorcycle, SUV/Truck, Handicapped) against spot types (Regular, Compact, Handicapped, Reserved) using Polymorphism.
* **Automated Ticketing & Billing:** Generates unique ticket IDs on entry and calculates precise, time-based receipts on exit.
* **VIP Spot:** Real-time license plate detection. Auto-upgrades registered VIPs to 'Reserved' status and grants exclusive access to blue zones.
* **Dynamic Fine Engine:** Implements the **Strategy Design Pattern** to allow Admins to switch between fine calculation rules on the fly:
  * *Fixed Scheme:* Flat rate for overstaying (>24 hours).
  * *Progressive Scheme:* Tiered penalties that increase over time.
  * *Hourly Scheme:* Continuous hourly penalty for overstays.
* **Reporting & Analytics:** Generates detailed reports for Occupancy, Revenue, Outstanding Fines, and Active Vehicles for admin.

---

## 🏗️ System Architecture & OOP Design

This project is strictly built on **Object-Oriented Programming (OOP)** principles and follows a clean **3-Tier Architecture** (UI, Service, DAO).

* **Encapsulation:** Immutable data models (e.g., `ExitBill` uses `private final` fields) ensure financial data integrity.
* **Inheritance & Polymorphism:** A robust `Vehicle` hierarchy allows the system to process any vehicle type dynamically (`vehicle.canParkIn(spotType)`).
* **Singleton Pattern:** Centralized managers (`ParkingService`, `DatabaseConnection`) prevent data desynchronization and manage system resources efficiently.
* **Strategy Pattern:** Decouples the fine calculation logic from the billing service, allowing flexible, runtime rule changes.
* **DAO Pattern (Data Access Object):** Completely separates raw SQL queries from the business logic, making the system scalable and database-agnostic.

---

## 🚀 Setup & Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Y3ee/parking-lot-management-system.git
   ```
2. **Run:** `Main.java`
  
