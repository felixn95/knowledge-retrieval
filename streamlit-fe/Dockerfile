# Use a lightweight Python base image
FROM python:3.11-slim

# Set environment variables to prevent Python from writing .pyc files and buffering stdout/stderr
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

# Install system dependencies
RUN apt-get update && apt-get install -y \
    build-essential \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Set the working directory
WORKDIR /app

# Copy the requirements directly
COPY requirements.txt .

# Install Python dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Copy app code and static files (like the image)
COPY . .

# Expose Streamlit default port
EXPOSE 8501

# Run the app
CMD ["streamlit", "run", "app2.py", "--server.port=8501", "--server.address=0.0.0.0"]