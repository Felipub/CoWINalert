#!/usr/bin/env python3
#import pdb # debug
import requests
from datetime import date
import cgi
import sys


today = date.today().strftime("%d-%m-20%Y")

pin_code =
state =
district_id =

# How is the script called
if (len(sys.arg) > 1 ) # From command line



fs = cgi.FieldStorage()

print("Content-type: text/plain\r\n\r\n")
print('KOALA NACIONAL')


print(fs["district_id"].value)

for key in fs.keys():
    print(key + ' = ' + fs[key].value)
#    print "%s = %s" % (key, fs[key].value)

#print( "Content-Type: text/plain\r\n\r\n" )

------------

headers = {'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36'}
#dd-mm-yyyy

url_pin = 'https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByPin?pincode=' + pin_code + '&date=' + today

url_district = 'https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByDistrict?district_id=' + district_id + '&date=' + today


result = requests.get(url, headers=headers)
print(result.text)

# Use time between request not to ofuscate the server
time.sleep(0.1)



JSON OF DATABASE MODEL
{
	"distritos pedidos": [
    "c1_9284yt3gh" : {
    	"distrito" : 713
      "last_time" : "2021-05-23_00:02"
    },
    "c2_3534535" : {
    	"distrito" : 556
      "last_time" : "2021-05-23_02:02"
    },
}
