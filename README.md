# TeacherTransfer

A peer-to-peer web platform enabling Bihar government school teachers to discover mutual, one-way, and multi-hop transfer opportunities.

## Overview

TeacherTransfer helps government school teachers find transfer matches based on location, subject, and school type - without school or government involvement.

### Key Features

- **Location-based matching** using geohash indexing
- **Subject & school type filtering** for relevant matches
- **Mutual, one-way, and multi-hop matching** support
- **Paid-only model** with ₹39-₹99/month subscriptions
- **Email notifications** for match alerts
- **Bihar-focused** with all 38 districts and blocks

## Tech Stack

### Backend
- Java 17
- Spring Boot 3
- Spring Data JPA
- Spring Security (JWT)
- PostgreSQL 15
- Razorpay (payments)
- MSG91 (OTP)
- SendGrid (email)

### Frontend
- Next.js 14 (App Router)
- TypeScript
- Tailwind CSS
- Leaflet.js + OpenStreetMap

### Infrastructure
- Docker & Docker Compose
- Nginx (reverse proxy)
- Let's Encrypt (SSL)

## Quick Start

### Prerequisites

- Docker & Docker Compose
- Java 17 (for local development)
- Node.js 18 (for local development)

### Environment Variables

Copy `.env.example` to `.env` and configure:

```bash
cp .env.example .env
```

Required variables:
- `DB_PASSWORD` - Database password
- `JWT_SECRET` - JWT signing secret
- `MSG91_AUTH_KEY` - MSG91 API key
- `RAZORPAY_KEY_ID` - Razorpay key ID
- `RAZORPAY_KEY_SECRET` - Razorpay key secret
- `SENDGRID_API_KEY` - SendGrid API key

### Running with Docker

```bash
docker-compose up -d
```

Services:
- API: http://localhost:8080/api
- Frontend: http://localhost:3000
- Database: localhost:5432

## Project Structure

```
TeacherTransfer/
├── backend/                 # Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/teachertransfer/
│   │   │   │   ├── entity/       # JPA entities
│   │   │   │   ├── enums/       # Enumerations
│   │   │   │   ├── repository/  # Data repositories
│   │   │   │   ├── service/     # Business logic
│   │   │   │   ├── controller/  # REST controllers
│   │   │   │   └── dto/         # Data transfer objects
│   │   │   └── resources/
│   │   │       └── application.yml
│   ├── pom.xml
│   └── Dockerfile
├── frontend/                # Next.js application
│   ├── src/
│   │   ├── app/          # App Router pages
│   │   ├── components/    # Reusable components
│   │   ├── lib/           # Utilities
│   │   └── types/         # TypeScript types
│   ├── package.json
│   ├── next.config.js
│   ├── tailwind.config.ts
│   └── Dockerfile
├── docker/                  # Docker configurations
│   ├── docker-compose.yml
│   ├── nginx.conf
│   └── init.sql
├── plans/                  # Architecture & planning docs
└── README.md
```

## API Documentation

### Authentication

```
POST /api/auth/send-otp      # Send OTP to phone
POST /api/auth/verify-otp     # Verify OTP
POST /api/auth/register        # Register new teacher
POST /api/auth/login           # Login with phone/password
```

### Profile

```
GET  /api/teacher/me          # Get current teacher profile
PUT  /api/teacher/me          # Update teacher profile
```

### Matching

```
GET /api/matches               # Get ranked matches
GET /api/matches/map          # Get matches for map (premium)
```

### Interest

```
POST   /api/interest/{teacherId}           # Send interest
POST   /api/interest/{interestId}/accept    # Accept interest
POST   /api/interest/{interestId}/reject    # Reject interest
GET     /api/interest/sent                 # Get sent interests
GET     /api/interest/received             # Get received interests
```

### Notifications

```
GET  /api/notifications         # Get notifications
POST /api/notifications/{id}/read    # Mark as read
POST /api/notifications/read-all    # Mark all as read
```

## Database Schema

Key tables:
- `teacher` - Teacher profiles
- `teacher_geo_index` - Geospatial index for fast matching
- `transfer_interest` - Interest/ping records
- `notification` - User notifications
- `match_result` - Cached match results
- `district` - Bihar districts
- `block` - Bihar blocks
- `payment` - Payment transactions
- `subscription_plan` - Subscription plans
- `job_run` - Batch job tracking

See [`plans/v1-specification-locked.md`](plans/v1-specification-locked.md) for complete schema.

## Matching Algorithm

The matching system uses:
1. **Geohash indexing** - O(1) location lookup
2. **Haversine formula** - Accurate Earth distance calculation
3. **Batch processing** - Multi-hop matching every 12 hours

See [`plans/matching-algorithm-explained.md`](plans/matching-algorithm-explained.md) for details.

## Deployment

### Production Deployment

```bash
# Build and start services
docker-compose -f docker-compose.yml up -d

# Setup SSL with certbot
docker exec teacher-transfer-nginx certbot certonly --webroot -w /var/www/certbot -d teachertransfer.in

# Restart nginx
docker restart teacher-transfer-nginx
```

### Monitoring

- Health check: `GET /api/actuator/health`
- Logs: `docker-compose logs -f`

## Cost Breakdown

| Item | Monthly Cost |
|------|-------------|
| VPS (2 vCPU, 4GB) | ₹800 |
| Domain | ₹70 |
| SSL | ₹0 (Let's Encrypt) |
| Maps | ₹0 (OpenStreetMap) |
| Email | ₹0 (SendGrid free tier) |
| SMS | ~₹150 (1000 OTPs) |
| **Total** | **~₹1,020** |

See [`plans/scaling-strategy.md`](plans/scaling-strategy.md) for scaling projections.

## Development

### Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

## Testing

```bash
# Backend tests
cd backend
mvn test

# With Testcontainers
mvn test -Dspring.profiles.active=testcontainers
```

## Documentation

- [`plans/architecture-plan.md`](plans/architecture-plan.md) - Technical architecture
- [`plans/implementation-plan.md`](plans/implementation-plan.md) - Implementation roadmap
- [`plans/matching-algorithm-explained.md`](plans/matching-algorithm-explained.md) - Matching algorithm deep dive
- [`plans/v1-specification-locked.md`](plans/v1-specification-locked.md) - Locked V1 specification
- [`plans/viability-analysis.md`](plans/viability-analysis.md) - Viability assessment

## License

Proprietary - All rights reserved

## Contact

For support or questions, contact: support@teachertransfer.in
