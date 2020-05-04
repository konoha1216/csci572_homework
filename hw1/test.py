from bs4 import BeautifulSoup
import time
import requests
from random import randint
import json
from html.parser import HTMLParser


USER_AGENT = {'User-Agent':'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36'}
class SearchEngine:
 @staticmethod
 def search(query, sleep=True):
    if sleep: # Prevents loading too many pages too soon
        time.sleep(randint(5,10))
    temp_url = '+'.join(query.split()) #for adding + between words for the query
    url = 'http://www.bing.com/search?q=' + temp_url + '&count=30'
    soup = BeautifulSoup(requests.get(url, headers=USER_AGENT).text,"html.parser")
    new_results = SearchEngine.scrape_search_result(soup)

    # print(new_results, len(new_results))
    if len(new_results)>10:
        new_results = new_results[:10]
    return new_results
 @staticmethod
 def scrape_search_result(soup):
    raw_results = soup.find_all("li", "b_algo")
    results = []
    duplicate = 0
    #implement a check to get only 10 results and also check that URLs must not be duplicated
    for result in raw_results:
        link = result.find('a').get('href')
        if link in results:
            duplicate = 1
        else:
            results.append(link)
    results.append(duplicate)
    return results
#############Driver code############

# SearchEngine.search("Which phase is the non dividing stage ?")

file = open("100QueriesSet1.txt")
lines = file.readlines()
my_dict = {}
count = 0
for row in lines:
    row = row.strip().strip(" ?")
    cur = SearchEngine.search(row)
    my_dict[row]=cur
    count+=1
    print(count, row, len(cur), cur[0] if len(cur)>1 else "zero")
with open("task1.json","w") as f:
    json.dump(my_dict,f)
####################################