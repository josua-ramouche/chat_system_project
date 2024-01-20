# Java report : Making the Chat System App

## Tech stack

--------------------------------------------------------------------------------------------------------------------------------------------

In the process of making our Chat System application, we made use of several technologies.

- **Maven** : This is basically the plugin that manages the whole java project, from the code to the generation of a .jar file. We can add different dependencies, by modifying a project object model file. These dependencies permitted us to import different useful tools for implementation (such as database commands for example) and testing (JUnit).


- **UDP (User Datagram Protocol)** : This tech is necessary for the Contact Discovery process, to retrieve other users that are using the app on the same network, and also let them know that you are connecting. The asynchronous aspect of UDP is relevant here as we should be able to connect to our application without another person necessarily using the app.


- **TCP (Transmission Control Protocol)** : Here is what permits us to handle the main functionality of our app, sending and receiving messages to/from a specific user. TCP is needed here because we need a connection to be established (synchronous) so that we can read from a Buffered reader, and write on a PrintWriter, based on an active Socket. A server is running permanently in the background when the app is being used to accept incoming connections from other users.


- **SQLite** : This library allowed us to set up and use local databases. These databases are necessary to save users found in the contact discovery process in a “Users” table, and sent and received messages in a “Chat” table for each person we talked with on the app.


- **Swing** : This provides all the tools related to the creation of Graphical User Interfaces. We used it in the View part of our MVC (Model View Controller) project structure, that is our Login, ContactList, Chat and ChangeUsername interfaces.


- **JUnit** : This module gives us the means to test our code, in separate java files, by verifying assertions, and directly using methods we implemented. This is essential to make sure our code does what is expected, and also to set up the continuous integration.


- **Mockito/PowerMockito** : Also used in the test part of our project. These dependencies permit us to “mock” some of our classes, so that we don’t have to directly use a real class instance. For example, we can mock InetAddress and Socket classes instead of using actual instances of these classes as they could be inaccessible in other cases (during the continuous integration process for example).

## Testing Policy

--------------------------------------------------------------------------------------------------------------------------------------------

As mentioned before, we used **JUnit** to make our tests. Thus, we progressively made tests as we were coding. Tests were made for each method of the Model and Controller parts of our project.

For most of them, the results are based on **assertions** after the use of certain methods, to verify values of attributes or length of lists. Others, as some tested methods do not have an impact on specific attributes, only verify that no exceptions were thrown during the execution of the test.

We also had to check that our tests passed correctly, on our machines in the first place, but also on **Git** (the green check mark indicating that no errors occurred while executing the tests on continuous integration). That was initially not always the case as some variables were null when the tests were executed outside our IDE. That is when **Mockito** was used, to ensure that problematic tests have constant results no matter where they were executed from.

After we verified that our methods were correct for the Controller and Model part of the project, we tested our **GUIs**. We first tried with two users on the same network. To test with more users, we opened the app from other INSA computers using ssh.

Finally, during the whole process, we made sure to write the maximum information possible in the console, in order to be able to debug our code.
