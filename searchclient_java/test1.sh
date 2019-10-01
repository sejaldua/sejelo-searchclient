#!/bin/bash

# LEVEL: SAD1
# java -jar ../server.jar -l ../levels/SAD1.lvl -c "java -Xmx8g searchclient.SearchClient" -g -s 150 -t 180 > ../optimization2/o2_SAD1_BFS.txt 2>&1
# java -jar ../server.jar -l ../levels/SAD1.lvl -c "java -Xmx8g searchclient.SearchClient -dfs" -g -s 150 -t 180 > ../optimization2/o2_SAD1_DFS.txt 2>&1

# # LEVEL: SAD2
# java -jar ../server.jar -l ../levels/SAD2.lvl -c "java -Xmx8g searchclient.SearchClient" -g -s 150 -t 180 > ../optimization2/o2_SAD2_BFS.txt 2>&1
# java -jar ../server.jar -l ../levels/SAD2.lvl -c "java -Xmx8g searchclient.SearchClient -dfs" -g -s 150 -t 180 > ../optimization2/o2_SAD2_DFS.txt 2>&1

# # LEVEL: SAfriendofDFS
# java -jar ../server.jar -l ../levels/SAfriendofDFS.lvl -c "java -Xmx8g searchclient.SearchClient" -g -s 150 -t 180 > ../optimization2/o2_SAfriendofDFS_BFS.txt 2>&1
# java -jar ../server.jar -l ../levels/SAfriendofDFS.lvl -c "java -Xmx8g searchclient.SearchClient -dfs" -g -s 150 -t 180 > ../optimization2/o2_SAfriendofDFS_DFS.txt 2>&1

# # LEVEL: SAfriendofBFS
# java -jar ../server.jar -l ../levels/SAfriendofBFS.lvl -c "java -Xmx8g searchclient.SearchClient" -g -s 150 -t 180 > ../optimization2/o2_SAfriendofBFS_BFS.txt 2>&1
# java -jar ../server.jar -l ../levels/SAfriendofBFS.lvl -c "java -Xmx8g searchclient.SearchClient -dfs" -g -s 150 -t 180 > ../optimization2/o2_SAfriendofBFS_DFS.txt 2>&1

# # LEVEL: SAFirefly
# java -jar ../server.jar -l ../levels/SAFirefly.lvl -c "java -Xmx8g searchclient.SearchClient" -g -s 150 -t 180 > ../optimization2/o2_SAFirefly_BFS.txt 2>&1
java -jar ../server.jar -l ../levels/SAFirefly.lvl -c "java -Xmx8g searchclient.SearchClient -dfs" -g -s 150 -t 180 > ../optimization2/o2_SAFirefly_DFS.txt 2>&1

# LEVEL: SACrunch
java -jar ../server.jar -l ../levels/SACrunch.lvl -c "java -Xmx8g searchclient.SearchClient" -g -s 150 -t 180 > ../optimization2/o2_SACrunch_BFS.txt 2>&1
# java -jar ../server.jar -l ../levels/SACrunch.lvl -c "java -Xmx8g searchclient.SearchClient -dfs" -g -s 150 -t 180 > ../optimization2/o2_SACrunch_DFS.txt 2>&1
