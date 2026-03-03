🎫 Ticket & Comment Management System

Simple Spring Boot application that allows:

Creating and viewing comments

Automatically analyzing comment context using Hugging Face AI

Automatically creating tickets based on comment content

Viewing tickets with category and priority

🛠 Tech Stack

Java 17+

Spring Boot

Spring Web

Maven (Wrapper included)

Hugging Face Inference API

Simple HTML/CSS frontend

🚀 How to Run the Project
1️⃣ Clone the repository
git clone <repo-url>
cd <project-folder>
2️⃣ Configure API Key

This project requires a Hugging Face API key.

Create application.properties inside:

src/main/resources/

Add:

huggingface.api.key=YOUR_HUGGING_FACE_API_KEY
huggingface.model=google-t5-base

⚠️ API key is NOT committed for security reasons.

Alternatively, use environment variable:

huggingface.api.key=${HF_API_KEY}

Then set locally:

Windows:

setx HF_API_KEY your_key_here

Mac/Linux:

export HF_API_KEY=your_key_here
3️⃣ Run the Application

Using Maven Wrapper (recommended):

Windows:

mvnw.cmd spring-boot:run

Mac/Linux:

./mvnw spring-boot:run

Application runs on:

http://localhost:8080
🤖 AI Ticket Creation Logic

When a comment is submitted:

The comment is sent to Hugging Face model.

Model analyzes context.

If createTicket = true, a ticket is created automatically with:

Title

Category (bug, feature, billing, account, other)

Priority (low, medium, high)

Summary

📂 Project Structure
src/main/java/
    controller/
    service/
    model/
    repository/

src/main/resources/
    application.properties (NOT committed)
    static/index.html
🧪 Example Comment

Input:

My phone is not working, I need help from a professional.

AI Output (example):

{
  "createTicket": true,
  "title": "Phone not working",
  "category": "bug",
  "priority": "high",
  "summary": "User reports phone malfunction and requests assistance."
}

👤 Author

Anton Aliaksandrau
Java Backend Developer
