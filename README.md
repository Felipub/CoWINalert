# CoWINalert
Tells you when there are free slots to get the COVID vaccine anywhere in India


In remote server:
I started with this kid: https://www.youtube.com/watch?v=YbGAUEjTKg4&t=822s

- Needs Python 3
- Install Firebase module for Python - https://pypi.org/project/python-firebase/
    $ sudo pip install requests==1.1.0
    $ sudo pip install python-firebase
- Install firebase_admin module for Python - https://firebase.google.com/docs/admin/setup#add-sdk
    $ sudo pip install firebase-admin
    maybe you need to intall these other thing to make it work:
    $ sudo pip install testresources
- Get the .json with the credentials from Firebase to run the script into the server diectory (or contact me for the file :)
- Add crontab job to execute server script every min -
    $crotab -e
    ...add this line
    * * * * *  date >> <path_to_log>/updatevaccines.err.log ; <path_to_script>/updatevaccines.py >> <path_to_log>/updatevaccines.log 2>> <path_to_log>/updatevaccines.err.log

Then attack!!
