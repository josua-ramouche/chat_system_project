# Chat System project 

The objective of this project is to create a chatsystem application for users connected on the same network. Each user has a username, associated to an IP address. A user should be able to send messages to connected contacts, change his username, notify it to all connected contacts, disconnect and also notify it to all connected contacts in this case.


## How to run the program : 

- Make sure to reload the maven project (if possible on the tool you are using) in case you did not use the same dependencies.
  *(If the software you are using cannot handle a maven project, or if you want to use only a command prompt. Please refer to the next section)*
- Run the **Main.java** in src/main/java on two different computers connected on the same network.

--------------------------------------------------------------------------------------------------------------------------------------------
(The next steps must be done only if the software you are using cannot handle a maven project, or if you want to use only a command prompt)

-------Linux------

 -> On a linux machine (INSA computers for example), in a console, check if maven is installed : 
    
    mvn --version
    
 If it is not the case, download maven by copying the next lines :
    
    mkdir -p ~/bin  # create a bin/ directory in your home
    cd ~/bin  # jump to it
    wget https://dlcdn.apache.org/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz -O maven.tar.gz  # download maven
    tar xf maven.tar.gz  # decompress it
    echo 'export PATH=~/bin/apache-maven-3.9.5/bin:$PATH' >> ~/.bashrc  # add mvn's directory to the PATH
    source ~/.bashrc  # reload terminal configuration 
    
To compile the project, in the project's directory :  

    mvn compile
    
 -> To run the program : 
 
    mvn exec:java -Dexec.mainClass="Main"
  
------Windows------
  
  (If java is installed on your machine)
    
 -> To compile (in src/main/java) : 

    javac Main.java 
    
 -> To run the program : 
    
    java Main

----------------------------------------------------------------------------------------------------------------------------------
