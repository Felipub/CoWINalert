#!/usr/bin/env python3
#import pdb # debug
import sys
import time
from selenium import webdriver
from selenium.common.exceptions import NoSuchElementException
from xvfbwrapper import Xvfb

# Usage:
# > getslots.py    #226010 by default
# > getslots.py <pin_code>
# > getslots.py <state> <district>

# NPI de sintaxis de clases en pyton..
class DosesAvailable:
    def __init__(self, date, brand, quantity):
        self.date = date
        self.brand = brand
        self.quantity = quantity

class Center:
    def __init__(self, name, address):
        self.name = name
        self.address = address
        self.vaccines = []

class Search:
    def __init__(self, search_mode, pin = '000000', state = 'NONE', district = 'NONE'):
        self.search_mode = search_mode
        self.pin_code = pin
        self.state = state
        self.district = district
        self.centers = []


# Start Xvfb wrapper
# Comment to see what is doing in the browser
vdisplay = Xvfb()
vdisplay.start()

web = webdriver.Chrome()
web.get('https://www.cowin.gov.in/home')

time.sleep(0.5)

pin_code = "226010"
state = "Uttar Pradesh"
district = "Lucknow"
search_mode = "pin"

if len(sys.argv) == 1:    # Default: use pin.
    search_mode = "pin"
    pin_code = "226010"
elif len(sys.argv) == 2:
    search_mode = "pin"
    pin_code = sys.argv[1]
elif len(sys.argv) == 3:
    search_mode = "district"
    state = sys.argv[1]
    district = sys.argv[2]

if search_mode == "pin":
    WebPinTextBox = web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div/div/div/form/mat-tab-group/div/mat-tab-body[1]/div/div[1]/div/input')
    WebPinTextBox.send_keys(pin_code)

elif search_mode == "district":
    WebSerchModeSwitch = web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div/div/div/form/mat-tab-group/mat-tab-header/div[2]/div/div/div[2]/div')
    WebSerchModeSwitch.click()

    time.sleep(0.2)
    WebStateEntry = web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div/div/div/form/mat-tab-group/div/mat-tab-body[2]/div/div/div[1]/mat-form-field/div/div[1]/div/mat-select')
    WebStateEntry.send_keys(state)

    time.sleep(0.2)
    WebDistrict = web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div/div/div/form/mat-tab-group/div/mat-tab-body[2]/div/div/div[2]/mat-form-field/div/div[1]/div/mat-select')
    WebDistrict.send_keys(district)

# Search
time.sleep(0.2)
if search_mode == "pin":
    WebSearchButton = web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div/div/div/form/mat-tab-group/div/mat-tab-body[1]/div/div[1]/div/div/button')
elif search_mode == "district":
    WebSearchButton = web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div/div/div/form/mat-tab-group/div/mat-tab-body[2]/div/div/div[3]/button')
WebSearchButton.click()

# Filter of +18
time.sleep(0.5)
WebAge18filter = web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div/div/div/div[2]/form/div/div/div[1]/div/div[1]/label')
WebAge18filter.click()

search = Search(search_mode, pin_code, state, district)

# Get info
while True:
    try:
        #Get CENTER NAME and ADDRESS
        center_name = web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div/div/div/div[2]/form/div/div/div[7]/div/div/div/div[' + str(len(search.centers)+1) + ']/div/div/div[1]/div/h5').text
        center_address = web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div/div/div/div[2]/form/div/div/div[7]/div/div/div/div[' + str(len(search.centers)+1) + ']/div/div/div[1]/div/p').text
        search.centers.append(Center(center_name, center_address))

        #print(search.centers[len(search.centers)-1].name)

        date = ''
        doses = ''
        brand = ''
        for d in range(6):
            # Get VACCINE TYPE
            brand = web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div/div/div/div[2]/form/div/div/div[7]/div/div/div/div[' + str(len(search.centers)) + ']/div/div/div[2]/ul/li[' + str(d+1) + ']/div/div/div[1]/h5').text
            brand = web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div/div/div/div[2]/form/div/div/div[7]/div/div/div/div[' + str(len(search.centers)) + ']/div/div/div[2]/ul/li[' + str(d+1) + ']/div/div/div[1]/h5').text

            if (brand == ''): continue

            # Get DATE
            date = web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div/div/div/div[2]/form/div/div/div[6]/div/div/ul/carousel/div/div/slide[' + str(d+1) + ']/div/li/a/p').text

            # Get DOSES
            doses = web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div/div/div/div[2]/form/div/div/div[7]/div/div/div/div[' + str(len(search.centers)) + ']/div/div/div[2]/ul/li[' + str(d+1) + ']/div/div/div[2]/span[1]').text

            search.centers[len(search.centers)-1].vaccines.append(DosesAvailable(date, brand, doses))
            print('.')  #impatient soother

        #print()

    except NoSuchElementException as e:
        #print(e)
        break

for center in search.centers:
    print('Center: ' + center.name + ' in ' + center.address)

    for doses in center.vaccines:
        print('\tDate: ' + doses.date)
        print('\tBrand: ' + doses.brand)
        print('\tQuantity: ' + doses.quantity)
        print()

    #print()

web.close()
vdisplay.stop()
print('Done :)')
