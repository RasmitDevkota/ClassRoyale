const functions = require('firebase-functions');

firebase.initializeApp({
    apiKey: "AIzaSyDz4E7cWDIMedScuc4TvGdEawhWGOLn4SQ",
    authDomain: "class-royale.firebaseapp.com",
    databaseURL: "https://class-royale.firebaseio.com",
    projectId: "class-royale",
    storageBucket: "class-royale.appspot.com",
    messagingSenderId: "670502096879",
    appId: "1:670502096879:web:d06e9f32378e0296fbf01a",
    measurementId: "G-F4CZ8W9P2B"
});

var db = firebase.firestore();
var games = db.collection('games');

function getDisplayName(uid) {
    users.doc(uid).get().then(function (doc) {
        var name = doc.data().displayName;
        return name;
    });
    return name;
}

exports.userJoin = functions.firestore.document('games/{gameid}').onUpdate((change, context) => {
    var gameDocId = context.params.gameid;
    games.doc(gameDocId).get().then(function (doc) {
        var d = new Date();
        var queue = doc.data().queue;

        for (uid in queue) {
            var seconds = queue[uid].seconds * 1000;
            if (seconds < d) {
                var acceptedUser = uid;
            }
        }

        var name = getDisplayName(acceptedUser);

        games.doc(gameDocId).update({
            queue: firebase.firestore.FieldValue.delete(),
            status: "CHOSEN",
            user2: {
                uid: acceptedUser,
                name: name
            }
        });
    });
});

exports.eventLogger = functions.database.ref('/games/{gameid}/{player}/{eventid}').onCreate((snapshot, context) => {
  var gameId = context.params.gameid;
  var uid = context.params.player;

  var eventData = snapshot.val();
  var eventFunction = eventData.eventFunction;

  switch (eventData) {
      case "placeCard":
        
      break;
  }

  var gameDoc = games.doc(gameId).collection(`$`).update({

  }).then(function () {

  });
});