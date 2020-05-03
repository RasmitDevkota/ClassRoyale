const firebase = require('firebase');
const functions = require('firebase-functions');

firebase.initializeApp({
    apiKey: "AIzaSyDMq0mi1Se1KXRyqaIwVZnv1csYshtrgu0",
    authDomain: "coronavirusbot19.firebaseapp.com",
    databaseURL: "https://coronavirusbot19.firebaseio.com",
    projectId: "coronavirusbot19",
    storageBucket: "coronavirusbot19.appspot.com",
    messagingSenderId: "814043085257",
    appId: "1:814043085257:web:d4151d18cb5d4a16ca1018",
    measurementId: "G-4TKZD7504L"
});

var db = firebase.firestore();
var games = db.collection('games');

exports.userJoin = functions.firestore.document('games/{gameid}').onUpdate((change, context) => {
    var gameId = context.params.gameid;
    games.doc(gameDocId).get().then(function (doc) {
        var d = new Date();
        var queue = doc.data().queue;

        for (uid in queue) {
            var seconds = queue[uid].seconds * 1000;
            if (seconds < d) {
                var acceptedUser = uid;
            }
        }

        users.doc(acceptedUser).get().then(function (doc) {
            var name = doc.data().displayName;
        });

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
