<img width="4000" height="2431" alt="image" src="https://github.com/user-attachments/assets/ae2ba261-e391-4dfe-ac47-9f9cb9809922" />

# Galaxy Trucker - Software Engineering Project 2024/5
Multiplayer game programmed in Java as a project for Software Engineering class 2024/5 at Politecnico di Milano, according to the following **rules** [(EN)](galaxy-trucker-rules-en.pdf) and **requirements** [(IT)](requirements.pdf).

The game was developed in **Java**, along other technologies such as **Java RMI** for networking, **JavaFX** for the GUI, **Mockito** for testing and **SceneBuilder** for GUI sketching.

Group members: 
- Fidel Guri (10840243)
- Claudio De Blasio (10854381)
- Andrea Giovanelli (10830176)
- Matteo Enut (10828090).

## Description

Functionalities implemented:
- complete ruleset
- CLI client
- GUI client

Advanced functionalities implemented:
- disconnection handling


## Try it out
Instructions to run the project via the jar files:
- Download the [jar files](deliverables/final/jar)
- Download the JavaFX SDK for running the GUI client *(JavaFX is not part of the standard JDK anymore)*
- Navigate to the folder
- On the host: run the server jar
	```bash
	java -jar server-jar.jar
	```
- On the clients: run the client jar
	```bash
	java -jar cli-jar.jar
	```
	or
	```bash
	java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar gui-jar.jar
	```

