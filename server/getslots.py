#!/usr/bin/env python3
#import pdb # debug
import sys
import time
from selenium import webdriver
from selenium.common.exceptions import NoSuchElementException
from xvfbwrapper import Xvfb

# Usage:
# > getslots.py    #226010 by default
# > getslots.py <pincode>
# > getslots.py <state> <district>

# NPI de sintaxis de clases en pyton..
class TimePlaceDoses:
    date = ''
    type = ''
    slots = ''

#        def __init__(doses, vaccineType):
#        self.doses = doses
#        self.vaccineType = vaccineType

class Center:
    name = ''
    addres = ''
    #doses = TimePlaceDoses()[]

#    def __init__(TimePlaceDoses[])

# Start Xvfb wrapper
# Comment to see what is doing in the browser
vdisplay = Xvfb()
vdisplay.start()

web = webdriver.Chrome()
web.get('https://www.cowin.gov.in/home')

time.sleep(0.5)

PinCode = "226010"
State = "Uttar Pradesh"
City = "Lucknow"
Mode = "pin"

if len(sys.argv) == 1:
    # Default: use pin.
    PinCode = "226010"

    WebPinTextBox = web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div/div/div/div[2]/form/div/div/div[2]/div/input')
    WebPinTextBox.send_keys(PinCode)

    WebSearchButton = web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div/div/div/div[2]/form/div/div/div[2]/div/div/button')
    WebSearchButton.click()

    Mode = "Pin"

elif len(sys.argv) == 2:
    # Search by pin code
    PinCode = sys.argv[1]

    WebPinTextBox = web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div/div/div/div[2]/form/div/div/div[2]/div/input')
    WebPinTextBox.send_keys(PinCode)

    WebSearchButton = web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div/div/div/div[2]/form/div/div/div[2]/div/div/button')
    WebSearchButton.click()

    Mode = "Pin"

elif len(sys.argv) == 3:
    # Search by district
    State = sys.argv[1]
    City = sys.argv[2]

    WebSerchModeSwitch = web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div/div/div/div[2]/form/div/div/div[1]/div/label/div')
    WebSerchModeSwitch.click()

    time.sleep(0.2)
    WebStateEntry = web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div[1]/div/div/div[2]/form/div/div/div[2]/div/div[1]/mat-form-field/div/div[1]/div/mat-select')
    WebStateEntry.send_keys(State)

    time.sleep(0.2)
    WebDistrict = web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div[1]/div/div/div[2]/form/div/div/div[2]/div/div[2]/mat-form-field/div/div[1]/div/mat-select')
    WebDistrict.send_keys(City)

    Mode = "District"

# Search
time.sleep(0.2)
if Mode == "Pin":
    WebSearchButton = web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div[1]/div/div/div[2]/form/div/div/div[2]/div/div/button')
else:
    WebSearchButton = web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div[1]/div/div/div[2]/form/div/div/div[2]/div/div[3]/button')
WebSearchButton.click()


# Filter of +18
time.sleep(0.5)
if Mode == "Pin":
    WebAge18filter = web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div/div/div/div[2]/form/div/div/div[4]/div/div[1]/label')
else:
    WebAge18filter = web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div[1]/div/div/div[2]/form/div/div/div[3]/div/div[1]/label')
WebAge18filter.click()

print()

Vaccines = Center()
print(type(Vaccines))

# Get info
Centers = []
while True:
    try:
        if Mode == "Pin":
            Centers.append(web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div/div/div/div[2]/form/div/div/div[10]/div/div/div/div[' + str(len(Centers)+1) + ']/div/div/div[1]/div/h5').text);
        else:
            Centers.append(web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div/div/div/div[2]/form/div/div/div[9]/div/div/div/div[' + str(len(Centers)+1) + ']/div/div/div[1]/div/h5').text);
        print(Centers[len(Centers)-1])

        DatesCarousel = []
        Availability = []
        VaccineType = []
        for d in range(6):

            # Get date
            if Mode == "Pin":
                DatesCarousel.append(web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div/div/div/div[2]/form/div/div/div[9]/div/div/ul/carousel/div/div/slide[' + str(d+1) + ']/div/li/a/p').text)
            else:
                DatesCarousel.append(web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div[1]/div/div/div[2]/form/div/div/div[8]/div/div/ul/carousel/div/div/slide[' + str(d+1) + ']/div/li/a/p').text)

            # Get doses
            if Mode == "Pin":
                Availability.append(web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div/div/div/div[2]/form/div/div/div[10]/div/div/div/div[' + str(len(Centers)) + ']/div/div/div[2]/ul/li[' + str(len(Availability)+1) + ']/div/div/a').text)
            else:
                Availability.append(web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div[1]/div/div/div[2]/form/div/div/div[9]/div/div/div/div[' + str(len(Centers)) + ']/div/div/div[2]/ul/li[' + str(len(Availability)+1) + ']/div/div/a').text)

            # Get vaccine type
            if Mode == "Pin":
                VaccineType.append(web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div/div/div/div[2]/form/div/div/div[10]/div/div/div/div[' + str(len(Centers)) + ']/div/div/div[2]/ul/li[' + str(len(VaccineType)+1) + ']/div/div/div[1]/h5').text)
            else:
                VaccineType.append(web.find_element_by_xpath('/html/body/app-root/div/app-home/div[2]/div/appointment-table/div/div/div/div/div/div/div/div/div/div/div[2]/form/div/div/div[9]/div/div/div/div[' + str(len(Centers)) + ']/div/div/div[2]/ul/li[' + str(len(VaccineType)+1) + ']/div/div/div[1]/h5').text)

        print(DatesCarousel)
        print(Availability)
        print(VaccineType)

    except NoSuchElementException as e:
        #print(e)
        break

vdisplay.stop()
print('Done :)')
#print(Centers)
