import networkx as nx

fh = open('edgeLists.txt', 'rb')
G = nx.read_edgelist(fh)
fh.close()

pr = nx.pagerank(G, alpha=0.85, personalization=None, max_iter=30)

f = open('PRvalue.txt', 'w')
for key,value in pr.items():
    f.write('/home/konoha/Desktop/shared/aaa/NYTIMES/nytimes/'+str(key)+"="+str(value))
    f.write('\n')
f.close()