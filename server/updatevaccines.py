#!/usr/bin/python3
#import pdb # debug
import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore


#DELETE#FIRESTORE_DB_URL = ''

def GetFirebaseRequests():
    #Initialize Cloud Firestore - https://firebase.google.com/docs/firestore/quickstart#python_2
    from firebase_admin import credentials
    from firebase_admin import firestore

    # Use a service account
    cred = credentials.Certificate('cowinalert-314812-45341a4bc1be.json')
    firebase_admin.initialize_app(cred)

    db = firestore.client()
    cowin_requests = db.collection(u'cowin')
    fb_docs = cowin_requests.stream()

    for doc in fb_docs:
        print(f'{doc.id} => {doc.to_dict()}')

    #fb = firebase.FirebaseApplication('https://cowinalert-25025.firebaseio.com', None)
    #result = fb.get('/cowin', None)
    #print(result)


def CowinApiPollDistrict():
    print('json')


GetFirebaseRequests()
