const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

// const db = admin.firestore();
exports.writeToFirestore = functions.firestore
    .document("users/{userId}")
    .onUpdate((snap, context) => {
      const newValues = snap.after.data();
      const previousValues = snap.before.data();
      // const userUID = context.params.userId;
      const token = newValues.token;
      if ((newValues.notify == true) && (previousValues.notify == false)) {
        // db.doc("example/hostia").set({name: "pringao"});
        // Notification details.
        const message = {
          notification: {
            title: "Covaccine",
            body: "Hurry up! Go to Cowin to get your slot!!!",
          },
        };
        // Send notifications to all tokens.
        admin.messaging().sendToDevice(token, message);
      }
    });
