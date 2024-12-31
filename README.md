Project Description: IPL Voting Management System
Overview:
The IPL Voting Management System is a web application designed to manage and facilitate voting for IPL (Indian Premier League) matches. The system allows users to register, vote for their favorite teams in various matches, and ultimately determine the winning teams. It also includes functionality for setting match winners and storing vote details.

Key Features:
User Authentication and Management:

Users can register and log in to the system.

Authentication is managed using JWT (JSON Web Tokens) for secure access.

User details are stored and managed in the system.

Match Management:

The system stores information about IPL matches, including teams and match dates.

Users can view match details and vote for their preferred teams.

Voting System:

Users can cast votes for their favorite teams in each match.

The system ensures that each user can vote only once per match, enforcing unique constraints on votes.

Winner Management:

Administrators can set the winning team for each match.

The system tracks and stores winner information and the users who voted for the winning team.

CORS Configuration:

The application allows cross-origin requests from specified origins to facilitate frontend-backend communication.

Security Configuration:

The application uses Spring Security to manage authentication and authorization.

It includes a custom JWT filter to verify tokens and secure endpoints.

Summary:
Your IPL Voting Management System is a comprehensive application that manages user authentication, match details, voting, and winner management. It leverages Spring Boot's capabilities for secure and efficient handling of data and processes, ensuring a seamless user experience.
