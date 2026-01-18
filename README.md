# 💸 Sharded Saga Wallet System

A **distributed wallet & transaction system** built using **Spring Boot, Apache ShardingSphere, and Saga Pattern**, designed to demonstrate **real-world distributed system concepts** such as **data sharding, eventual consistency, and fault-tolerant transactions**.

This project simulates how modern fintech systems handle **money transfers at scale** without relying on distributed locks or 2PC.

---

## 🚀 Why This Project?

This project was built to deeply understand and implement:

- 🔀 **Database Sharding** (horizontal scaling)
- 🔁 **Saga Pattern** for distributed transactions
- 📦 **Event-driven workflows**
- 💥 **Failure handling & compensation**
- 🧠 **Production-grade transaction design**

Inspired by real systems used in **wallets, banking platforms, and payment gateways**.

---

## 🏗️ High-Level Architecture

Client
↓
Spring Boot REST APIs
↓
Saga Orchestrator
↓
Saga Steps (Debit → Credit → Update Status)
↓
Sharded MySQL Databases (via ShardingSphere)
