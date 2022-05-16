const webPush = require('web-push');
const bodyParser = require('body-parser');
var express = require('express');
var app = express();

var strPushSubscription;
var subscription;


// POSTリクエストのjsonのbodyを扱う
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());


// key生成用。特に使わなくてよい
app.get('/key', function (req, res) {
  res.send(JSON.stringify(webPush.generateVAPIDKeys()));
  res.sendStatus(200);
});


// key情報をwebpushへ詰め込む
webPush.setVapidDetails(
  "mailto:sample@sample.com",
  "BDAvo4RGEXpgAY9gUPQGUI3Gt2qr9Cw3IE1yqHprcOHy0jh3odEBObhx5AJqLAxvraTz2sJVvIxT6vBhmqx6jFE",
  "xyECeKdDAjOJ07lnJZxEjLEYyiI7QDW3yHsOK8z6XpE"
);


// cors対策
app.options('/*', function (req, res) {
  res.header('Access-Control-Allow-Origin', '*')
  res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE,OPTIONS')
  res.header(
    'Access-Control-Allow-Headers',
    'Content-Type, Authorization, access_token'
  )
  res.sendStatus(200);
});

// clientからsubscriptionを取得する
app.post('/subscribe', function(req, res) {
	res.header('Access-Control-Allow-Origin', '*')
	res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE')
	res.header(
	  'Access-Control-Allow-Headers',
	  'Content-Type, Authorization, access_token'
	)
	
	
	strPushSubscription = JSON.stringify(req.body);
	res.sendStatus(201);
});

app.post('/pushNotification', function(req, res) {
	res.header('Access-Control-Allow-Origin', '*')
	res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE')
	res.header(
	  'Access-Control-Allow-Headers',
	  'Content-Type, Authorization, access_token'
	)
    subscription = req.body;
    
    // 今回は使わないが、payload設定できる
	const payload = JSON.stringify({
	  title: "プッシュ通知のテスト"
	});	

	// 通知をpush serviceに依頼
	webPush.sendNotification(subscription, payload);
	res.sendStatus(201);
});


app.listen(3100);