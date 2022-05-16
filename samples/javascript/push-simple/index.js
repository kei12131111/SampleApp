{

// サーバの公開鍵
const vapidPublicKey = 'BDAvo4RGEXpgAY9gUPQGUI3Gt2qr9Cw3IE1yqHprcOHy0jh3odEBObhx5AJqLAxvraTz2sJVvIxT6vBhmqx6jFE';



function OnButtonClick() {
	if(Notification.permission == 'denied'){
		return;
	} else {
		// service workerを登録
		navigator.serviceWorker.register('/service-worker.js')
		      .then(() => {
		        console.log('registered');
		      })
		      .catch(() => {
		        console.log('register failed');
		      });

		// service workerが登録された場合
		navigator.serviceWorker.ready.then(async function(registration) {

			// push serviceへsubscribe
			const pushSubscription = await registration.pushManager.subscribe({
				userVisibleOnly: true,
				applicationServerKey: vapidPublicKey
			});
			
			// PushSubscription をサーバーに送る
		    await fetch("http://localhost:3100/subscribe", {
		      method: "POST",
		      body: JSON.stringify(pushSubscription),
		      headers: {
		        "Content-Type": "application/json"
		      }
		    });
		    console.log("finish send");
		});
	};
};

}