
'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.updateUser = functions.firestore
    .document('users/{userId}/{notify}')
    .onUpdate((change, context) => {
      // Get an object representing the document
      // e.g. {'name': 'Marie', 'age': 66}
      const newValue = change.after.data();
      // ...or the previous value before this update
      const previousValue = change.before.data();
      // access a particular field as you would any JS property
      const token = newValue.token;
      const userUID = context.params.uid;

      // perform desired operations ...
      if (previousValue.notify == False && newValue.notify == True){
            
      }
            
    

    // Get the list of device notification tokens.
          const getDeviceTokensPromise = admin.database()
              .ref(`/users/${userID}/token`).once('value');

          // Get the follower profile.
          const getFollowerProfilePromise = admin.auth().getUser(userUID);

          // The snapshot to the user's tokens.
          let tokensSnapshot;

          // The array containing all the user's tokens.
          let tokens;

          const results = await Promise.all([getDeviceTokensPromise, getFollowerProfilePromise]);
          tokensSnapshot = results[0];

     // Notification details.
          const payload = {
            notification: {
              title: 'New slot for Vaccination',
              body: `Hurry up and go to CoWin to book your new slot!`
            }

    // Listing all tokens as an array.
      tokens = Object.keys(tokensSnapshot.val());
      // Send notifications to all tokens.
      const response = await admin.messaging().sendToDevice(tokens, payload);
      // For each message check if there was an error.
      const tokensToRemove = [];
      response.results.forEach((result, index) => {
        const error = result.error;
        if (error) {
          functions.logger.error(
            'Failure sending notification to',
            tokens[index],
            error
          );
          // Cleanup the tokens who are not registered anymore.
          if (error.code === 'messaging/invalid-registration-token' ||
              error.code === 'messaging/registration-token-not-registered') {
            tokensToRemove.push(tokensSnapshot.ref.child(tokens[index]).remove());
          }
        }
      });
      return Promise.all(tokensToRemove);

});
