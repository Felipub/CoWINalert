#!/usr/bin/python3
import pdb # debug
import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
from datetime import date, datetime
import requests
import json
import os.path
from os import path

#Globals
alerts = 0;

class FirebaseDB:
    def __init__(self):
        #Initialize Cloud Firestore - https://firebase.google.com/docs/firestore/quickstart#python_2
        from firebase_admin import credentials
        from firebase_admin import firestore

        # Use a service account
        cred = credentials.Certificate('cowinalert-25025-5dc6b50e090a.json')
        firebase_admin.initialize_app(cred)

        self.db = firestore.client()


    # Polls Firebase db in search of cowin table
    def GetRequests(self):
        cowin_requests = self.db.collection(u'cowin')
        fb_docs = cowin_requests.stream()

        #DEBUG#for doc in fb_docs:
        #DEBUG#    print(f'{doc.id} => {doc.to_dict()}')
        return fb_docs.to_dict()

    def GetUsers(self):
        fb_users = self.db.collection(u'users')
        fb_docs = fb_users.stream()

        dict_users = {}
        for doc in fb_docs:
            #print(f'{doc.id} => {doc.to_dict()}')
            dict_users[doc.id] = doc.to_dict()

        return dict_users

    def SendAlert(self, user):
        doc_user = self.db.collection(u'users').document(u''+user['uid']+'')
        doc_user.set({u'notify' : True}, merge=True)
        print('   - Alarm notification sent to user: ' + user['uid'])

def GetRequestsFromUsers(dict_users):
    requests = {}
    for user in dict_users.values():
        #DELETE#user_data = doc.to_dict()
        print(user)
        print()
        requests[user['district']] = ''
        requests[user['pin']] = ''

    #print(requests)
    return requests


def CowinApiRequestInfo(request_id):
    headers = {'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36'}
    today = date.today().strftime("%d-%m-%Y")

    search_by = ""
    if (len(str(request_id)) == 6):
        subdir = 'calendarByPin'
        search_by = 'pincode'
    elif (len(str(request_id)) <= 3):
        subdir = 'calendarByDistrict'
        search_by = 'district_id'
    else:
        raise Exception("Unknown search token id.")

    url = 'https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/' + subdir + '?' + search_by + '=' + str(request_id) + '&date=' + today
    #print(url)

    result = requests.get(url, headers=headers)
    #print(result.text)

    json_centers_doses = json.loads(result.text)
    #print(centers_doses)

    return json_centers_doses

def AlertNewVaccinesToUsers(req_id, center, session, new_doses_1, new_doses_2, dict_users, fb_db):
    global alerts
    now = datetime.now().strftime("%d/%m/%Y %H:%M:%S")
    print('[' + now + '] >>> ' + str(req_id) + ': Found ' + str(new_doses_1 + new_doses_2) + ' new doses in ' + center['name'] + ' on ' + session['date'])

    #print(fbdocs_users)

    for user in dict_users.values():

        if (user['district'] == req_id or user['pin'] == req_id):

            if ( (user['covishield'] and session['vaccine'] == 'COVISHIELD')
                or (user['sputnikv'] and session['vaccine'] == 'SPUTNIKV') # TO CONFIRM!!
                or  (user['covaxin'] and session['vaccine'] == 'COVAXIN')):

                if (   (user['free'] and center['fee_type'] == 'Free')
                    or (user['paid'] and center['fee_type'] == 'Paid')):

                    if (    (user['plus18'] and session['min_age_limit'] == 18)
                        or  (user['plus45'] and session['min_age_limit'] == 45)):

                        if (   (user['dose1'] and new_doses_1 > 0)
                            or (user['dose2'] and new_doses_2 > 0)):

                            fb_db.SendAlert(user)
                            alerts = alerts + 1

    return 0


# MAIN

FB_DB = FirebaseDB()

#dict_requests = FB_DB.GetRequests() #using cowin table/collection - cannot use trigger functions at the moment
users = FB_DB.GetUsers()
dict_requests = GetRequestsFromUsers(users)

for request_id in dict_requests:
    #print(f'{doc.id} => {doc.to_dict()}')
    if (request_id == -1): continue

    json_current_vaccines = CowinApiRequestInfo(request_id)

    json_last_vaccines = {}
    dir_name = 'last_repertoire'
    if not path.isdir(dir_name):
        os.mkdir(dir_name)
        print('Repertories directory created: ' + dir_name)

    # Get last state of the district/pin
    file_path = dir_name + '/' + str(request_id) + '.json'
    if (path.exists(file_path)):
        with open(file_path) as json_file:
            try:
                json_last_vaccines = json.load(json_file)
            except json.decoder.JSONDecodeError as e:
                print(' !!! Could not import file ' + file_path)
    else:
        json_last_vaccines['centers'] = ''

    #print(' >>> LAST JSON FOR ' + str(request_id) + ': ' + str(json_last_vaccines))

    # Collate previous and current situations of vaccines
    alerts = 0;
    for current_center in json_current_vaccines['centers']:
        center_found = False
        for last_center in json_last_vaccines['centers']:
            if (current_center['center_id'] == last_center['center_id']):
                #print('BINGO! ' + str(current_center['center_id']))
                center_found = True
                for session_now in current_center['sessions']:
                    session_found = False
                    for session_last in last_center['sessions']:
                        if (session_now['session_id'] == session_last['session_id']):
                            #print('BINGO!! session found ' + str(session_now['date']))
                            session_found = True;
                            new_doses_1_in_session = session_now['available_capacity_dose1'] - session_last['available_capacity_dose1']
                            new_doses_2_in_session = session_now['available_capacity_dose2'] - session_last['available_capacity_dose2']

                            if (new_doses_1_in_session > 0 or new_doses_2_in_session  > 0):
                                AlertNewVaccinesToUsers(request_id, current_center, session_now, new_doses_1_in_session, new_doses_2_in_session, users, FB_DB)
                            break  #for of sessions

                    if not session_found:
                        # report all vaccines in the session
                        new_doses_1_in_session = session_now['available_capacity_dose1']
                        new_doses_2_in_session = session_now['available_capacity_dose2']

                        if (new_doses_1_in_session > 0 or new_doses_2_in_session  > 0):
                            AlertNewVaccinesToUsers(request_id, current_center, session_now, new_doses_1_in_session, new_doses_2_in_session, users, FB_DB)

                break #for of centers

    now = datetime.now().strftime("%d/%m/%Y %H:%M:%S")
    print('[' + now + '] >>> ' + str(alerts) + ' new alerts set for pin/district ' + str(request_id) + '.')

    # Overwrite the previous json with current
    with open(file_path, 'w') as outfile:
        json.dump(json_current_vaccines, outfile)

''' TRASH CODE
Duplicate Firebase document:
doc_user = FB_DB.db.collection(u'users').document(u'j9TppJgtZSbURxiuFWCjUDr8cBC2').get().to_dict()
doc_user2 = FB_DB.db.collection(u'users').document(u'eDPfonMUbxUX35rUtxkL5JeSXUx1').get()
dict_user2 = doc_user.copy()
FB_DB.db.collection(u'users').document(u'eDPfonMUbxUX35rUtxkL5JeSXUx1').set(dict_user2, merge=True)
'''
