#!/usr/bin/python3
#import pdb # debug
import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
from datetime import date, datetime
import requests
import json
import os.path
from os import path

#DELETE#FIRESTORE_DB_URL = ''

# Polls Firebase db in search of cowin table
def GetFirebaseRequests():
    #Initialize Cloud Firestore - https://firebase.google.com/docs/firestore/quickstart#python_2
    from firebase_admin import credentials
    from firebase_admin import firestore

    # Use a service account
    cred = credentials.Certificate('cowinalert-25025-5dc6b50e090a.json')
    firebase_admin.initialize_app(cred)

    db = firestore.client()
    cowin_requests = db.collection(u'cowin')
    fb_docs = cowin_requests.stream()

    #DEBUG#for doc in fb_docs:
    #DEBUG#    print(f'{doc.id} => {doc.to_dict()}')
    return fb_docs


def CowinApiRequestInfo(thing_id):
    headers = {'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36'}
    today = date.today().strftime("%d-%m-%Y")

    search_by = ""
    if (len(str(thing_id)) == 6):
        subdir = 'calendarByPin'
        search_by = 'pincode'
    elif (len(str(thing_id)) <= 3):
        subdir = 'calendarByDistrict'
        search_by = 'district_id'
    else:
        raise Exception("Unknown search token id.")

    url = 'https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/' + subdir + '?' + search_by + '=' + str(thing_id) + '&date=' + today
    #print(url)

    result = requests.get(url, headers=headers)
    #print(result.text)

    json_centers_doses = json.loads(result.text)
    #print(centers_doses)

    return json_centers_doses


# Return number of alerts submitted
#   0 for no alerts needed
#   -1 for Cowin Api error
#   -2 for Firebase error
def AlertNewVaccines(id, newJson):
    # Import lastJson if exist

    new_alerts = 0;
    # TODO: Compare both jsons centers doses

    # TODO: notify to Firebase db if there are new vaccines available

    return new_alerts



# Infinite loop

fbdocs_requests = GetFirebaseRequests()

for doc in fbdocs_requests:
    #print(f'{doc.id} => {doc.to_dict()}')
    thing_id = doc.id
    json_current_vaccines = CowinApiRequestInfo(thing_id)

    json_last_vaccines = {}
    dir_name = 'last_repertoire'
    if not path.isdir(dir_name):
        os.mkdir(dir_name)
        print('Repertories directory created: ' + dir_name)

    # Get last state of the district/pin
    file_path = dir_name + '/' + thing_id + '.json'
    if (path.exists(file_path)):
        with open(file_path) as json_file:
            try:
                json_last_vaccines = json.load(json_file)
            except json.decoder.JSONDecodeError as e:
                print(' !!! Could not import file ' + file_path)

    #print(' >>> LAST JSON FOR ' + thing_id + ': ' + str(json_last_vaccines))

    # Collate previous and current situations of vaccines
    alerts = 0;
    for current_center in json_current_vaccines['centers']:
        for last_center in json_last_vaccines['centers']:
            if (current_center['center_id'] == last_center['center_id']):
                #print('BINGO! ' + str(current_center['center_id']))
                for session_now in current_center['sessions']:
                    for session_last in last_center['sessions']:
                        if (session_now['date'] == session_last['date']):
                            new_vaccines = session_now['available_capacity_dose1'] - session_last['available_capacity_dose1']
                            if (new_vaccines > 0):
                                # ALERT of new vaccines!!
                                alerts = alerts + 1
                                now = datetime.now().strftime("%d/%m/%Y %H:%M:%S")
                                print('[' + now + '] >>> Found ' + str(new_vaccines) + ' vaccines in ' + current_center['name'] + ' on ' + session_now['date'])

                                #TODO: Notify to Firebase
                        break  #for of sessions

            break #for of centers

    now = datetime.now().strftime("%d/%m/%Y %H:%M:%S")
    print('[' + now + '] >>> ' + str(alerts) + ' new alerts set.')

    # Overwrite the previous json with current
    with open(file_path, 'w') as outfile:
        json.dump(json_current_vaccines, outfile)
