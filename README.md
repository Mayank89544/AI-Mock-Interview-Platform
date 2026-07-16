# AI Mock Interview Platform

A full-stack AI-powered platform that simulates real technical interviews and provides instant, structured feedback — helping candidates practice and improve before the real thing.

🔗 **Live Demo:** https://ai-mock-interview-frontend-ics9.onrender.com

💻 **Repository:** https://github.com/Mayank89544/AI-Mock-Interview-Platform

---

## 📖 Overview

Instead of static question banks, this platform generates fresh, role-specific interview questions using Google's Gemini API and evaluates each answer in real time — scoring technical accuracy, depth, and clarity, and offering concrete feedback on strengths, weaknesses, and how to improve.

## ✨ Features

- **Role & difficulty-based question generation** — choose a job role (e.g., Java Backend Developer, Full-Stack Developer, React Frontend Developer, Spring Boot Developer) and difficulty level (Junior / Mid-Level / Senior)
- **5 AI-generated questions per session**, each tagged with its technical topic
- **Real-time AI evaluation** of every answer across 3 metrics: technical accuracy, depth, and clarity, rolled into an overall score
- **Detailed qualitative feedback** — strengths, improvement areas, and a specific tip for each answer
- **Full interview report** at the end of each session with per-question breakdowns
- **Persistent interview history** — past sessions are saved and retrievable, so users can track progress over time

## 🛠️ Tech Stack

**Frontend**
- React (Vite)
- Fetch API for backend communication

**Backend**
- Spring Boot (Java)
- Spring Data JPA / Hibernate
- PostgreSQL

**AI Integration**
- Google Gemini 2.5 Flash
- Structured JSON schema output for reliable, parseable question generation and evaluation

**Deployment**
- Render (frontend as a Static Site, backend as a Web Service, PostgreSQL as a managed database)

## 🏗️ Architecture

```
┌─────────────┐         ┌──────────────────┐         ┌─────────────┐
│   React      │  REST   │   Spring Boot     │  API    │   Gemini    │
│   Frontend   │ ──────▶ │   Backend         │ ──────▶ │   2.5 Flash │
│  (Render)    │ ◀────── │   (Render)        │ ◀────── │             │
└─────────────┘         └────────┬──────────┘         └─────────────┘
                                   │
                                   ▼
                          ┌─────────────────┐
                          │   PostgreSQL     │
                          │   (Render)       │
                          └─────────────────┘
```

## 🔌 API Endpoints

| Method | Endpoint                     | Description                                      |
|--------|-------------------------------|---------------------------------------------------|
| POST   | `/api/interview/start`       | Generates 5 role/difficulty-specific questions     |
| POST   | `/api/interview/evaluate`    | Evaluates a single answer and returns scored feedback |
| POST   | `/api/interview/save`        | Saves a completed session with all answers/scores  |
| GET    | `/api/interview/history`     | Retrieves all past interview sessions              |

## 🚀 Getting Started (Local Setup)

### Prerequisites
- Node.js (v18+)
- Java 17+
- Maven
- PostgreSQL running locally
- A Google Gemini API key

### Backend Setup
```bash
cd backend
# Set environment variables (or use application.properties defaults)
export GOOGLE_API_KEY=your_gemini_api_key
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/interviewdb
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres

mvn spring-boot:run
```
The backend will start on `http://localhost:8080`.

### Frontend Setup
```bash
cd frontend
npm install

# Create a .env file with:
# VITE_API_BASE_URL=http://localhost:8080

npm run dev
```
The frontend will start on `http://localhost:5173`.

## 🌍 Deployment Notes

This project is deployed on Render with the frontend and backend as two independent services:

- **Frontend (Static Site):** built with `npm install && npm run build`, published from the `dist` directory. The `VITE_API_BASE_URL` environment variable must be set **before building**, since Vite bakes environment variables into the bundle at build time, not runtime.
- **Backend (Web Service):** listens on the port Render assigns via the `PORT` environment variable. CORS is configured to explicitly allow the deployed frontend's origin.
- **Database:** managed PostgreSQL instance on Render, connected via environment variables (`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`).

> **Note:** The live demo is hosted on Render's free tier, which spins down after periods of inactivity. The first request after idling may take 30–60 seconds to respond while the server wakes up.


## 🔮 Future Improvements

- Add support for behavioral/HR interview questions alongside technical ones
- Voice-based answer input with speech-to-text
- Export interview reports as PDF
- Add authentication so users can view only their own history
- Set up uptime monitoring to eliminate cold-start delays on the live demo

## 👤 Author

**Mayank Chandel**
[LinkedIn](https://linkedin.com/in/mayank-chandel) • [GitHub](https://github.com/Mayank89544) • [LeetCode](https://leetcode.com/mayank_0001)

## 📄 License

This project is open source and available under the [MIT License](LICENSE).