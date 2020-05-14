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
var cards = db.collection("cards").doc("cards");
var games = db.collection("games");
var users = db.collection("users");
var emails = db.collection("emails");

var storage = firebase.storage().ref();
var cardThumbnails = storage.child('card_thumbnails')

function addCard() {
    var name = document.getElementById("cardName").value;
    var description = document.getElementById("cardDescription").value;

    var type = $("cardType").val();
    var rarity = $("cardRarity").val();

    var thumbnail = document.getElementById("cardThumbnail").files[0];

    if (!name || type == "" || !description || rarity == "") {
        alert ("You forgot to enter an input for something! Every field is required!");
        return console.log("Not enough parameters given. " + name + description + type + rarity);
    }

    var reader = new FileReader();
    var preview = document.getElementById("cardThumbnailPreview");

    reader.onloadend = function () {
        preview.src = reader.result;
        return console.log(reader.readAsDataURL);
    }

    if (thumbnail) {
        reader.readAsDataURL(thumbnail);
    } else {
        preview.src = "";
        return alert("Card thumbnail is missing!");
    }

    var seed = Math.round(Math.random());
    var HPValues = [100, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600];
    var attackDamageValues = [30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80];
    
    switch (rarity) {
        case "Common":
            var HP = HPValues[seed];
            if (["Person", "Normal"].includes(type)) {
                var attackDamage = attackDamageValues[seed];
            } else {
                var attackDamage = null;
            }
            break;
        case "Normal":
            var HP = HPValues[seed + 2];
            if (["Person", "Normal"].includes(type)) {
                var attackDamage = attackDamageValues[seed + 2];
            } else {
                var attackDamage = null;
            }
            break;
        case "Rare":
            var HP = HPValues[seed + 4];
            if (["Person", "Normal"].includes(type)) {
                var attackDamage = attackDamageValues[seed + 4];
            } else {
                var attackDamage = null;
            }
            break;
        case "Ultra Rare":
            var HP = HPValues[seed + 6];
            if (["Person", "Normal"].includes(type)) {
                var attackDamage = attackDamageValues[seed + 6];
            } else {
                var attackDamage = null;
            }
            break;
        case "Legendary":
            var HP = HPValues[seed + 8];
            if (["Person", "Normal"].includes(type)) {
                var attackDamage = attackDamageValues[seed + 8];
            } else {
                var attackDamage = null;
            }
            break;
        case "Mythical":
            var HP = HPValues[seed + 10];
            if (["Person", "Normal"].includes(type)) {
                var attackDamage = attackDamageValues[seed + 10];
            } else {
                var attackDamage = null;
            }
            break;
    }

    console.log(`${name}, ${HP}, ${type}, ${description}, ${rarity}, 1, ${attackDamage}, 0, 1000`);

    cards.collection(type).doc(name).get().then(function (doc) {
        if (!doc.exists) {
            if (["Person", "Normal"].includes(type)) {
                cards.collection(type).doc(name).set({
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
                    console.log("Successfully added card ${name} of type ${type}!");
                    return alert("Successfully added card ${name} of type ${type}!");
                }).catch(function (e) {
                    console.log(e);
                    return alert("Error occurred! Please contact a developer!");
                });
            } else {
                cards.collection(type).doc(name).set({
                    name: name,
                    HP: HP,
                    type: type,
                    description: description,
                    rarity: rarity,
                    level: 1,
                    XP: 0,
                    xpToLevelUp: 1000
                }).then(function () {
                    console.log("Successfully added card ${name} of type ${type}!");
                    return alert("Successfully added card ${name} of type ${type}!");
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
    emails.doc(name).get().then(function (doc) {
        var uid = doc.data().uid;

        var gameDoc = games.doc();
        gameDoc.set({
            status: "PENDING",
            user1: {
                uid: uid,
                name: name
            }
        }).then(function () {
            console.log(`User ${name} with uid ${uid} created game ${gameDoc.id}.`);
        });
    });
}

function joinGame(gameId, name) {
    emails.doc(name).get().then(function (doc) {
        var uid = doc.data().uid;

        games.doc(gameId).update({
            status: "CHECKING",
            ["queue." + uid]: firebase.firestore.FieldValue.serverTimestamp()
        }).then(function () {
            console.log(`User ${name} with uid ${uid} joined queue for game ${gameId} at ${firebase.firestore.FieldValue.serverTimestamp()}.`);
        });
    });
}