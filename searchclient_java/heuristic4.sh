#!/bin/bash

# LEVEL: SAD1
java -jar ../server.jar -l ../levels/SAD1.lvl -c "java -Xmx8g searchclient.SearchClient -astar" -g -s 150 -t 180 > ../heuristic4/h4_SAD1_astar.txt 2>&1
java -jar ../server.jar -l ../levels/SAD1.lvl -c "java -Xmx8g searchclient.SearchClient -greedy" -g -s 150 -t 180 > ../heuristic4/h4_SAD1_greedy.txt 2>&1

# LEVEL: SAD2
java -jar ../server.jar -l ../levels/SAD2.lvl -c "java -Xmx8g searchclient.SearchClient -astar" -g -s 150 -t 180 > ../heuristic4/h4_SAD2_astar.txt 2>&1
java -jar ../server.jar -l ../levels/SAD2.lvl -c "java -Xmx8g searchclient.SearchClient -greedy" -g -s 150 -t 180 > ../heuristic4/h4_SAD2_greedy.txt 2>&1

# LEVEL: SAfriendofDFS
java -jar ../server.jar -l ../levels/SAfriendofDFS.lvl -c "java -Xmx8g searchclient.SearchClient -astar" -g -s 150 -t 180 > ../heuristic4/h4_SAfriendofDFS_astar.txt 2>&1
java -jar ../server.jar -l ../levels/SAfriendofDFS.lvl -c "java -Xmx8g searchclient.SearchClient -greedy" -g -s 150 -t 180 > ../heuristic4/h4_SAfriendofDFS_greedy.txt 2>&1

# LEVEL: SAfriendofBFS
java -jar ../server.jar -l ../levels/SAfriendofBFS.lvl -c "java -Xmx8g searchclient.SearchClient -astar" -g -s 150 -t 180 > ../heuristic4/h4_SAfriendofBFS_astar.txt 2>&1
java -jar ../server.jar -l ../levels/SAfriendofBFS.lvl -c "java -Xmx8g searchclient.SearchClient -greedy" -g -s 150 -t 180 > ../heuristic4/h4_SAfriendofBFS_greedy.txt 2>&1

# LEVEL: SAFirefly
java -jar ../server.jar -l ../levels/SAFirefly.lvl -c "java -Xmx8g searchclient.SearchClient -astar" -g -s 150 -t 180 > ../heuristic4/h4_SAFirefly_astar.txt 2>&1
java -jar ../server.jar -l ../levels/SAFirefly.lvl -c "java -Xmx8g searchclient.SearchClient -greedy" -g -s 150 -t 180 > ../heuristic4/h4_SAFirefly_greedy.txt 2>&1

# LEVEL: SACrunch
java -jar ../server.jar -l ../levels/SACrunch.lvl -c "java -Xmx8g searchclient.SearchClient -astar" -g -s 150 -t 180 > ../heuristic4/h4_SACrunch_astar.txt 2>&1
java -jar ../server.jar -l ../levels/SACrunch.lvl -c "java -Xmx8g searchclient.SearchClient -greedy" -g -s 150 -t 180 > ../heuristic4/h4_SACrunch_greedy.txt 2>&1

# LEVEL: SAsoko1_64
java -jar ../server.jar -l ../levels/SAsoko1_64.lvl -c "java -Xmx8g searchclient.SearchClient -greedy" -g -s 150 -t 180 > ../heuristic4/h4_SAsoko1_64_greedy.txt 2>&1

# LEVEL: SAsoko2_64
java -jar ../server.jar -l ../levels/SAsoko2_64.lvl -c "java -Xmx8g searchclient.SearchClient -greedy" -g -s 150 -t 180 > ../heuristic4/h4_SAsoko2_64_greedy.txt 2>&1

# LEVEL: SAsoko3_64
java -jar ../server.jar -l ../levels/SAsoko3_64.lvl -c "java -Xmx8g searchclient.SearchClient -greedy" -g -s 150 -t 180 > ../heuristic4/h4_SAsoko3_64_greedy.txt 2>&1
