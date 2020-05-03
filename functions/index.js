const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

var db = admin.firestore();

var games = db.collection('games');
var users = db.collection('users');

exports.userJoin = functions.firestore.document('games/{gameid}').onUpdate((change, context) => {
    var gameDocId = context.params.gameid;
    games.doc(gameDocId).get().then(function (doc) {
        var d = new Date();
        var queue = doc.data().queue;

        if (!queue) {
            return;
        }

        for (uid in queue) {
            var seconds = queue[uid].seconds * 1000;
            if (seconds < d) {
                var acceptedUser = uid;
            }
        }
        
        users.doc(uid).get().then(function (doc) {
            var name = doc.data().displayName;

            games.doc(gameDocId).update({
                queue: firebase.firestore.FieldValue.delete(),
                status: "CHOSEN",
                "user2.uid": acceptedUser,
                "user2.name": name
            }); 
        }).catch(function(e));
    });
});

exports.eventLogger = functions.database.ref('/games/{gameid}/{player}/{eventid}').onCreate((snapshot, context) => {
  var gameId = context.params.gameid;
  var uid = context.params.player;
  var eventId = context.params.eventid

  var eventData = snapshot.val();
  var eventFunction = eventData.eventFunction;

  switch (eventData) {
      case "placeCard":

      break;
  }

  var gameDoc = games.doc(gameId).collection('events').doc(eventId).update({

  }).then(function () {

  });
});
