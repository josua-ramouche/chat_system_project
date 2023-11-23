# Chat System project 

The objective of this project is to create a chatsystem application for users connected on the same network. Each user has a username, associated to an IP address. A user should be able to send messages to connected contacts, change his username, notify it to all connected contacts, disconnect and also notify it to all connected contacts in this case.


## First milestone : Contact discovery 

Our first step on this project is to do a contact discovery on the network : When a user connects, its username is sent to connected users on the same network to verify its unicity. If the first sent username is not unique, the user must change it. When the username is valid, every connected user adds our freshly connected user to their contact list. So does the new user with every other connected user. 

To test this functionnality : 
- Make sure to reload the maven project in case you did not use the same dependencies.
- Run the **UserContactDiscoveryController.java** main in src/main/java/Controller on two different computers connected on the same network.
- You are free to modify the inital username before launching the program (line 14) as well as the new username used to test the change of username after 3 seconds (line 38). 
- JUnit tests for our methoods can be found in **ClientContactDiscoveryControllerTest.java**, **ServerContactDiscoveryControllerTest.java** (both located in src/test/java/Controller) and **UserTest.java** (src/test/java/Model).
