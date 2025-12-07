#!/bin/bash

# IPL Voting App Startup Script
set -e

# ECR Login
aws ecr get-login-password --region $AWS_DEFAULT_REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com

# Pull latest image
docker pull $AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com/$ECR_REPO_NAME:latest

# Stop and remove existing container
docker stop ipl-voting-app || true
docker rm ipl-voting-app || true

# Start new container
docker run -d --name ipl-voting-app -p 8080:8080 \
  --restart unless-stopped \
  -e DATASOURCE_URL="$DATASOURCE_URL" \
  -e DATASOURCE_USER="$DATASOURCE_USER" \
  -e DATASOURCE_PASSWORD="$DATASOURCE_PASSWORD" \
  -e FRONTEND_URL="$FRONTEND_URL" \
  -e JWT_SECRET="$JWT_SECRET" \
  $AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com/$ECR_REPO_NAME:latest

echo "IPL Voting App started successfully"