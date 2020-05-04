import json
import pandas

with open("task1.json", 'r') as f:
    myResult = json.loads(f.read())
    # print(myResult)

with open("Google_Result1.json", 'r') as f:
    Google = json.loads(f.read())
    # print(Google)

# the overlapping calculation

def ifSimilar(str1, str2):
    str1 = str1.replace('https://','').replace('http://','').replace("www.","").strip('/').lower()
    str2 = str2.replace('https://','').replace('http://','').replace("www.","").strip('/').lower()
    if str1==str2:
        return True
    else:
        return False


result_dict = {'Queries':[], 'Number of Overlapping Results':[], 'Percent Overlap':[], 'Spearman Coefficient':[]}

for key in myResult:
    if key in Google.keys():
        print("good: ", key)
    else:
        print("error: ", key)

new_dict = {}
total = 0
result = 0
no = 1
for key in myResult:
    result_dict['Queries'].append('Queries ' + str(no))
    no += 1
    my = myResult[key]
    gle = Google[key]
    fenzi = 0
    cnt = 0
    for i in range(len(gle)):
        for j in range(len(my)):
            if ifSimilar(gle[i], my[j]):
                fenzi = fenzi+(i-j)*(i-j)
                cnt += 1

    if cnt==0:
        coe = 0
    else:
        if cnt==1:
            if fenzi==0:
                coe = 1
            else:
                coe = 0
        else:
            coe = 1 - (6*fenzi)/(cnt*(cnt*cnt-1))
    result+=coe
    total+=cnt
    result_dict['Number of Overlapping Results'].append(cnt)
    result_dict['Percent Overlap'].append(cnt*10)
    result_dict['Spearman Coefficient'].append(coe)
    print(key, cnt, cnt*10, coe)
print("total: ", total)
print("result: ", result)

result_dict['Queries'].append('Average')
result_dict['Number of Overlapping Results'].append(total/100)
result_dict['Percent Overlap'].append(total/10)
result_dict['Spearman Coefficient'].append(result/100)

df = pandas.DataFrame(result_dict)
df.to_csv('hw1_queries.csv', index=False)