#!/usr/bin/env python3
#import pdb # debug
import requests
import time

headers = {'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36'}

for i in range(20):
    url = 'https://cdn-api.co-vin.in/api/v2/admin/location/districts/' + str(i)
    result = requests.get(url, headers=headers)
    print(result.text)
    time.sleep(0.1)
