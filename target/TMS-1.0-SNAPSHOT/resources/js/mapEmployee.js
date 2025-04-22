// Set your Mapbox access token
mapboxgl.accessToken = 'pk.eyJ1Ijoib2xhbCIsImEiOiJjbThidjM5aTgxcDNtMm1zNTh6NjRqeDE3In0.EIobbUYeWUFMarhuxGr_sg';

// Initialize the map centered on a default location
var map = new mapboxgl.Map({
    container: 'mapEmployee', // ID of the map container
    style: 'mapbox://styles/mapbox/streets-v10',
    center: [36.860235, -1.300294], // Default center (longitude, latitude)
    zoom: 10
});

// Add navigation controls (zoom and rotation)
map.addControl(new mapboxgl.NavigationControl());

// When the map loads, add a GeoJSON layer to highlight specific roads
let origin = window.location.origin;

let websocketProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';

// Construct the WebSocket URL
let wsUrl = websocketProtocol + '//' + window.location.host + '/TMS/tracking';
var websocket = new WebSocket(wsUrl);

// Function to send geolocation data via WebSocket
function createCustomMarker() {
    var markerEl = document.createElement('div');
    markerEl.style.backgroundImage = 'url("' + markerImageUrl + '")';
    markerEl.style.width = '40px';
    markerEl.style.height = '40px';
    markerEl.style.backgroundSize = 'cover';
    markerEl.style.cursor = 'pointer'; // Indicates interactivity

    markerEl.addEventListener('click', () => {
        fetch(`/TMS/api/profile/driverDetails`)
                .then(res => {
                    if (!res.ok)
                        throw new Error(res.statusText);
                    return res.json();
                })
                .then(profile => {
                    document.getElementById('modal-username').textContent = profile.username;
                    document.getElementById('modal-email').textContent = profile.email;
                    document.getElementById('modal-phone').textContent = profile.phoneNumber;
                    new bootstrap.Modal(document.getElementById('driverInfoModal')).show();
                })
                .catch(err => {
                    console.error('Error loading profile:', err);
                    alert('Unable to load driver details.');
                });
    });


    return markerEl;
}

function sendGeolocationData(position) {
    var userData = {
        lat: position.coords.latitude,
        lng: position.coords.longitude,
        driverName: window.driverUsername // Optionally, assign a unique identifier
    };

    // Ensure the WebSocket connection is open before sending
    if (websocket.readyState === WebSocket.OPEN) {
        websocket.send(JSON.stringify(userData));
        console.log("Sent geolocation data:", userData);
    } else {
        // Wait for the WebSocket to open before sending
        websocket.addEventListener('open', function () {
            websocket.send(JSON.stringify(userData));
            console.log("Sent geolocation data after connection opened:", userData);
        });
    }
}


// Prompt for device geolocation and update the map accordingly
if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(function (position) {
        var userCoords = [position.coords.longitude, position.coords.latitude];
        // Center the map on the user's current location
        console.log("coordinates", userCoords[0], "latitude ", userCoords[1])
        map.setCenter(userCoords);

        var customMarker = createCustomMarker();
        // Add a marker to indicate the user's location
        new mapboxgl.Marker(customMarker).setLngLat(userCoords).addTo(map);
        sendGeolocationData(position);

    }, function (error) {
        console.error("Error obtaining location: ", error);
    });
} else {
    console.error("Geolocation is not supported by this browser.");
}