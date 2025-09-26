# 🎫 OnboardIQ Backend

This backend provides APIs for managing tickets and sending ticket summaries.  
It is built using **Java + Javalin** and is consumed by the Gmail add-on frontend.

---

## 🚀 Purpose
The backend handles:
- Creating new tickets from Gmail messages.
- Updating tickets with additional information (IBS number).
- Sending users a summary email of their tickets for a specified time period.

---

## 📡 API Endpoints

### 1. `POST /tickets`
**Purpose:** Create a new ticket.  

**Parameters (form-data):**
- `acquisioner` → Email of the person who sent the message.  
- `transactioner` → Email of the person receiving the message.  
- `clientInfo` → First 500 characters of the email body.  

---

### 2. `POST /tickets/{id}/update`
**Purpose:** Update an existing ticket with an IBS number.  

**Path variable:**  
- `id` → UUID of the ticket to update.  

**Parameters (form-data):**  
- `ibsNumber` → The IBS number to associate with the ticket.  

---

### 3. `POST /send-tickets-email`
**Purpose:** Send a ticket summary email to a user for a given time period.  

**Parameters (form-data):**  
- `user` → Email address of the recipient.  
- `period` → Time range for the summary (e.g., `7d`, `30d`).  
