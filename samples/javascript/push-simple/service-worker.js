{

self.addEventListener('push', function(event) {
  event.waitUntil(
    self.registration.showNotification('sample', {
      body: 'success!!!!',
    })
  );
});

}