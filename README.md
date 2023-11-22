# Chat System project 

The objective of this project is to create a chatsystem application for users connected on the same network. Each user has a username, associated to an IP address. A user should be able to send messages to connected contacts, change his username and notify it to all connected contacts, and disconnect and notify it to all connected contacts.


## First milestone : Contact discovery 

Our first step on this project is to do a contact discovery : When a user connects, its username is sent to connected users to verify its unicity. If the first sent username is not unique, the user must change it. When the username is valid, every connected user adds our freshly connected user to their contact list. So does the new user with every other connected user. 

To test this functionnality : 
