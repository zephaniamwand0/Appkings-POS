const functions = require('firebase-functions');
/**
 * https://firebase.google.com/docs/functions/write-firebase-functions
 */
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

//FireBase db calculations for rating average
exports.productProfit = functions.database.ref('/Products/{product_id}/sellingPrice')
    .onWrite((change, context) => {
        let snapshot = change.after;

        let sum = 0;
        snapshot.forEach(child => {
            sum = sum + child.val();
        });
        let productRating = sum / snapshot.numChildren();

        return snapshot.ref.parent.child('profit').set(productRating);
    });