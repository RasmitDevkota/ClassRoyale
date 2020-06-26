firebase.initializeApp({
    apiKey: "AIzaSyDz4E7cWDIMedScuc4TvGdEawhWGOLn4SQ",
    authDomain: "class-royale.firebaseapp.com",
    databaseURL: "https://coronavirusbot19.firebaseio.com/",
    projectId: "class-royale",
    storageBucket: "class-royale.appspot.com",
    messagingSenderId: "670502096879",
    appId: "1:670502096879:web:d06e9f32378e0296fbf01a",
    measurementId: "G-F4CZ8W9P2B"
});

var db = firebase.firestore();
var cards = db.collection("cards").doc("cards");
var games = db.collection("games");
var users = db.collection("users");
var emails = db.collection("emails");

var storage = firebase.storage().ref();
var cardThumbnails = storage.child('card_thumbnails');

var queues = firebase.database().ref('queues');

function addCard() {
    var name = document.getElementById("cardName").value;
    var description = document.getElementById("cardDescription").value;
    var type = document.getElementById("cardType").value;
    var rarity = document.getElementById("cardRarity").value;
    var thumbnail = document.getElementById("cardThumbnail").files[0];

    if (!name || !type || !description || !rarity || !thumbnail) {
        alert ("You forgot to enter an input for something! Every field is required!");
        return console.log("Not enough parameters given.");
    }

    var seed = Math.round(Math.random());
    var HPValues = [100, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600, 600];
    var attackDamageValues = [30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 80];
    var seedOffsets = new Map([
        ['Common', 0],
        ['Normal', 2],
        ['Rare', 4],
        ['Ultra Rare', 6],
        ['Legendary', 8],
        ['Mythical', 10]
    ]);

    var seedOffset = seedOffsets.get(rarity);

    var HP = HPValues[seed + seedOffset];
    if (["Person", "Normal"].includes(type)) {
        var attackDamage = attackDamageValues[seed + seedOffset];
    } else {
        var attackDamage = null;
    }

    console.log(`${name}, ${HP}, ${type}, ${description}, ${rarity}, 1, ${attackDamage}, 0, 1000`);

    cards.collection(type.toLowerCase()).doc(name).get().then(function (doc) {
        if (!doc.exists) {
            if (["Person", "Normal"].includes(type)) {
                cards.collection(type.toLowerCase()).doc(name).set({
                    name: name,
                    HP: HP,
                    type: type,
                    description: description,
                    rarity: rarity,
                    level: 1,
                    attackDamage: attackDamage,
                    XP: 0,
                    xpToLevelUp: 1000
                }).then(function () {
                    var storageRef = firebase.storage().ref('card_thumbnails/' + name);
                    storageRef.put(thumbnail).then(function (snapshot) {
                        console.log('Uploaded file ' + thumbnail.name + '!');
                    });

                    console.log(`Successfully added card ${name} of type ${type}!`);
                    return alert(`Successfully added card ${name} of type ${type}!`);
                }).catch(function (e) {
                    console.log(e);
                    return alert("Error occurred! Please contact a developer!");
                });
            } else {
                cards.collection(type.toLowerCase()).doc(name).set({
                    name: name,
                    HP: HP,
                    type: type,
                    description: description,
                    rarity: rarity,
                    level: 1,
                    XP: 0,
                    xpToLevelUp: 1000
                }).then(function () {
                    var storageRef = firebase.storage().ref('card_thumbnails/' + name);
                    storageRef.put(thumbnail).then(function (snapshot) {
                        console.log('Uploaded file ' + thumbnail.name + '!');
                    });

                    console.log(`Successfully added card ${name} of type ${type}!`);
                    return alert(`Successfully added card ${name} of type ${type}!`);
                }).catch(function (e) {
                    console.log(e);
                    return alert("Error occurred! Please contact a developer!");
                });
            }
        } else {
            console.log(`Card ${name} of type ${type} already exists.`);
            return alert(`Card ${name} of type ${type} already exists. Please try something else!`);
        }
    }).catch(function (e) {
        console.log(e);
        return alert("Error occurred! Please contact a developer!");
    });
}

function testGame(name1, name2) {
    emails.doc(name1).get().then(function (doc) {
        var uid = doc.data().uid;

        var gameDoc = games.doc();
        gameDoc.set({
            status: "PENDING",
            "user1": {
                "uid": uid,
                "name": name1
            }
        }).then(function () {
            console.log(`User ${name1} with uid ${uid} created game ${gameDoc.id}.`);
        });

        return gameDoc.id;
    }).then(function (gameId) {
        emails.doc(name2).get().then(function (doc) {
            var uid = doc.data().uid;

            games.doc(gameId).update({
                status: "CHECKING",
                ["queue." + uid]: firebase.firestore.FieldValue.serverTimestamp()
            }).then(function () {
                var seconds = firebase.firestore.FieldValue.serverTimestamp().seconds * 1000;
                console.log(`User ${name2} with uid ${uid} joined queue for game ${gameId} at ${seconds}.`);
            });
        });
    });
}

function startGame(name) {
    var queue = queues.child(name);

    queue.set({
        "status": "PENDING",
        "user1": name
    }).then(function () {
        console.log("Queue Created");
        queue.on('child_added', function (data) {
            if (data.val(). != name) {
                console.log('User found!');
                return console.log("startGame", data.key, data.val());
            } else {
                console.log(data.val().user1)
            }
        });
    });
}

function joinGame(name) {
    queues.orderByChild("status").equalTo("PENDING").limitToFirst(1).once('value', function (snapshot) {
        var i = 0;
        snapshot.forEach(function (childSnapshot) {
            var gameKey = childSnapshot.key;
            console.log(gameKey);

            var update = {};
            update['/queues/' + gameKey + '/queue/' + name] = new Date().getTime();

            firebase.database().ref().update(update).then(function () {
                console.log('Found a game!');

                queue.once('child_removed', function (data) {
                    return console.log("joinGame", data.key, data.val().text, data.val().author);
                });
            });

            i++;
        });

        if (i == 0) {
            return joinGame(name);
        } else if (i == 1) {
            return;
        } else {
            alert('Error occurred!');
            return console.error(`Error occurred: joinGame query size ${i} not equal to 0 or 1`);
        }
    });
}