# ParkEase (Backend) 🚗

ParkEase is a lightweight, learning-focused backend for a real-time parking management system. It provides APIs for managing parking lots, reserving slots, handling payments, and delivering real-time status updates via WebSockets. Designed specifically as an SDE resume demo project to showcase backend workflows, clean architecture, and system integration.

---

## 🛠️ Tech Stack & Key Features
- **Java 17 & Spring Boot**: Core application framework.
- **Spring Security**: Role-based access control (`USER`, `OWNER`, `ADMIN`) using stateful session cookies (tailored for SPA frontends).
- **Spring Data JPA & MySQL**: Relational database persistence.
- **WebSockets (STOMP & SockJS)**: Real-time broadcast of slot availability.
- **Razorpay Integration**: Payment order creation, secure verification (HMAC-SHA256), and retry handling.
- **Spring Scheduling**: Automated background tasks for booking lifecycles.

---

## 📁 Project Structure

```text
parkease-backend
├── src/main/java/org/example/parkease/
│   ├── config/             # Security (Dev/Prod), WebSockets, and Razorpay Config
│   ├── controller/         # REST Controllers (Auth, Admin, Booking, Lot, Slot, Payment)
│   ├── dto/                # Request and Response Data Transfer Objects (DTOs)
│   ├── entity/             # JPA Database Entities
│   ├── enums/              # Enums (Role: USER/OWNER/ADMIN, BookingStatus)
│   ├── event/              # WebSocket events and broadcasts
│   ├── exception/          # Custom Exceptions & Global Exception Handler
│   ├── repository/         # Spring Data JPA Repositories
│   ├── service/            # Core business logic services
│   └── ParkEaseApplication.java
└── src/main/resources/
    ├── application.properties      # Shared configurations & custom timeout properties
    ├── application-dev.properties  # Dev database environment setup
    └── application-prod.properties # Production CORS/Database setup
```

---

## 🗄️ Database Schema

### 1. `user` Table
| Column | Type | Key | Description / Constraints |
| :--- | :--- | :--- | :--- |
| `id` | `INT` | PK | Primary Key (Identity) |
| `username` | `VARCHAR` | Unique | Unique identifier for login (Not Null) |
| `password` | `VARCHAR` | - | BCrypt hashed password (Not Null) |
| `role` | `VARCHAR` | - | Roles: `USER`, `OWNER`, `ADMIN` |

### 2. `parking_lot` Table
| Column | Type | Key | Description / Constraints |
| :--- | :--- | :--- | :--- |
| `id` | `INT` | PK | Primary Key (Identity) |
| `name` | `VARCHAR` | Unique | Unique name of the parking lot (Not Null) |
| `location` | `VARCHAR` | - | Address / geographic location (Not Null) |
| `total_slots` | `INT` | - | Total slots capacity |
| `is_active` | `BOOLEAN` | - | Status flag of the parking lot |
| `owner_id` | `INT` | FK | References `user.id` (Not Null) |

### 3. `parking_slot` Table
| Column | Type | Key | Description / Constraints |
| :--- | :--- | :--- | :--- |
| `id` | `INT` | PK | Primary Key (Identity) |
| `slot_number`| `INT` | - | Slot identifier number inside the lot |
| `slot_type` | `VARCHAR` | - | Type of slot (e.g. CAR, BIKE) |
| `available` | `BOOLEAN` | - | Availability status (true = free, false = locked) |
| `price_per_minute`| `DECIMAL`| - | Price rate per minute |
| `lot_id` | `INT` | FK | References `parking_lot.id` (Not Null) |

### 4. `booking` Table
| Column | Type | Key | Description / Constraints |
| :--- | :--- | :--- | :--- |
| `id` | `INT` | PK | Primary Key (Identity) |
| `user_id` | `INT` | FK | References `user.id` (Not Null) |
| `parking_slot_id`| `INT`| FK | References `parking_slot.id` |
| `status` | `VARCHAR` | - | Enum: `PENDING_PAYMENT`, `CONFIRMED`, `ACTIVE`, `COMPLETED`, `PAYMENT_FAILED`, `PAYMENT_EXPIRED` |
| `booked_at` | `DATETIME` | - | Timestamp when booking was created |
| `paid_at` | `DATETIME` | - | Timestamp when booking payment verified |
| `start_time` | `DATETIME` | - | Actual starting time of parking |
| `end_time` | `DATETIME` | - | Expiration/completion timestamp |
| `duration_minutes`| `INT`| - | Booked duration in minutes |
| `price_per_minute`| `DECIMAL`| - | Price rate captured at booking time |
| `amount` | `DECIMAL` | - | Calculated booking amount (`duration_minutes` * `price_per_minute`) |
| `razorpay_order_id`| `VARCHAR`| Unique | Razorpay Order reference ID |
| `razorpay_payment_id`| `VARCHAR`| Unique | Razorpay Payment verification reference ID |
| `retry_attempts` | `INT` | - | Total payment retry attempts (Default: 0) |

---

## 🔄 Core Application Flows

### 1. Booking & Payment Lifecycle
1. **Reserve**: A `USER` creates a booking for a slot. The slot's status changes to `available = false` (locked) and the booking status is set to `PENDING_PAYMENT`.
2. **Order**: Frontend requests order creation (`POST /api/v1/payments/create-order/{bookingId}`). The backend generates a Razorpay Order and returns it.
3. **Verify**: The frontend triggers checkout and posts verification details to `/api/v1/payments/verify`. The backend verifies the signature using HMAC-SHA256:
   - **Success**: Booking status is updated to `CONFIRMED`, `startTime` is set to now, and the slot lock is maintained.
   - **Failure**: Booking status changes to `PAYMENT_FAILED`, and the slot is released (`available = true`).
4. **Retry**: If a payment fails or expires, the user can call `/api/v1/payments/retry/{bookingId}` to re-attempt payment if the slot is still free.

### 2. Background Tasks & Real-time Updates
- **Payment Cleanup Scheduler**: Runs every 30 seconds. Automatically cancels stale `PENDING_PAYMENT` bookings after the timeout limit (default 1 minute), releases the slot, and broadcasts slot availability to frontend clients.
- **Auto-Complete Scheduler**: Runs every minute. Checks for active bookings where the duration has expired, sets booking status to `COMPLETED`, releases the slot, and broadcasts availability.
- **WebSocket Broadcast**: Any event releasing a slot publishes a `ParkingSlotAvailableEvent` which broadcasts a message to `/topic/slot-available`.

---

## 🔌 Core API Endpoints

### Authentication
- `POST /api/v1/auth/register` - Register a new user
- `POST /api/v1/auth/login` - Login (sets Session Cookie)
- `GET /api/v1/auth/me` - Get logged-in user profile
- `POST /api/v1/auth/logout` - Logout (invalidates Session)

### Parking Lots & Slots
- `GET /api/v1/parking-lots` - Get all active lots
- `POST /api/v1/parking-lots` - Create a lot (`OWNER` only)
- `GET /api/v1/parking-lots/{lotId}/slots` - Get slots for a lot
- `POST /api/v1/parking-slots` - Create slot in a lot (`OWNER` only)

### Bookings & Payments
- `POST /api/v1/bookings` - Book a slot (`USER` only)
- `GET /api/v1/bookings/my` - View my bookings (`USER` only)
- `PUT /api/v1/bookings/{bookingId}/complete` - Mark booking as finished (`USER` or `ADMIN`)
- `POST /api/v1/payments/create-order/{bookingId}` - Generate Razorpay Order
- `POST /api/v1/payments/verify` - Verify Razorpay signature & activate booking

### Admin Endpoints
- `GET /api/v1/admin/users` - View all registered users
- `DELETE /api/v1/admin/users/{userId}` - Delete user
- `GET /api/v1/bookings` - View all bookings in system