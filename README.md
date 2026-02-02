
# MediTrack

A full-stack medication tracking application that helps users manage their prescriptions, dosages, and schedules. Built with React, Spring Boot, and PostgreSQL.

## 🔗 Live Demo

**Try it out:** [https://meditrack-delta-one.vercel.app](https://meditrack-delta-one.vercel.app)

## 📋 Features

- **User Authentication**: Secure registration and login with JWT-based authentication
- **Email Verification**: Email verification system for new user accounts
- **Password Management**: Password reset functionality with secure token-based system
- **Medication Search**: Search medications using the OpenFDA Drug API
- **Medication Management**: Add, edit, and delete medications with detailed information
- **Smart Dosage Input**: Split number/unit fields with dropdown for common medication units (mg, mcg, tablets, etc.)
- **Notes & Instructions**: Add optional notes with modal popup display for longer content
- **Responsive Design**: Clean, modern UI built with Tailwind CSS
- **Profile Management**: Update user profile information

## 🛠️ Tech Stack

### Frontend
- **React** with Vite
- **React Router** for navigation
- **Tailwind CSS** for styling
- **Axios** for API requests
- Deployed on **Vercel**

### Backend
- **Spring Boot** (Java)
- **Spring Security** with JWT authentication
- **PostgreSQL** database
- **Flyway** for database migrations
- **Resend** email service with custom domain
- Deployed on **Render**

### Database & Services
- **Supabase** PostgreSQL (free tier, 500MB)
- **Resend** email service (custom domain: meditracks.org)
- **OpenFDA API** for drug information

## ✨ What I Built

This project was built from scratch as a full-stack medication management system. Key accomplishments:

- Designed and implemented RESTful API with Spring Boot
- Built responsive React frontend with modern UI/UX
- Integrated OpenFDA API for real-time drug information lookup
- Implemented secure JWT-based authentication with refresh tokens
- Set up email verification and password reset flows
- Migrated from development to production-ready free hosting stack
- Configured custom domain email sending with Resend
- Optimized dosage input with smart split fields and unit selection
- Added modal-based notes display for better UX
- Deployed frontend to Vercel with CI/CD
- Deployed backend to Render with environment-based configuration
- Migrated database from Render to Supabase for sustainability


## 🔐 Security Features

- Password hashing with BCrypt
- JWT access & refresh tokens
- Email verification for new accounts
- Secure password reset with time-limited tokens
- CORS configuration for frontend-backend communication
- Environment-based configuration for sensitive data

## 💡 Key Learning Outcomes

- Full-stack application development with modern frameworks
- RESTful API design and implementation
- Database schema design and migrations
- JWT authentication and authorization
- Email service integration with custom domains
- Frontend state management and routing
- Deployment and DevOps (Vercel, Render, Supabase)
- Environment configuration and secrets management
- API integration (OpenFDA)
- Responsive web design with Tailwind CSS

## 📝 License

This project is for portfolio/demonstration purposes.

## 👤 Author

Built by Jasdeep Chahal

---

*Note: This is a demonstration project. Configuration files contain placeholder values and the application requires proper environment setup to run locally.*
