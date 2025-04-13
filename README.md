# knowledge-retrieval

Thesis Project Master of Science Uni Würzburg.

The module leverages retrieval augmented generation techniques to retrieve relevant context from documents by natural language queries. The approach is based on Quarkus, AzureOpenAI, and the pgvector extension for PostgreSQL.

To run the retrieval E2E, place an .env file with your respective AZURE keys in the server repo (https://github.com/felixn95/knowledge-retrieval/blob/main/server/.env).

## Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed.
- Git installed for cloning the repository.
- Place an .env file in the root of the server folder with the following Azure OpenAI keys

## Overview

This project uses Docker Compose to orchestrate three main services:

- **PostgreSQL with pgvector**: Stores document data and conversation sessions.
- **pgAdmin**: Provides a web interface for managing the database.
- **Quarkus Backend**: The application server processing chat requests and leverages retrieval techniques.

The repository contains SQL scripts (`dump.sql` in the `postgres` folder) to initialize the database schema and seed data. These scripts are executed only when the database volume is empty.

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/knowledge-retrieval.git
cd knowledge-retrieval
```

### 2. Start the Services
Located in the root directory of the repository, execute the following command:
```bash
docker-compose up -d
```
