Pre-Compilation steps: 
  Make sure the PeerClientServer has the correct IP of the current RS. 

Compilation steps: 

Eclipse: 
  If one already has a recent version of Eclipse, load this folder into Eclipse and run RegistrationServer 
  and then PeerClientServer. Eclipse handles the classpath compilation. 

Command line / terminal:  
  While within 401_proj1/src - run: 
    javac processes/RegistrationServer.java 
    javac processes/PeerClientServer.java 
    java processes.RegistrationServer 
    java processes.PeerClientServer 
  
  The 'javac' command compiles the .java files into .class files, then 'java' runs the programs. 