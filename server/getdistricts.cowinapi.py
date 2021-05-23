#!/usr/bin/env python3
#import pdb # debug
import requests
import time
import json

headers = {'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36'}

url = 'https://cdn-api.co-vin.in/api/v2/admin/location/states'
result = requests.get(url, headers=headers)
json_states = json.loads(result.text)

#print(json_states)

i = 0
for json_state in json_states["states"]:
    url = 'https://cdn-api.co-vin.in/api/v2/admin/location/districts/' + str(json_state["state_id"])
    result = requests.get(url, headers=headers)
    #print(result.text)
    json_districts = json.loads(result.text)
    json_state["districts"] = json_districts["districts"]
    time.sleep(0.1)
    i = i + 1
    print(i)

#print("------------------------------------")
print(json_states)
