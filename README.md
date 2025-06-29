Componenti del gruppo: Fidel Guri (10840243), Claudio De Blasio (10854381), Andrea Giovanelli (10830176)  e Matteo Enut (10828090)

Funzionalità implementate: Regole complete, CLI + GUI, funzionalità avanzata: disconnessione

Istruzioni su come eseguire il progetto dai JAR: 
Aprire il terminale, 
per runnare il server, eseguire: java -jar folder_path/server-jar.jar
per runnare il client, eseguire: java -jar folder_path/cli-jar.jar
per runnare la gui, esehuire: java -jar folder_path/gui-jar.jar

Percentuale di copertura dei test: 
it.polimi.ingsw      50% (70/...)   29% (301/...)   24% (161/...)   24% (735/...)
├── controller        27% (13/...)   25% (74/...)    23% (452/...)   19% (167/...)
├── gui               0%  (0/18)     0%  (0/245)     0%  (0/13...)   0%  (0/541)
├── model             90% (5/...)    67% (225/...)   80% (107/...)   73% (568/...)
│   ├── Rmi           0%  (0/3)      0%  (0/88)      0%  (0/978)     0%  (0/495)
│   ├── Server        100% (1/1)     100% (2/2)      100% (85/85)    100% (0/0)
│   ├── Socket        0%  (0/7)      0%  (0/43)      0%  (0/800)     0%  (0/389)
├── view              100% (0/0)     100% (0/0)      100% (0/0)      100% (0/0)
