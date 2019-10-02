#!/bin/bash

java -jar ../server.jar -l ../levels/SAlabyrinth.lvl -c "java -Xmx8g searchclient.SearchClient" -g -s 150 -t 180 > ../other_levels/SAlabyrinth_BFS.txt 2>&1

java -jar ../server.jar -l ../levels/SAlabyrinth.lvl -c "java -Xmx8g searchclient.SearchClient -dfs" -g -s 150 -t 180 > ../other_levels/SAlabyrinth_DFS.txt 2>&1

java -jar ../server.jar -l ../levels/SAlabyrinthOfStBertin.lvl -c "java -Xmx8g searchclient.SearchClient" -g -s 150 -t 180 > ../other_levels/SAlabyrinthOfStBertin_BFS.txt 2>&1

java -jar ../server.jar -l ../levels/SAlabyrinthOfStBertin.lvl -c "java -Xmx8g searchclient.SearchClient -dfs" -g -s 150 -t 180 > ../other_levels/SAlabyrinthOfStBertin_DFS.txt 2>&1

java -jar ../server.jar -l ../levels/SAmicromousecontest2011.lvl -c "java -Xmx8g searchclient.SearchClient" -g -s 150 -t 180 > ../other_levels/SAmicromousecontest2011_BFS.txt 2>&1

java -jar ../server.jar -l ../levels/SAmicromousecontest2011.lvl -c "java -Xmx8g searchclient.SearchClient -dfs" -g -s 150 -t 180 > ../other_levels/SAmicromousecontest2011_DFS.txt 2>&1

