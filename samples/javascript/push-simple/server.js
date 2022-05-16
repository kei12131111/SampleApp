const webPush = require('web-push');
const bodyParser = require('body-parser');
var express = require('express');
var app = express();

var strPushSubscription;
var subscription;


// POST���N�G�X�g��json��body������
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());


// key�����p�B���Ɏg��Ȃ��Ă悢
app.get('/key', function (req, res) {
  res.send(JSON.stringify(webPush.generateVAPIDKeys()));
  res.sendStatus(200);
});


// key����webpush�֋l�ߍ���
webPush.setVapidDetails(
  "mailto:sample@sample.com",
  "BDAvo4RGEXpgAY9gUPQGUI3Gt2qr9Cw3IE1yqHprcOHy0jh3odEBObhx5AJqLAxvraTz2sJVvIxT6vBhmqx6jFE",
  "xyECeKdDAjOJ07lnJZxEjLEYyiI7QDW3yHsOK8z6XpE"
);


// cors�΍�
app.options('/*', function (req, res) {
  res.header('Access-Control-Allow-Origin', '*')
  res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE,OPTIONS')
  res.header(
    'Access-Control-Allow-Headers',
    'Content-Type, Authorization, access_token'
  )
  res.sendStatus(200);
});

// client����subscription���擾����
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
    
    // ����͎g��Ȃ����Apayload�ݒ�ł���
	const payload = JSON.stringify({
	  title: "�v�b�V���ʒm�̃e�X�g"
	});	

	// �ʒm��push service�Ɉ˗�
	webPush.sendNotification(subscription, payload);
	res.sendStatus(201);
});


app.listen(3100);