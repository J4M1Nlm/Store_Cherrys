# CherryTwins Store API (Backend)

> **Purpose:** Backend API for the CherryTwins e‑commerce storefront (Spring Boot + JWT + REST). This repo is the server-side project; the frontend consumes the REST API.

---

## 📌 Overview (Frontend‑centric)

This project implements the entire backend for an online store, including: authentication, catalog, cart, checkout, payments (simulated + Stripe webhook support), user profiles & addresses, order history, coupons, and reviews.

The frontend should treat this as a headless API server:

- **Base URL:** `http://localhost:8080` (default)
- **API prefix:** `/api/*`
- **Auth:** JWT Bearer tokens (returned by `/api/auth/login` and `/api/auth/register`)

This README documents the endpoints, request/response shapes, security rules, and expected behavior for frontend developers.

---

## 🚀 Getting Started (Local Development)

### Run with Maven (recommended)

```bash
./mvnw spring-boot:run
```

### Run with Docker

```bash
docker build -t cherry-twins-backend .
docker run -e SPRING_DATASOURCE_URL=... -e SPRING_DATASOURCE_USERNAME=... -e SPRING_DATASOURCE_PASSWORD=... -p 8080:8080 cherry-twins-backend
```

> 🚩 The backend expects a database (configured via `SPRING_DATASOURCE_*`). It does not include an embedded H2 setup by default.

---

## 🧩 Important Configuration (env variables)

The backend is configured via `src/main/resources/application.yml` and environment variables. Key variables for frontend integration:

| Env var | Default | Purpose |
|--------|---------|---------|
| `PORT` | `8080` | Server port |
| `JWT_SECRET` | *required* | Secret used to sign JWTs (must be set) |
| `JWT_EXP_MINUTES` | `60` | Token expiry |
| `FRONTEND_BASE_URL` | `http://localhost:3000` | Used to build links in verification/reset emails |
| `MAIL_FROM` | `no-reply@cherrytwins.com` | Sender address for transactional emails |
| `PAYMENTS_PROVIDER` | `stripe` | Payment provider (currently supports simulated + Stripe webhook support) |

### CORS

Allowed origins are hardcoded in `src/main/java/com/cherrytwins/shop/security/CorsConfig.java`:

- `http://127.0.0.1:3000`
- `http://localhost:3000`
- `https://pagina-cherry-twins.onrender.com`

If your frontend runs elsewhere, update this file.

---

## 🔐 Auth (JWT)

### Register

`POST /api/auth/register`

Request body (JSON):

```json
{
  "email": "user@example.com",
  "password": "P4ssw0rd!",
  "fullName": "My Name",
  "phone": "+123456789"
}
```

Response (200):

```json
{
  "token": "<JWT>"
}
```

### Login

`POST /api/auth/login`

Request body (JSON):

```json
{
  "email": "user@example.com",
  "password": "P4ssw0rd!"
}
```

Response (200):

```json
{
  "token": "<JWT>"
}
```

### Using the token

Include the token on authenticated requests:

```
Authorization: Bearer <JWT>
```

### Password reset (email flow)

`POST /api/auth/password/forgot`

Request body (JSON):

```json
{ "email": "user@example.com" }
```

**NOTE:** This endpoint always returns `204 No Content` for security; it does not reveal whether the email exists.

### Email verification / reset password (frontend requirements)

The backend sends verification/reset links to the frontend URL configured via `FRONTEND_BASE_URL` using the query parameter `token`:

- `/verify-email?token=<token>`
- `/reset-password?token=<token>`

⚠️ **Important:** There is currently *no backend endpoint* that actually consumes these tokens (the verification and reset logic exists in the backend service layer, but no public route is exposed). A frontend implementation would need a new REST endpoint (e.g., `POST /api/auth/verify-email` and `POST /api/auth/reset-password`) to complete the flow.

---

## 📚 Public (No Auth) Endpoints

### Catalog

| Endpoint | Method | Description |
|--------|--------|-------------|
| `/api/catalog/artists` | GET | List artists |
| `/api/catalog/categories` | GET | List categories (flat) |
| `/api/catalog/categories/tree` | GET | Category tree |
| `/api/catalog/products` | GET | Paginated product list with filters |
| `/api/catalog/products/{slug}` | GET | Product detail (includes variants + images) |

#### Products query params

- `q` (string) — full text search
- `artist` (slug)
- `category` (slug)
- `minPriceCents` (int)
- `maxPriceCents` (int)
- `page` (int, default 0)
- `size` (int, default 20)
- `sort` (string, e.g. `price:asc` / `price:desc`)

### Reviews (public)

| Endpoint | Method | Description |
|--------|--------|-------------|
| `/api/catalog/products/{productId}/reviews` | GET | Paginated list of reviews for a product |

---

## 🔒 Authenticated Endpoints (Requires Bearer Token)

### User profile

| Endpoint | Method | Description |
|--------|--------|-------------|
| `/api/users/me` | GET | Get current user profile |
| `/api/users/me` | PUT | Update profile (`fullName`, `phone`) |

### Addresses

| Endpoint | Method | Description |
|--------|--------|-------------|
| `/api/users/addresses` | GET | List user addresses |
| `/api/users/addresses` | POST | Create address |
| `/api/users/addresses/{id}` | GET | Get address by id |
| `/api/users/addresses/{id}` | PUT | Update address |
| `/api/users/addresses/{id}/default` | PUT | Mark as default address |
| `/api/users/addresses/{id}` | DELETE | Delete address |

### Cart

| Endpoint | Method | Description |
|--------|--------|-------------|
| `/api/cart` | GET | Get or create active cart |
| `/api/cart/items` | POST | Add item to cart |
| `/api/cart/items/{itemId}` | PUT | Update item quantity |
| `/api/cart/items/{itemId}` | DELETE | Remove item |
| `/api/cart` | DELETE | Clear cart |
| `/api/cart/abandon` | POST | Mark current cart as abandoned |

### Orders

| Endpoint | Method | Description |
|--------|--------|-------------|
| `/api/orders/checkout` | POST | Checkout cart → order PENDING |
| `/api/orders` | GET | List own orders (paginated) |
| `/api/orders/{orderId}` | GET | Order details |

### Payments (simulated)

| Endpoint | Method | Description |
|--------|--------|-------------|
| `/api/payments/init` | POST | Create payment intent (returns a simulated `clientSecret`) |
| `/api/payments/{paymentId}/simulate/succeed` | POST | Mark payment as successful (for demo/testing) |
| `/api/payments/{paymentId}/simulate/fail` | POST | Mark payment as failed (for demo/testing) |

### Coupons

| Endpoint | Method | Description |
|--------|--------|-------------|
| `/api/coupons/validate` | POST | Validate coupon + compute discount |

### Reviews (authenticated)

| Endpoint | Method | Description |
|--------|--------|-------------|
| `/api/catalog/products/{productId}/reviews` | POST | Create review (requires purchase) |
| `/api/catalog/products/reviews/{reviewId}` | PUT | Update own review |
| `/api/catalog/products/reviews/{reviewId}` | DELETE | Delete own review |

---

## 🛠️ Admin (requires ADMIN role)

There are APIs under `/api/admin/**` protected by `ROLE_ADMIN`. They are not intended for public storefront UI, but can be used by an admin panel to manage products, inventory, orders, coupons, reviews, and users.

---

## 🧠 API Error Format

Errors are returned with a JSON body. Common shapes:

- Validation errors (`400`):

```json
{
  "message": "Validation error",
  "fields": { "fieldName": "message" }
}
```

- Generic errors (`400`, `401`, `404`, `500`):

```json
{ "message": "Error message" }
```

---

## 🧪 API Docs (Swagger)

You can access the OpenAPI docs:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

---

## 🧩 Notes for Frontend Developers

- **JWT storage:** Store tokens securely (e.g., HttpOnly cookie or in-memory) and send via `Authorization: Bearer <token>`.
- **Token expiry:** Tokens expire by default after `60` minutes (`app.jwt.exp-minutes`). Frontend should detect `401` and prompt re-login.
- **Cart workflow:** Cart is tied to the authenticated user; it is created on first access.
- **Checkout flow:** The frontend should create an order (`/api/orders/checkout`), then initialize a payment (`/api/payments/init`), then simulate/confirm payment.
- **Email flow:** The backend sends email verification and password reset links, but **does not expose endpoints** to consume the tokens yet. If you implement frontend pages (`/verify-email`, `/reset-password`), coordinate with backend to add endpoints.

---

## 🧱 Project Structure (Key Packages)

- `com.cherrytwins.shop.security` — Auth, JWT, and security configuration
- `com.cherrytwins.shop.catalog` — Public catalog & product APIs
- `com.cherrytwins.shop.cart` — Cart model + endpoints
- `com.cherrytwins.shop.orders` — Checkout + order history
- `com.cherrytwins.shop.payments` — Payment handling (simulated + Stripe webhook)
- `com.cherrytwins.shop.users` — Profile + address management
- `com.cherrytwins.shop.reviews` — Product reviews

---

## ✅ Quick Tips

- Use the Swagger UI to experiment with endpoints and payloads.
- For local frontend development, ensure `FRONTEND_BASE_URL` matches your frontend origin to get correct email links.
- If you need additional APIs (e.g., email token consumption), add new endpoints under `/api/auth` and follow existing patterns.
