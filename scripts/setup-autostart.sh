#!/bin/bash

# Setup script to configure auto-start on EC2 instance

echo "Setting up IPL Voting App auto-start..."

# Create app directory
sudo mkdir -p /home/ubuntu/ipl-voting-app

# Copy startup script
sudo cp start-app.sh /home/ubuntu/ipl-voting-app/
sudo chmod +x /home/ubuntu/ipl-voting-app/start-app.sh
sudo chown ubuntu:ubuntu /home/ubuntu/ipl-voting-app/start-app.sh

# Copy systemd service file
sudo cp ipl-voting-app.service /etc/systemd/system/

# Reload systemd and enable service
sudo systemctl daemon-reload
sudo systemctl enable ipl-voting-app.service

echo "Auto-start setup complete!"
echo "Your app will now start automatically when EC2 instance boots up."
echo ""
echo "Manual commands:"
echo "  Start:   sudo systemctl start ipl-voting-app"
echo "  Stop:    sudo systemctl stop ipl-voting-app"
echo "  Status:  sudo systemctl status ipl-voting-app"